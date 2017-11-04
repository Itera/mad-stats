package no.itera.mad.stats;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.channel.MessageChannels;
import org.springframework.messaging.SubscribableChannel;

@Configuration
public class MadStatsConfiguration {


  @Bean()
  public SubscribableChannel statsChannel() {
    return MessageChannels.publishSubscribe().get();
  }

}
