package uk.gov.justice.laa.data.claims.certificated.api.application.query.getitems;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.shared.readmodel.ItemReadModel;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.shared.repository.ItemReadRepository;

/** Handles retrieval of all items. */
@Slf4j
@RequiredArgsConstructor
@Component
public class GetItemsHandler {

  private final ItemReadRepository itemReadRepository;

  /**
   * Handles the given get items query.
   *
   * @param query the get items query
   * @return the list of item read models
   */
  @Transactional(readOnly = true)
  public List<ItemReadModel> handle(GetItemsQuery query) {
    log.info("Retrieving all items");
    return itemReadRepository.findAllProjected();
  }
}
