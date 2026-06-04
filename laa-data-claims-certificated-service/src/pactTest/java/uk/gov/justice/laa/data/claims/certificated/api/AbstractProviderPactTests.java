package uk.gov.justice.laa.data.claims.certificated.api;

import jakarta.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.actuate.endpoint.web.PathMappedEndpoints;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import uk.gov.justice.laa.data.claims.certificated.api.repository.ItemRepository;

@EnableAutoConfiguration(
    exclude = {
      DataSourceAutoConfiguration.class
    })
@TestPropertySource(
    properties = {
      "laa.springboot.starter.auth.authentication-header=Authorization",
    })
public class AbstractProviderPactTests {

  @BeforeAll
  static void setupPactProperties() {
    System.setProperty("pactbroker.host", "localhost");
    System.setProperty("pactbroker.port", "9292");
    System.setProperty("pactbroker.scheme", "http");
    System.setProperty("laa.springboot.starter.exports.enabled", "true");
  }

  @MockitoBean protected ItemRepository itemRepository;

  // Mocked various DB beans to allow application to run properly without dependencies
  @MockitoBean protected Flyway flyway;
  @MockitoBean protected EntityManagerFactory entityManagerFactory;
  @MockitoBean protected PathMappedEndpoints pathMappedEndpoints;
  @MockitoBean protected DataSource dataSource;
}
