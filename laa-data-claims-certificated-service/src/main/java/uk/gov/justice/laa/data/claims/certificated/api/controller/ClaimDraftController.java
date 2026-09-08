package uk.gov.justice.laa.data.claims.certificated.api.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.justice.laa.data.claims.certificated.api.api.ClaimDraftsApi;
import uk.gov.justice.laa.data.claims.certificated.api.model.ClaimDraft;
import uk.gov.justice.laa.data.claims.certificated.api.model.ClaimDraftCreateRequest;
import uk.gov.justice.laa.data.claims.certificated.api.service.ClaimDraftService;

/** Controller for handling claim draft requests. */
@RestController
@RequiredArgsConstructor
@Slf4j
public class ClaimDraftController extends BaseApiController implements ClaimDraftsApi {

  private final ClaimDraftService claimDraftService;

  @Override
  public ResponseEntity<ClaimDraft> createClaimDraft(
      @Valid @RequestBody ClaimDraftCreateRequest claimDraftCreateRequest) {
    log.info("Creating claim draft");

    ClaimDraft claimDraft = claimDraftService.createClaimDraft(claimDraftCreateRequest);
    var location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{id}")
            .buildAndExpand(claimDraft.getId())
            .toUri();

    return ResponseEntity.created(location).body(claimDraft);
  }
}
