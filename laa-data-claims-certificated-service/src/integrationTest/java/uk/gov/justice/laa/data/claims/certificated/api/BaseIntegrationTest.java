package uk.gov.justice.laa.data.claims.certificated.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Base class for MockMvc-based integration tests.
 *
 * <p>Centralises the common setup shared by integration tests that exercise the REST API against a
 * real (Testcontainers) PostgreSQL database:
 *
 * <ul>
 *   <li>{@code @SpringBootTest} loading the full application context.
 *   <li>{@code @AutoConfigureMockMvc} providing a configured {@link MockMvc}.
 *   <li>{@code @Import(TestcontainersConfig.class)} starting the PostgreSQL container.
 * </ul>
 *
 * <p>Subclasses only need to add behaviour specific to their scenario, for example
 * {@code @Transactional} for rollback isolation, {@code @TestPropertySource} to override
 * configuration, or {@code @DirtiesContext} to reset shared state between methods.
 */
@SpringBootTest(classes = DataClaimsCertificatedApiApplication.class)
@AutoConfigureMockMvc
@Import(TestcontainersConfig.class)
public abstract class BaseIntegrationTest {

  @Autowired protected MockMvc mockMvc;
}
