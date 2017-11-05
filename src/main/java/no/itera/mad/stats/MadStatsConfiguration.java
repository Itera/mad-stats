package no.itera.mad.stats;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.SubscribableChannel;

@Configuration
@EnableReactiveMongoRepositories
public class MadStatsConfiguration{

  @Bean()
  public SubscribableChannel statsChannel() {
    return MessageChannels.publishSubscribe().get();
  }

}
