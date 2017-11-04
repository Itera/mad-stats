package no.itera.mad.stats.model;

class HealthProblem {

  private final String problem;

  HealthProblem(String reason) {
    this.problem = reason;
  }

  public String getProblem() {
    return problem;
  }
}
