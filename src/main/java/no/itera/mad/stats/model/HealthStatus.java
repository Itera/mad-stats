package no.itera.mad.stats.model;

enum HealthStatus {
  OK("ok"), WARNING("warning"), CRITICAL("critical");

  private final String value;

  HealthStatus(String value) {
    this.value = value;
  }

  public String getValue() {
    return value;
  }
}
