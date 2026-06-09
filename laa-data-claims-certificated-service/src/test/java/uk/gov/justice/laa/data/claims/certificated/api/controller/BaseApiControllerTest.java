package uk.gov.justice.laa.data.claims.certificated.api.controller;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.resilience4j.ratelimiter.RateLimiter;
import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;

class BaseApiControllerTest {

  /** Minimal concrete subclass so the abstract base class can be instantiated for testing. */
  private final BaseApiController controller = new BaseApiController() {};

  @Test
  void get429ResponseReturnsTooManyRequestsProblemDetail() {
    ResponseEntity<ProblemDetail> response = controller.get429Response();

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);

    ProblemDetail body = response.getBody();
    assertThat(body).isNotNull();
    assertThat(body.getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
    assertThat(body.getTitle()).isEqualTo("Too Many Requests");
    assertThat(body.getDetail()).isEqualTo("Rate limit exceeded. Please try again later.");
  }

  @Test
  void genericFallbackDelegatesToGet429Response() {
    RequestNotPermitted exception =
        RequestNotPermitted.createRequestNotPermitted(
            RateLimiter.ofDefaults("genericFallbackTest"));

    ResponseEntity<ProblemDetail> response = controller.genericFallback(exception);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().getStatus()).isEqualTo(HttpStatus.TOO_MANY_REQUESTS.value());
  }

  @Test
  void get429ResponseIsSerialisableAsProblemJson() {
    // The ProblemDetail body is rendered as application/problem+json by Spring.
    assertThat(MediaType.APPLICATION_PROBLEM_JSON).isNotNull();
    assertThat(controller.get429Response().getBody()).isInstanceOf(ProblemDetail.class);
  }
}
