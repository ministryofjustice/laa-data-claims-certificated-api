package uk.gov.justice.laa.data.claims.certificated.api.application.query.handler;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.GetItemsQuery;
import uk.gov.justice.laa.data.claims.certificated.api.readmodel.repository.ItemViewRepository;
import uk.gov.justice.laa.data.claims.certificated.api.readmodel.view.ItemView;

/** Handles retrieval of all items. */
@Slf4j
@RequiredArgsConstructor
@Component
public class GetItemsHandler {

  private final ItemViewRepository itemViewRepository;

  /**
   * Handles the given get items query.
   *
   * @param query the get items query
   * @return the list of item views
   */
  @Transactional(readOnly = true)
  public List<ItemView> handle(GetItemsQuery query) {
    log.info("Retrieving all items");
    return itemViewRepository.findAllProjected();
  }
}
