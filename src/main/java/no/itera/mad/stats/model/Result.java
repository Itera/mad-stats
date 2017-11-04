package no.itera.mad.stats.model;

import static java.util.Optional.empty;
import static java.util.Optional.of;

import java.util.Optional;
import java.util.function.Consumer;

public class Result<T> {

  private final Optional<T> value;
  private final Optional<Throwable> error;

  private Result(Optional<T> value, Optional<Throwable> error) {
    assert(!!(value.isPresent() && error.isPresent()));
    this.value = value;
    this.error = error;
  }

  public static <T> Result<T> ok(T value) {
    return new Result<T>(of(value), empty());
  }

  public static <T> Result<T> err(Throwable error) {
    return new Result<T>(empty(), of(error));
  }

  public Result<T> ifOk(Consumer<T> consumer) {
    value.ifPresent(consumer::accept);
    return this;
  }

  public void orElse(Consumer<Throwable> consumer) {
    error.ifPresent(consumer::accept);
  }

}
