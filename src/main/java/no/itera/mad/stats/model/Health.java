package no.itera.mad.stats.model;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static java.util.Collections.unmodifiableList;

import java.util.List;

public class Health {

  private final HealthStatus status;
  private final List<HealthProblem> problems;

  private Health(HealthStatus status, List<HealthProblem> problems) {
    this.status = status;
    this.problems = unmodifiableList(problems);
  }

  public static Health ok() {
    return new Health(HealthStatus.OK, emptyList());
  }

  public static Health warning(String reason) {
    return new Health(HealthStatus.WARNING, singletonList(new HealthProblem(reason)));
  }

  public static Health err(String reason) {
    return new Health(HealthStatus.CRITICAL, singletonList(new HealthProblem(reason)));
  }

  public String getStatus() {
    return status.getValue();
  }

  public List<HealthProblem> getProblems() {
    return problems;
  }
}
