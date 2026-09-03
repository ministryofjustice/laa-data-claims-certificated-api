package uk.gov.justice.laa.data.claims.certificated.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.data.claims.certificated.api.entity.DraftClaimEntity;
import uk.gov.justice.laa.data.claims.certificated.api.mapper.DraftClaimMapper;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaim;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaimCreateRequest;
import uk.gov.justice.laa.data.claims.certificated.api.repository.DraftClaimRepository;
import uk.gov.justice.laa.data.claims.certificated.api.utils.Uuid7Generator;

/** Service class for handling draft claim requests. */
@Slf4j
@RequiredArgsConstructor
@Service
public class DraftClaimService {
  private final DraftClaimRepository repository;
  private final DraftClaimMapper mapper;
  private final Uuid7Generator uuidGenerator;

  /**
   * Creates a draft claim.
   *
   * @param draftClaimRequestBody the draft claim to be created
   * @return the draft claim
   */
  public DraftClaim createDraft(DraftClaimCreateRequest draftClaimRequestBody) {
    log.info("Creating draft claim");
    DraftClaimEntity draftClaimEntity = mapper.toDraftClaimEntity(draftClaimRequestBody);

    draftClaimEntity.setId(uuidGenerator.generate());
    draftClaimEntity.setStatus(DraftClaimEntity.DraftClaimStatus.DRAFT);

    DraftClaimEntity savedEntity = repository.save(draftClaimEntity);

    return mapper.toDraftClaim(savedEntity);
  }
}
