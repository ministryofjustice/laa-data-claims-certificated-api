package uk.gov.justice.laa.data.claims.certificated.api.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.github.resilience4j.ratelimiter.RateLimiterConfig;
import io.github.resilience4j.ratelimiter.RateLimiterRegistry;
import io.github.resilience4j.springboot3.ratelimiter.autoconfigure.RateLimiterAutoConfiguration;
import java.time.Duration;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import uk.gov.justice.laa.data.claims.certificated.api.constants.RateLimiterNames;

/**
 * Unit test verifying that the resilience4j rate limiter instances declared in {@code
 * application.yml} are bound with the expected thresholds. It loads the real configuration file via
 * {@link ConfigDataApplicationContextInitializer} together with the resilience4j
 * auto-configuration, without starting the full application context (no database required).
 */
@DisplayName("Rate limiter configuration")
class RateLimiterConfigurationTest {

  private static final List<String> ITEM_RATE_LIMITERS =
      List.of(
          RateLimiterNames.GET_ITEMS,
          RateLimiterNames.GET_ITEM,
          RateLimiterNames.CREATE_ITEM,
          RateLimiterNames.UPDATE_ITEM,
          RateLimiterNames.DELETE_ITEM);

  private final ApplicationContextRunner contextRunner =
      new ApplicationContextRunner()
          .withInitializer(new ConfigDataApplicationContextInitializer())
          .withConfiguration(AutoConfigurations.of(RateLimiterAutoConfiguration.class));

  @Test
  @DisplayName("creates a single RateLimiterRegistry bean")
  void rateLimiterRegistryIsCreated() {
    contextRunner.run(context -> assertThat(context).hasSingleBean(RateLimiterRegistry.class));
  }

  @Test
  @DisplayName("configures every item rate limiter with the expected thresholds")
  void allItemRateLimitersAreConfiguredWithExpectedThresholds() {
    contextRunner.run(
        context -> {
          RateLimiterRegistry registry = context.getBean(RateLimiterRegistry.class);

          for (String name : ITEM_RATE_LIMITERS) {
            RateLimiterConfig config = registry.rateLimiter(name).getRateLimiterConfig();

            assertThat(config.getLimitForPeriod()).as("limitForPeriod for %s", name).isEqualTo(10);
            assertThat(config.getLimitRefreshPeriod())
                .as("limitRefreshPeriod for %s", name)
                .isEqualTo(Duration.ofSeconds(1));
            assertThat(config.getTimeoutDuration())
                .as("timeoutDuration for %s", name)
                .isEqualTo(Duration.ZERO);
          }
        });
  }

  @Test
  @DisplayName("gives each item endpoint its own rate limiter instance")
  void everyItemEndpointHasItsOwnRateLimiterInstance() {
    contextRunner.run(
        context -> {
          RateLimiterRegistry registry = context.getBean(RateLimiterRegistry.class);

          // Each operation must resolve to a distinct rate limiter so heavy traffic on one
          // endpoint does not throttle the others.
          assertThat(ITEM_RATE_LIMITERS.stream().map(registry::rateLimiter).distinct().count())
              .isEqualTo(ITEM_RATE_LIMITERS.size());
        });
  }
}
