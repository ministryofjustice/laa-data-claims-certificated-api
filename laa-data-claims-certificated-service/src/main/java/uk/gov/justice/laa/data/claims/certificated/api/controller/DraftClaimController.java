package uk.gov.justice.laa.data.claims.certificated.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.justice.laa.data.claims.certificated.api.api.DraftsApi;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaim;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaimCreateRequest;
import uk.gov.justice.laa.data.claims.certificated.api.service.DraftClaimService;

/** Controller for handling draft claim requests. */
@RestController
@RequiredArgsConstructor
@Slf4j
public class DraftClaimController extends BaseApiController implements DraftsApi {

  private final DraftClaimService draftClaimService;

  @Override
  public ResponseEntity<DraftClaim> createDraft(
      @Valid @RequestBody DraftClaimCreateRequest draftClaimCreateRequest) {
    log.info("Creating draft claim");

    DraftClaim draftClaim = draftClaimService.createDraft(draftClaimCreateRequest);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(draftClaim.getId())
            .toUri();

    return ResponseEntity.created(location).body(draftClaim);
  }
}
