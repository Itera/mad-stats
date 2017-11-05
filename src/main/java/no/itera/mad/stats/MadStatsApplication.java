package no.itera.mad.stats;

import static java.lang.String.format;

import java.util.function.Consumer;

import no.itera.mad.stats.model.CreateStatsDto;
import no.itera.mad.stats.model.Health;
import no.itera.mad.stats.model.Result;
import no.itera.mad.stats.model.Statistics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.http.MediaType;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.SubscribableChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@SpringBootApplication
@RestController
public class MadStatsApplication {

  private static final Logger logger = LoggerFactory.getLogger(MadStatsApplication.class);

  private final StatsRepository statsRepository;
  private final SubscribableChannel statsChannel;

  @Autowired
  public MadStatsApplication(
      StatsRepository statsRepository,
      SubscribableChannel statsChannel,
      ReactiveMongoOperations operations
  ) {
    this.statsRepository = statsRepository;
    this.statsChannel = statsChannel;

    operations.collectionExists(Statistics.class)
        .filter(exists -> !exists)
        .flatMap(exists -> operations.createCollection(
            Statistics.class,
            CollectionOptions.empty()
                .size(1024 * 1024 * 10)
                .maxDocuments(1024)
                .capped())
        )
        .then()
        .block();
  }

  public static void main(String[] args) {
    SpringApplication.run(MadStatsApplication.class, args);
  }

  @GetMapping("/health")
  public Health health() {
    return Health.ok();
  }

  @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
  public Mono<Statistics> stats(@RequestBody CreateStatsDto createStatsDto) {
    Statistics statistics = Statistics.fromApi(createStatsDto);

    if (!createStatsDto.isPersist()) {
      logger.info("Received non-persistent message from {}", createStatsDto.getService());
      if (statsChannel.send(new GenericMessage<>(statistics))) {
        return Mono.just(statistics);
      } else {
        return Mono.empty();
      }
    } else {
      logger.info("Received persistent message from {}", createStatsDto.getService());
      return statsRepository.save(statistics);
    }
  }

  @GetMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> stats() {
    return Flux.create(sink -> {

      // Closure which will dispatch the json stat to client
      Consumer<Statistics> consumer = statistics -> toJson(statistics)
          .ifOk(info("Stats sent: {}", sink::next))
          .orElse(err(format("Could not serialize msg to json: %s", statistics), sink::error));

      // Subscribe to database updates
      statsRepository.findWithTailableCursorBy().subscribe(consumer);

      // Subscribe to non-persisted changes
      MessageHandler handler = msg -> consumer.accept((Statistics)msg.getPayload());
      sink.onDispose(() -> statsChannel.unsubscribe(handler));
      statsChannel.subscribe(handler);
    });
  }

  private static Result<String> toJson(Object value) {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return Result.ok(mapper.writeValueAsString(value));
    } catch (JsonProcessingException e) {
      return Result.err(e);
    }
  }

  private static <T> Consumer<T> info(String message, Consumer<T> next) {
    return v -> {
        logger.info(message, v);
        next.accept(v);
    };
  }

  private static Consumer<Throwable> err(String reason, Consumer<Throwable> next) {
    return v -> {
      logger.error(reason, v);
      next.accept(v);
    };
  }
}

