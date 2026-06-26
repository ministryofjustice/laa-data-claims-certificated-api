package uk.gov.justice.laa.data.claims.certificated.api.application.command.updateitem;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.data.claims.certificated.api.application.command.shared.repository.ItemRepository;
import uk.gov.justice.laa.data.claims.certificated.api.domain.ItemEntity;
import uk.gov.justice.laa.data.claims.certificated.api.exception.ItemNotFoundException;

/** Handles the update of an existing item. */
@Slf4j
@RequiredArgsConstructor
@Component
public class UpdateItemHandler {

  private final ItemRepository itemRepository;

  /**
   * Handles the given update item command.
   *
   * @param command the update item command
   */
  @Transactional
  public void handle(UpdateItemCommand command) {
    log.info("Updating item: {}", command.id());
    ItemEntity item =
        itemRepository
            .findById(command.id())
            .orElseThrow(
                () ->
                    new ItemNotFoundException(
                        String.format("No item found with id: %s", command.id())));
    item.setName(command.name());
    item.setDescription(command.description());
    itemRepository.save(item);
    log.info("Updated item: {}", command.id());
  }
}
