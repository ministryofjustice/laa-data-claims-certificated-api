package uk.gov.justice.laa.data.claims.certificated.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.data.claims.certificated.api.entity.DraftClaimEntity;
import uk.gov.justice.laa.data.claims.certificated.api.mapper.DraftClaimMapper;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaim;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaimCreateRequest;
import uk.gov.justice.laa.data.claims.certificated.api.repository.DraftClaimRepository;
import uk.gov.justice.laa.data.claims.certificated.api.utils.Uuid7Generator;

@ExtendWith(MockitoExtension.class)
@DisplayName("DraftClaimService")
class DraftClaimServiceTest {

  @Mock private DraftClaimRepository mockDraftClaimRepository;
  @Mock private DraftClaimMapper mockDraftClaimMapper;
  @Mock private Uuid7Generator mockUuid7Generator;

  @InjectMocks private DraftClaimService draftClaimService;

  @Nested
  @DisplayName("Create draft claim")
  class CreateDraftClaim {
    @Test
    @DisplayName("returns the created draft claim")
    void shouldCreateDraftClaim() {
      DraftClaimCreateRequest draftRequestBody =
          DraftClaimCreateRequest.builder()
              .sourceSystem("TestClient")
              .createdByUserId("user-123")
              .data(Map.of("key1", "value1", "key2", "value2"))
              .metadata(Map.of("meta1", "value1"))
              .draftTypeId(UUID.fromString("12345678-1234-7234-1234-123456789013"))
              .certificateId("cert-123")
              .build();

      UUID draftId = UUID.fromString("12345678-1234-7234-1234-123456789012");
      DraftClaimEntity initialEntity =
          DraftClaimEntity.builder()
              .sourceSystem(draftRequestBody.getSourceSystem())
              .createdByUserId(draftRequestBody.getCreatedByUserId())
              .data(draftRequestBody.getData())
              .metadata(draftRequestBody.getMetadata())
              .draftTypeId(draftRequestBody.getDraftTypeId())
              .certificateId(draftRequestBody.getCertificateId())
              .build();
      DraftClaim expectedDraftClaim =
          DraftClaim.builder()
              .id(draftId)
              .sourceSystem(draftRequestBody.getSourceSystem())
              .createdByUserId(draftRequestBody.getCreatedByUserId())
              .data(draftRequestBody.getData())
              .metadata(draftRequestBody.getMetadata())
              .draftTypeId(draftRequestBody.getDraftTypeId())
              .certificateId(draftRequestBody.getCertificateId())
              .status(DraftClaim.StatusEnum.DRAFT)
              .build();
      when(mockUuid7Generator.generate()).thenReturn(draftId);
      when(mockDraftClaimMapper.toDraftClaimEntity(draftRequestBody)).thenReturn(initialEntity);
      when(mockDraftClaimRepository.save(any(DraftClaimEntity.class)))
          .thenAnswer(i -> i.getArgument(0));
      when(mockDraftClaimMapper.toDraftClaim(any(DraftClaimEntity.class)))
          .thenReturn(expectedDraftClaim);

      DraftClaim actualDraftClaim = draftClaimService.createDraft(draftRequestBody);

      ArgumentCaptor<DraftClaimEntity> entityCaptor =
          ArgumentCaptor.forClass(DraftClaimEntity.class);
      verify(mockDraftClaimRepository).save(entityCaptor.capture());
      DraftClaimEntity savedEntity = entityCaptor.getValue();

      assertThat(savedEntity.getId()).isEqualTo(draftId);
      assertThat(savedEntity.getStatus()).isEqualTo(DraftClaimEntity.DraftClaimStatus.DRAFT);

      assertThat(actualDraftClaim).isEqualTo(expectedDraftClaim);
    }
  }
}
