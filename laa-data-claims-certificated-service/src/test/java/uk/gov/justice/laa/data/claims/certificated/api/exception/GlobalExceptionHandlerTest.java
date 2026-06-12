package uk.gov.justice.laa.data.claims.certificated.api.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.ServletWebRequest;

@DisplayName("GlobalExceptionHandler")
class GlobalExceptionHandlerTest {

  GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

  @Nested
  @DisplayName("ItemNotFoundException handling")
  class ItemNotFound {

    @Test
    @DisplayName("returns 404 Not Found with the error details")
    void handleItemNotFoundReturnsNotFoundStatusAndErrorMessage() {
      MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/items/99");
      ResponseEntity<Object> result =
          globalExceptionHandler.handleItemNotFound(
              new ItemNotFoundException("Item not found"), new ServletWebRequest(request));

      assertThat(result).isNotNull();
      assertThat(result.getStatusCode()).isEqualTo(NOT_FOUND);
      assertThat(result.getBody()).isInstanceOf(ProblemDetail.class);
      ProblemDetail body = (ProblemDetail) result.getBody();
      assertThat(body.getDetail()).isEqualTo("Item not found");
      assertThat(body.getInstance()).hasToString("/api/v1/items/99");
      assertThat(body.getType()).hasToString("about:blank");
    }
  }

  @Nested
  @DisplayName("Generic exception handling")
  class GenericException {

    @Test
    @DisplayName("returns 500 Internal Server Error with a generic message")
    void handleGenericExceptionReturnsInternalServerErrorStatusAndErrorMessage() {
      ResponseEntity<String> result =
          globalExceptionHandler.handleGenericException(
              new RuntimeException("Something went wrong"));

      assertThat(result).isNotNull();
      assertThat(result.getStatusCode()).isEqualTo(INTERNAL_SERVER_ERROR);
      assertThat(result.getBody()).isNotNull();
      assertThat(result.getBody()).isEqualTo("An unexpected application error has occurred.");
    }
  }
}
