package uk.gov.justice.laa.data.claims.certificated.api.application.query.getitembyid;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.shared.readmodel.ItemReadModel;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.shared.repository.ItemReadRepository;
import uk.gov.justice.laa.data.claims.certificated.api.exception.ItemNotFoundException;

/** Handles retrieval of a single item by identifier. */
@Slf4j
@RequiredArgsConstructor
@Component
public class GetItemByIdHandler {

  private final ItemReadRepository itemReadRepository;

  /**
   * Handles the given get item by id query.
   *
   * @param query the get item by id query
   * @return the item read model
   */
  @Transactional(readOnly = true)
  public ItemReadModel handle(GetItemByIdQuery query) {
    log.info("Retrieving item: {}", query.id());
    return itemReadRepository
        .findProjectedById(query.id())
        .orElseThrow(
            () ->
                new ItemNotFoundException(String.format("No item found with id: %s", query.id())));
  }
}
