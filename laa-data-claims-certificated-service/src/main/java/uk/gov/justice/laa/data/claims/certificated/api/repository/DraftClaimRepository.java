package uk.gov.justice.laa.data.claims.certificated.api.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.justice.laa.data.claims.certificated.api.entity.DraftClaimEntity;

@Repository
public interface DraftClaimRepository extends JpaRepository<DraftClaimEntity, UUID> {}
