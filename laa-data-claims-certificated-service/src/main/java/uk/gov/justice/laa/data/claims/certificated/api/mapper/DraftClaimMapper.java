package uk.gov.justice.laa.data.claims.certificated.api.mapper;

import org.mapstruct.Mapper;
import uk.gov.justice.laa.data.claims.certificated.api.entity.DraftClaimEntity;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaim;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaimCreateRequest;

/** The mapper between DraftClaim and DraftClaimEntity. */
@Mapper(componentModel = "spring")
public interface DraftClaimMapper {

  DraftClaim toDraftClaim(DraftClaimEntity entity);

  DraftClaimEntity toDraftClaimEntity(DraftClaim draftClaim);

  DraftClaimEntity toDraftClaimEntity(DraftClaimCreateRequest draftClaimCreateRequest);
}
