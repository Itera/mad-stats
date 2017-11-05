package no.itera.mad.stats.model;

import java.util.Date;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateStatsDto {

  private final boolean persist;
  private final String service;
  private final String topic;
  private final String key;
  private final String value;

  @JsonCreator
  public CreateStatsDto(
      @JsonProperty("persist") boolean persist,
      @JsonProperty("service") String service,
      @JsonProperty("topic") String topic,
      @JsonProperty("key") String key,
      @JsonProperty("value") String value
  ) {
    this.persist = persist;
    this.service = service;
    this.key = key;
    this.topic = topic;
    this.value = value;
  }

  public boolean isPersist() {
    return persist;
  }

  public String getService() {
    return service;
  }

  public String getTopic() {
    return topic;
  }

  public String getKey() {
    return key;
  }

  public String getValue() {
    return value;
  }
}
