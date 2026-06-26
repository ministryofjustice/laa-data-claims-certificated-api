package uk.gov.justice.laa.data.claims.certificated.api.controller.command;

import java.net.URI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.justice.laa.data.claims.certificated.api.api.ItemsCommandApi;
import uk.gov.justice.laa.data.claims.certificated.api.application.command.createitem.CreateItemCommand;
import uk.gov.justice.laa.data.claims.certificated.api.application.command.createitem.CreateItemHandler;
import uk.gov.justice.laa.data.claims.certificated.api.application.command.deleteitem.DeleteItemCommand;
import uk.gov.justice.laa.data.claims.certificated.api.application.command.deleteitem.DeleteItemHandler;
import uk.gov.justice.laa.data.claims.certificated.api.application.command.updateitem.UpdateItemCommand;
import uk.gov.justice.laa.data.claims.certificated.api.application.command.updateitem.UpdateItemHandler;
import uk.gov.justice.laa.data.claims.certificated.api.model.ItemRequestBody;

/** Controller handling write (command) operations on items. */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemCommandController implements ItemsCommandApi {

  private final CreateItemHandler createItemHandler;
  private final UpdateItemHandler updateItemHandler;
  private final DeleteItemHandler deleteItemHandler;

  @Override
  public ResponseEntity<Void> createItem(@RequestBody ItemRequestBody itemRequestBody) {
    log.info("Creating item {}", itemRequestBody);
    Long id =
        createItemHandler.handle(
            new CreateItemCommand(itemRequestBody.getName(), itemRequestBody.getDescription()));
    URI uri =
        ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(id).toUri();
    return ResponseEntity.created(uri).build();
  }

  @Override
  public ResponseEntity<Void> updateItem(Long id, ItemRequestBody itemRequestBody) {
    log.info("Updating item {}", id);
    updateItemHandler.handle(
        new UpdateItemCommand(id, itemRequestBody.getName(), itemRequestBody.getDescription()));
    return ResponseEntity.noContent().build();
  }

  @Override
  public ResponseEntity<Void> deleteItem(Long id) {
    log.info("Deleting item {}", id);
    deleteItemHandler.handle(new DeleteItemCommand(id));
    return ResponseEntity.noContent().build();
  }
}
