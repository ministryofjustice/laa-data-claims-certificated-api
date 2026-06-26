package uk.gov.justice.laa.data.claims.certificated.api.application.command.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.data.claims.certificated.api.domain.ItemEntity;

/** Write-side repository for managing item entities. */
@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {}
