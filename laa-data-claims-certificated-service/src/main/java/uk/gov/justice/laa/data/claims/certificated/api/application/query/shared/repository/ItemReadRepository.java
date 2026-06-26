package uk.gov.justice.laa.data.claims.certificated.api.application.query.shared.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import uk.gov.justice.laa.data.claims.certificated.api.application.query.shared.readmodel.ItemReadModel;
import uk.gov.justice.laa.data.claims.certificated.api.domain.ItemEntity;

/**
 * Read-optimised projection repository. Projects scalar fields only — no entity associations are
 * permitted here.
 */
@org.springframework.stereotype.Repository
public interface ItemReadRepository extends Repository<ItemEntity, Long> {

  @Query(
      """
      SELECT new uk.gov.justice.laa.data.claims.certificated.api.application\
      .query.shared.readmodel.ItemReadModel(i.id, i.name, i.description)
      FROM Item i
      """)
  List<ItemReadModel> findAllProjected();

  @Query(
      """
      SELECT new uk.gov.justice.laa.data.claims.certificated.api.application\
      .query.shared.readmodel.ItemReadModel(i.id, i.name, i.description)
      FROM Item i
      WHERE i.id = :id
      """)
  Optional<ItemReadModel> findProjectedById(@Param("id") Long id);
}
