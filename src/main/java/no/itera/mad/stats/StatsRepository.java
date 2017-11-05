package no.itera.mad.stats;

import java.util.UUID;

import no.itera.mad.stats.model.Statistics;

import org.springframework.data.mongodb.repository.Tailable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public interface StatsRepository extends ReactiveCrudRepository<Statistics, UUID> {

  Flux<Statistics> findAll();

  Mono<Statistics> save(Statistics statistics);

  @Tailable
  Flux<Statistics> findWithTailableCursorBy();
}
