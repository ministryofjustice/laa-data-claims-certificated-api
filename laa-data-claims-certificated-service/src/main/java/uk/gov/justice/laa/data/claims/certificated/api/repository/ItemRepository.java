package uk.gov.justice.laa.data.claims.certificated.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.data.claims.certificated.api.entity.ItemEntity;

/** Repository for managing item entities. */
@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {}
