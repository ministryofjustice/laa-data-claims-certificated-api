package uk.gov.justice.laa.data.claims.certificated.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import uk.gov.justice.laa.data.claims.certificated.api.BaseIntegrationTest;

/**
 * Integration tests that simulate burst traffic against a rate limited endpoint and assert that the
 * configured limit is enforced with HTTP 429 (and an RFC 9457 {@code application/problem+json}
 * body) once the limit is exceeded.
 *
 * <p>The {@code getItemsRateLimiter} is overridden with a small, deterministic limit and a long
 * refresh window so the assertions are stable: only {@link #LIMIT} requests are permitted within
 * the window, and {@code timeoutDuration=0} means excess requests are rejected immediately rather
 * than waiting for a permit.
 *
 * <p>{@link DirtiesContext} rebuilds the context (and therefore resets the rate limiter permits)
 * before each test method so the two scenarios do not interfere with one another.
 */
@TestPropertySource(
    properties = {
      "resilience4j.ratelimiter.instances.getItemsRateLimiter.limitForPeriod=3",
      "resilience4j.ratelimiter.instances.getItemsRateLimiter.limitRefreshPeriod=1m",
      "resilience4j.ratelimiter.instances.getItemsRateLimiter.timeoutDuration=0"
    })
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@DisplayName("Rate limiter burst traffic")
class RateLimiterBurstIntegrationTest extends BaseIntegrationTest {

  private static final int LIMIT = 3;
  private static final String ITEMS_URL = "/api/v1/items";

  @Test
  @DisplayName("allows requests up to the limit then returns 429 for the next request")
  void sequentialBurstAllowsUpToLimitThenReturns429() throws Exception {
    // Requests within the limit succeed.
    for (int i = 0; i < LIMIT; i++) {
      mockMvc.perform(get(ITEMS_URL)).andExpect(status().isOk());
    }

    // The next request exceeds the limit and is rejected with a problem+json 429.
    mockMvc
        .perform(get(ITEMS_URL))
        .andExpect(status().isTooManyRequests())
        .andExpect(content().contentType(MediaType.APPLICATION_PROBLEM_JSON))
        .andExpect(jsonPath("$.status").value(HttpStatus.TOO_MANY_REQUESTS.value()))
        .andExpect(jsonPath("$.title").value("Too Many Requests"))
        .andExpect(jsonPath("$.detail").value("Rate limit exceeded. Please try again later."));
  }

  @Test
  @DisplayName("grants only the configured number of permits and rejects the rest with 429")
  void concurrentBurstGrantsOnlyLimitPermitsAndRejectsTheRestWith429() throws Exception {
    int totalRequests = 12;
    ExecutorService pool = Executors.newFixedThreadPool(totalRequests);
    CountDownLatch ready = new CountDownLatch(totalRequests);
    CountDownLatch start = new CountDownLatch(1);
    AtomicInteger okCount = new AtomicInteger();
    AtomicInteger tooManyCount = new AtomicInteger();

    List<Future<Integer>> futures = new ArrayList<>();
    for (int i = 0; i < totalRequests; i++) {
      Callable<Integer> task =
          () -> {
            ready.countDown();
            start.await();
            int statusCode = mockMvc.perform(get(ITEMS_URL)).andReturn().getResponse().getStatus();
            if (statusCode == HttpStatus.OK.value()) {
              okCount.incrementAndGet();
            } else if (statusCode == HttpStatus.TOO_MANY_REQUESTS.value()) {
              tooManyCount.incrementAndGet();
            }
            return statusCode;
          };
      futures.add(pool.submit(task));
    }

    // Release all threads at once to simulate a genuine burst.
    assertThat(ready.await(10, TimeUnit.SECONDS))
        .as("all threads reached the start latch")
        .isTrue();
    start.countDown();

    try {
      for (Future<Integer> future : futures) {
        future.get();
      }
    } finally {
      pool.shutdownNow();
      assertThat(pool.awaitTermination(10, TimeUnit.SECONDS)).isTrue();
    }

    assertThat(okCount.get()).as("permitted requests").isEqualTo(LIMIT);
    assertThat(tooManyCount.get()).as("rejected requests").isEqualTo(totalRequests - LIMIT);
  }
}
