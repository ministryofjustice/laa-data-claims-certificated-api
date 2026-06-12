package uk.gov.justice.laa.data.claims.certificated.api.controller;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

/**
 * Base controller providing shared behaviour for all API controllers, such as the rate limiter
 * fallback handling. Extend this class to reuse the {@link #genericFallback(RequestNotPermitted)}
 * fallback method referenced by {@code @RateLimiter} annotations.
 */
public abstract class BaseApiController {

  /**
   * Fallback method invoked when the rate limiter rejects a request.
   *
   * @param e the exception thrown when the request is not permitted
   * @return a {@code 429 Too Many Requests} response
   */
  protected ResponseEntity<ProblemDetail> genericFallback(RequestNotPermitted e) {
    return get429Response();
  }

  /**
   * Builds a standard {@code 429 Too Many Requests} response.
   *
   * @return a {@code 429 Too Many Requests} response
   */
  protected ResponseEntity<ProblemDetail> get429Response() {
    ProblemDetail problemDetail =
        ProblemDetail.forStatusAndDetail(
            HttpStatus.TOO_MANY_REQUESTS, "Rate limit exceeded. Please try again later.");
    problemDetail.setTitle("Too Many Requests");
    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }
}
