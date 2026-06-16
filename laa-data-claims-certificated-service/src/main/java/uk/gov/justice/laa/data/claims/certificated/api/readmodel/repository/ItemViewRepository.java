package uk.gov.justice.laa.data.claims.certificated.api.readmodel.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.data.repository.query.Param;
import uk.gov.justice.laa.data.claims.certificated.api.domain.entity.ItemEntity;
import uk.gov.justice.laa.data.claims.certificated.api.readmodel.view.ItemView;

/**
 * Read-optimised projection repository. Projects scalar fields only — no entity associations are
 * permitted here.
 */
@org.springframework.stereotype.Repository
public interface ItemViewRepository extends Repository<ItemEntity, Long> {

  @Query(
      """
      SELECT new uk.gov.justice.laa.data.claims.certificated.api.readmodel.view.ItemView(
               i.id, i.name, i.description)
      FROM ItemEntity i
      """)
  List<ItemView> findAllProjected();

  @Query(
      """
      SELECT new uk.gov.justice.laa.data.claims.certificated.api.readmodel.view.ItemView(
               i.id, i.name, i.description)
      FROM ItemEntity i
      WHERE i.id = :id
      """)
  Optional<ItemView> findProjectedById(@Param("id") Long id);
}
