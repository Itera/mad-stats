package no.itera.mad.stats;

import no.itera.mad.stats.model.CreateStatsDto;
import no.itera.mad.stats.model.Health;
import no.itera.mad.stats.model.Result;

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
    System.out.println(statsChannel);
    toJson(createStatsDto).ifOk(System.out::println);

    statsChannel.send(new GenericMessage<>(createStatsDto));
  }

  @GetMapping(value = "/", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
  public Flux<String> stats() {
	  System.out.println(statsChannel);
    return Flux.create(sink -> {
      MessageHandler handler = msg -> {
        System.out.println("Received message");
        toJson(msg).ifOk(System.out::println).orElse((e) -> new RuntimeException(""));
        toJson(msg).ifOk(sink::next).orElse(sink::error);
      };

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
}

