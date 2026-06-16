package uk.gov.justice.laa.data.claims.certificated.api.application.command.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.data.claims.certificated.api.application.command.DeleteItemCommand;
import uk.gov.justice.laa.data.claims.certificated.api.domain.repository.ItemRepository;
import uk.gov.justice.laa.data.claims.certificated.api.exception.ItemNotFoundException;

/** Handles the deletion of an existing item. */
@Slf4j
@RequiredArgsConstructor
@Component
public class DeleteItemHandler {

  private final ItemRepository itemRepository;

  /**
   * Handles the given delete item command.
   *
   * @param command the delete item command
   */
  @Transactional
  public void handle(DeleteItemCommand command) {
    log.info("Deleting item: {}", command.id());
    if (!itemRepository.existsById(command.id())) {
      throw new ItemNotFoundException(String.format("No item found with id: %s", command.id()));
    }
    itemRepository.deleteById(command.id());
    log.info("Deleted item: {}", command.id());
  }
}
