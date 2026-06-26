package uk.gov.justice.laa.data.claims.certificated.api.controller.query.mapper;

import org.mapstruct.Mapper;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.shared.readmodel.ItemReadModel;
import uk.gov.justice.laa.data.claims.certificated.api.model.Item;

/** Read-side mapper: maps ItemReadModel projections to the generated API response model. */
@Mapper(componentModel = "spring")
public interface GetItemResponseMapper {

  /**
   * Maps the given item read model to the generated API Item model.
   *
   * @param readModel the item read model projection
   * @return the API Item response model
   */
  Item toApiModel(ItemReadModel readModel);
}
