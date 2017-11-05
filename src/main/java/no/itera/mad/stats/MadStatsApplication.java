package no.itera.mad.stats;

import static java.lang.String.format;

import java.util.function.Consumer;

import no.itera.mad.stats.model.CreateStatsDto;
import no.itera.mad.stats.model.Health;
import no.itera.mad.stats.model.Result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
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

@SpringBootApplication
@RestController
public class MadStatsApplication {

  private static final Logger logger = LoggerFactory.getLogger(MadStatsApplication.class);


  private final SubscribableChannel statsChannel;

  @Autowired
  public MadStatsApplication(SubscribableChannel statsChannel) {
    this.statsChannel = statsChannel;
  }

  public static void main(String[] args) {
    SpringApplication.run(MadStatsApplication.class, args);
  }

  @GetMapping("/health")
  public Health health() {
    return Health.ok();
  }

  @PostMapping(value = "/", consumes = MediaType.APPLICATION_JSON_VALUE)
  public void stats(@RequestBody CreateStatsDto createStatsDto) {
    logger.info("Received message from {}", createStatsDto.getService());
    statsChannel.send(new GenericMessage<>(createStatsDto));
  }

  @GetMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> stats() {
    return Flux.create(sink -> {
      MessageHandler handler = msg ->
        toJson(msg)
            .ifOk(info("Stats sent: {}", sink::next))
            .orElse(err(format("Could not serialize msg to json: %s", msg), sink::error));


      sink.onCancel(() -> statsChannel.unsubscribe(handler));
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

