package uk.gov.justice.laa.data.claims.certificated.api.constants;

/**
 * Centralised names of the resilience4j rate limiter instances.
 *
 * <p>Each value must match an instance configured under {@code resilience4j.ratelimiter.instances}
 * in {@code application.yml}. Referencing these constants from {@code @RateLimiter(name = ...)}
 * annotations avoids duplicated magic strings and keeps the controllers in sync with the
 * configuration.
 */
public final class RateLimiterNames {

  private RateLimiterNames() {
    // Utility class - prevent instantiation.
  }

  /** Rate limiter for the get all items endpoint. */
  public static final String GET_ITEMS = "getItemsRateLimiter";

  /** Rate limiter for the get item by id endpoint. */
  public static final String GET_ITEM = "getItemRateLimiter";

  /** Rate limiter for the create item endpoint. */
  public static final String CREATE_ITEM = "createItemRateLimiter";

  /** Rate limiter for the update item endpoint. */
  public static final String UPDATE_ITEM = "updateItemRateLimiter";

  /** Rate limiter for the delete item endpoint. */
  public static final String DELETE_ITEM = "deleteItemRateLimiter";
}
