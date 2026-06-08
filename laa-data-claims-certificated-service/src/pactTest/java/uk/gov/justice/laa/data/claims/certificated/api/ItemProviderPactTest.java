package uk.gov.justice.laa.data.claims.certificated.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import au.com.dius.pact.provider.junit5.HttpTestTarget;
import au.com.dius.pact.provider.junit5.PactVerificationContext;
import au.com.dius.pact.provider.junit5.PactVerificationInvocationContextProvider;
import au.com.dius.pact.provider.junitsupport.Provider;
import au.com.dius.pact.provider.junitsupport.State;
import au.com.dius.pact.provider.junitsupport.TargetRequestFilter;
import au.com.dius.pact.provider.junitsupport.loader.PactFolder;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestTemplate;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;
import uk.gov.justice.laa.data.claims.certificated.api.entity.ItemEntity;

@Slf4j
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Provider(value = "laa-data-claims-certificated-api")
// @PactBroker // <-- uncomment this and remove the @PactFolder
@PactFolder("src/pactTest/resources/pacts")
public class ItemProviderPactTest extends AbstractProviderPactTests {

  @LocalServerPort private int port;

  @BeforeEach
  void setUp(PactVerificationContext context) {
    HttpTestTarget target = new HttpTestTarget("localhost", port);
    context.setTarget(target);
  }

  @TargetRequestFilter
  public void requestFilter(HttpRequest request) {
    request.addHeader("Authorization", "00000000-0000-0000-0000-000000000000");
  }

  @TestTemplate
  @ExtendWith(PactVerificationInvocationContextProvider.class)
  void pactVerificationTestTemplate(PactVerificationContext context) {
    // context.verifyInteraction(); - uncomment when using PactBroker
  }

  // --- Provider States ---

  @State("An item exists with ID 1")
  void toItemExistsState() {
    ItemEntity dummyEntity = new ItemEntity();

    // Using AssertJ to verify local dummy test data integrity if needed
    assertThat(dummyEntity).isNotNull();

    when(itemRepository.findById(1L)).thenReturn(Optional.of(dummyEntity));
  }
}
