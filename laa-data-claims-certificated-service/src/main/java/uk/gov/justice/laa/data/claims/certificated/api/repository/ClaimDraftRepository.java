package uk.gov.justice.laa.data.claims.certificated.api.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.data.claims.certificated.api.entity.ClaimDraftEntity;

/**
 * Repository interface for managing ClaimDraftEntity instances. Provides basic CRUD operations
 * through JpaRepository.
 */
@Repository
public interface ClaimDraftRepository extends JpaRepository<ClaimDraftEntity, UUID> {}
