package uk.gov.justice.laa.data.claims.certificated.api.readmodel.mapper;

import org.mapstruct.Mapper;
import uk.gov.justice.laa.data.claims.certificated.api.model.Item;
import uk.gov.justice.laa.data.claims.certificated.api.readmodel.view.ItemView;

/** Read-side mapper: maps ItemView projections to the generated API response model. */
@Mapper(componentModel = "spring")
public interface ItemViewMapper {

  /**
   * Maps the given item view to the generated API Item model.
   *
   * @param view the item view projection
   * @return the API Item response model
   */
  Item toApiModel(ItemView view);
}
