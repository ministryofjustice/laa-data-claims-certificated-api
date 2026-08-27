package uk.gov.justice.laa.data.claims.certificated.api.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaim;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaimCreateRequest;

/** Service class for handling draft claim requests. */
@Slf4j
@RequiredArgsConstructor
@Service
public class DraftClaimService {
  /**
   * Creates a draft claim.
   *
   * @param draftClaimRequestBody the draft claim to be created
   * @return the draft claim
   */
  public DraftClaim createDraft(DraftClaimCreateRequest draftClaimRequestBody) {
    log.info("Creating draft claim");
    return null;
  }
}
