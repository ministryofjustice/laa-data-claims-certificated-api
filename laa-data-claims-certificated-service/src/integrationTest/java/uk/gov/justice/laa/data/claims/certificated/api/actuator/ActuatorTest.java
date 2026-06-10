package uk.gov.justice.laa.data.claims.certificated.api.actuator;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;
import uk.gov.justice.laa.data.claims.certificated.api.TestcontainersConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@Import(TestcontainersConfig.class)
@TestPropertySource(
    properties = {
      "management.endpoints.web.exposure.include=health",
      "management.server.port=0",
    })
class ActuatorTest {

  @Value("${local.management.port}")
  private int managementPort;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  void actuatorHealthEndpointShouldReturnUp() {
    ResponseEntity<String> result =
        restTemplate.getForEntity(
            "http://localhost:" + managementPort + "/actuator/health", String.class);

    assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
    assertThat(result.getBody()).contains("\"status\":\"UP\"");
  }
}
