package uk.gov.justice.laa.data.claims.certificated.api.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.data.claims.certificated.api.api.ItemsQueryApi;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.GetItemByIdQuery;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.GetItemsQuery;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.handler.GetItemByIdHandler;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.handler.GetItemsHandler;
import uk.gov.justice.laa.data.claims.certificated.api.model.Item;
import uk.gov.justice.laa.data.claims.certificated.api.readmodel.mapper.ItemViewMapper;
import uk.gov.justice.laa.data.claims.certificated.api.readmodel.view.ItemView;

/** Controller handling read (query) operations on items. */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemQueryController implements ItemsQueryApi {

  private final GetItemsHandler getItemsHandler;
  private final GetItemByIdHandler getItemByIdHandler;
  private final ItemViewMapper itemViewMapper;

  @Override
  public ResponseEntity<List<Item>> getItems() {
    log.info("Getting all items");
    List<ItemView> views = getItemsHandler.handle(new GetItemsQuery());
    return ResponseEntity.ok(views.stream().map(itemViewMapper::toApiModel).toList());
  }

  @Override
  public ResponseEntity<Item> getItemById(Long id) {
    log.info("Getting item {}", id);
    ItemView view = getItemByIdHandler.handle(new GetItemByIdQuery(id));
    return ResponseEntity.ok(itemViewMapper.toApiModel(view));
  }
}
