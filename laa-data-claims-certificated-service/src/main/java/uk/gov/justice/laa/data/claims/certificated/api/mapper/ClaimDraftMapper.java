package uk.gov.justice.laa.data.claims.certificated.api.mapper;

import org.mapstruct.Mapper;
import uk.gov.justice.laa.data.claims.certificated.api.entity.ClaimDraftEntity;
import uk.gov.justice.laa.data.claims.certificated.api.model.ClaimDraft;
import uk.gov.justice.laa.data.claims.certificated.api.model.ClaimDraftCreateRequest;

/** The mapper between ClaimDraft and ClaimDraftEntity. */
@Mapper(componentModel = "spring")
public interface ClaimDraftMapper {

  ClaimDraft toClaimDraft(ClaimDraftEntity entity);

  ClaimDraftEntity toClaimDraftEntity(ClaimDraft claimDraft);

  ClaimDraftEntity toClaimDraftEntity(ClaimDraftCreateRequest claimDraftCreateRequest);
}
