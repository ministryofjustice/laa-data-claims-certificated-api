package uk.gov.justice.laa.data.claims.certificated.api.application.query.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.GetItemByIdQuery;
import uk.gov.justice.laa.data.claims.certificated.api.exception.ItemNotFoundException;
import uk.gov.justice.laa.data.claims.certificated.api.readmodel.repository.ItemViewRepository;
import uk.gov.justice.laa.data.claims.certificated.api.readmodel.view.ItemView;

/** Handles retrieval of a single item by identifier. */
@Slf4j
@RequiredArgsConstructor
@Component
public class GetItemByIdHandler {

  private final ItemViewRepository itemViewRepository;

  /**
   * Handles the given get item by id query.
   *
   * @param query the get item by id query
   * @return the item view
   */
  @Transactional(readOnly = true)
  public ItemView handle(GetItemByIdQuery query) {
    log.info("Retrieving item: {}", query.id());
    return itemViewRepository
        .findProjectedById(query.id())
        .orElseThrow(
            () ->
                new ItemNotFoundException(String.format("No item found with id: %s", query.id())));
  }
}
