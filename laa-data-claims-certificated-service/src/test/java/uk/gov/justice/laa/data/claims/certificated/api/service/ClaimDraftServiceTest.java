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
import uk.gov.justice.laa.data.claims.certificated.api.entity.ClaimDraftEntity;
import uk.gov.justice.laa.data.claims.certificated.api.mapper.ClaimDraftMapper;
import uk.gov.justice.laa.data.claims.certificated.api.model.ClaimDraft;
import uk.gov.justice.laa.data.claims.certificated.api.model.ClaimDraftCreateRequest;
import uk.gov.justice.laa.data.claims.certificated.api.repository.ClaimDraftRepository;
import uk.gov.justice.laa.data.claims.certificated.api.utils.Uuid7Generator;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClaimDraftService")
class ClaimDraftServiceTest {

  @Mock private ClaimDraftRepository mockClaimDraftRepository;
  @Mock private ClaimDraftMapper mockClaimDraftMapper;
  @Mock private Uuid7Generator mockUuid7Generator;

  @InjectMocks private ClaimDraftService ClaimDraftService;

  @Nested
  @DisplayName("Create claim draft")
  class CreateClaimDraft {
    @Test
    @DisplayName("returns the created claim draft")
    void shouldCreateClaimDraft() {
      ClaimDraftCreateRequest draftRequestBody =
          ClaimDraftCreateRequest.builder()
              .sourceSystem("TestClient")
              .createdByUserId("user-123")
              .data(Map.of("key1", "value1", "key2", "value2"))
              .metadata(Map.of("meta1", "value1"))
              .claimTypeId(UUID.fromString("12345678-1234-7234-1234-123456789013"))
              .certificateId("cert-123")
              .build();

      UUID draftId = UUID.fromString("12345678-1234-7234-1234-123456789012");
      ClaimDraftEntity initialEntity =
          ClaimDraftEntity.builder()
              .sourceSystem(draftRequestBody.getSourceSystem())
              .createdByUserId(draftRequestBody.getCreatedByUserId())
              .data(draftRequestBody.getData())
              .metadata(draftRequestBody.getMetadata())
              .claimTypeId(draftRequestBody.getClaimTypeId())
              .certificateId(draftRequestBody.getCertificateId())
              .build();
      ClaimDraft expectedClaimDraft =
          ClaimDraft.builder()
              .id(draftId)
              .sourceSystem(draftRequestBody.getSourceSystem())
              .createdByUserId(draftRequestBody.getCreatedByUserId())
              .data(draftRequestBody.getData())
              .metadata(draftRequestBody.getMetadata())
              .claimTypeId(draftRequestBody.getClaimTypeId())
              .certificateId(draftRequestBody.getCertificateId())
              .status(ClaimDraft.StatusEnum.DRAFT)
              .build();
      when(mockUuid7Generator.generate()).thenReturn(draftId);
      when(mockClaimDraftMapper.toClaimDraftEntity(draftRequestBody)).thenReturn(initialEntity);
      when(mockClaimDraftRepository.save(any(ClaimDraftEntity.class)))
          .thenAnswer(i -> i.getArgument(0));
      when(mockClaimDraftMapper.toClaimDraft(any(ClaimDraftEntity.class)))
          .thenReturn(expectedClaimDraft);

      ClaimDraft actualClaimDraft = ClaimDraftService.createClaimDraft(draftRequestBody);

      ArgumentCaptor<ClaimDraftEntity> entityCaptor =
          ArgumentCaptor.forClass(ClaimDraftEntity.class);
      verify(mockClaimDraftRepository).save(entityCaptor.capture());
      ClaimDraftEntity savedEntity = entityCaptor.getValue();

      assertThat(savedEntity.getId()).isEqualTo(draftId);
      assertThat(savedEntity.getStatus()).isEqualTo(ClaimDraftEntity.ClaimDraftStatus.DRAFT);

      assertThat(actualClaimDraft).isEqualTo(expectedClaimDraft);
    }
  }
}
