package uk.gov.justice.laa.data.claims.certificated.api.controller.query;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.justice.laa.data.claims.certificated.api.api.ItemsQueryApi;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.getitembyid.GetItemByIdHandler;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.getitembyid.GetItemByIdQuery;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.getitems.GetItemsHandler;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.getitems.GetItemsQuery;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.shared.readmodel.ItemReadModel;
import uk.gov.justice.laa.data.claims.certificated.api.controller.query.mapper.GetItemResponseMapper;
import uk.gov.justice.laa.data.claims.certificated.api.model.Item;

/** Controller handling read (query) operations on items. */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ItemQueryController implements ItemsQueryApi {

  private final GetItemsHandler getItemsHandler;
  private final GetItemByIdHandler getItemByIdHandler;
  private final GetItemResponseMapper getItemResponseMapper;

  @Override
  public ResponseEntity<List<Item>> getItems() {
    log.info("Getting all items");
    List<ItemReadModel> readModels = getItemsHandler.handle(new GetItemsQuery());
    return ResponseEntity.ok(readModels.stream().map(getItemResponseMapper::toApiModel).toList());
  }

  @Override
  public ResponseEntity<Item> getItemById(Long id) {
    log.info("Getting item {}", id);
    ItemReadModel readModel = getItemByIdHandler.handle(new GetItemByIdQuery(id));
    return ResponseEntity.ok(getItemResponseMapper.toApiModel(readModel));
  }
}
