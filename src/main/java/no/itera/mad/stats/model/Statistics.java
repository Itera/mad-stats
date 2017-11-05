package no.itera.mad.stats.model;

import java.util.Date;
import java.util.UUID;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
public class Statistics {

  private final @Id String uuid;
  private final String timestamp;
  private final String service;
  private final String topic;
  private final String key;
  private final String value;

  public Statistics(String service, String topic, String key, String value) {
    this.uuid = UUID.randomUUID().toString();
    this.timestamp = new Date().toString();
    this.service = service;
    this.topic = topic;
    this.key = key;
    this.value = value;
  }

  public static Statistics fromApi(CreateStatsDto dto) {
    return new Statistics(
        dto.getService(),
        dto.getTopic(),
        dto.getKey(),
        dto.getValue()
    );
  }

  public String getUuid() {
    return uuid;
  }

  public String getTimestamp() {
    return timestamp;
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
