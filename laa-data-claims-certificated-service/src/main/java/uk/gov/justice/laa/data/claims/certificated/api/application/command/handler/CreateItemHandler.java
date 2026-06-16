package uk.gov.justice.laa.data.claims.certificated.api.application.command.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.data.claims.certificated.api.application.command.CreateItemCommand;
import uk.gov.justice.laa.data.claims.certificated.api.domain.entity.ItemEntity;
import uk.gov.justice.laa.data.claims.certificated.api.domain.repository.ItemRepository;

/** Handles the creation of a new item. */
@Slf4j
@RequiredArgsConstructor
@Component
public class CreateItemHandler {

  private final ItemRepository itemRepository;

  /**
   * Handles the given create item command.
   *
   * @param command the create item command
   * @return the id of the created item
   */
  @Transactional
  public Long handle(CreateItemCommand command) {
    log.info("Creating item: {}", command.name());
    ItemEntity entity = new ItemEntity();
    entity.setName(command.name());
    entity.setDescription(command.description());
    ItemEntity saved = itemRepository.save(entity);
    log.info("Created item with id: {}", saved.getId());
    return saved.getId();
  }
}
