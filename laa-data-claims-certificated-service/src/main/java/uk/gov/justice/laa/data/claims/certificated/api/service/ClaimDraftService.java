package uk.gov.justice.laa.data.claims.certificated.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.data.claims.certificated.api.entity.ClaimDraftEntity;
import uk.gov.justice.laa.data.claims.certificated.api.mapper.ClaimDraftMapper;
import uk.gov.justice.laa.data.claims.certificated.api.model.ClaimDraft;
import uk.gov.justice.laa.data.claims.certificated.api.model.ClaimDraftCreateRequest;
import uk.gov.justice.laa.data.claims.certificated.api.repository.ClaimDraftRepository;
import uk.gov.justice.laa.data.claims.certificated.api.utils.Uuid7Generator;

/** Service class for handling draft claim requests. */
@Slf4j
@RequiredArgsConstructor
@Service
public class ClaimDraftService {
  private final ClaimDraftRepository repository;
  private final ClaimDraftMapper mapper;
  private final Uuid7Generator uuidGenerator;

  /**
   * Creates a draft claim.
   *
   * @param claimDraftRequestBody the draft claim to be created
   * @return the draft claim
   */
  public ClaimDraft createClaimDraft(ClaimDraftCreateRequest claimDraftRequestBody) {
    log.info("Creating draft claim");
    ClaimDraftEntity claimDraftEntity = mapper.toClaimDraftEntity(claimDraftRequestBody);

    claimDraftEntity.setId(uuidGenerator.generate());
    claimDraftEntity.setStatus(ClaimDraftEntity.ClaimDraftStatus.DRAFT);
    claimDraftEntity.setCreatedAt(java.time.OffsetDateTime.now());
    claimDraftEntity.setUpdatedAt(java.time.OffsetDateTime.now());

    ClaimDraftEntity savedEntity = repository.save(claimDraftEntity);

    return mapper.toClaimDraft(savedEntity);
  }
}
