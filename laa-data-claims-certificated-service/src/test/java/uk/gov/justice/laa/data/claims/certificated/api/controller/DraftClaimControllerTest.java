package uk.gov.justice.laa.data.claims.certificated.api.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaim;
import uk.gov.justice.laa.data.claims.certificated.api.model.DraftClaimCreateRequest;
import uk.gov.justice.laa.data.claims.certificated.api.service.DraftClaimService;

@WebMvcTest(DraftClaimController.class)
@DisplayName("DraftClaimController")
class DraftClaimControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private DraftClaimService mockDraftClaimService;

  @Nested
  @DisplayName("POST /api/v1/drafts")
  class CreateDraft {
    @Test
    @DisplayName("returns 201 Created with the persisted draft")
    void createDraftReturnsCreatedStatus() throws Exception {
      UUID draftId = UUID.fromString("12345678-1234-7234-1234-123456789012");
      UUID draftTypeId = UUID.fromString("12345678-1234-7234-1234-123456789013");
      DraftClaimCreateRequest draftRequestBody =
          DraftClaimCreateRequest.builder()
              .sourceSystem("TestClient")
              .createdByUserId("user-123")
              .data(Map.of("key1", "value1", "key2", "value2"))
              .metadata(Map.of("meta1", "value1"))
              .draftTypeId(draftTypeId)
              .certificateId("cert-123")
              .build();
      when(mockDraftClaimService.createDraft(draftRequestBody))
          .thenReturn(
              DraftClaim.builder()
                  .id(draftId)
                  .sourceSystem(draftRequestBody.getSourceSystem())
                  .createdByUserId(draftRequestBody.getCreatedByUserId())
                  .data(draftRequestBody.getData())
                  .metadata(draftRequestBody.getMetadata())
                  .draftTypeId(draftRequestBody.getDraftTypeId())
                  .certificateId(draftRequestBody.getCertificateId())
                  .status(DraftClaim.StatusEnum.DRAFT)
                  .build());

      mockMvc
          .perform(
              post("/api/v1/drafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\"sourceSystem\": \"TestClient\", \"createdByUserId\": \"user-123\", \"data\": {\"key1\": \"value1\", \"key2\": \"value2\"}, \"metadata\": {\"meta1\": \"value1\"}, \"draftTypeId\": \"12345678-1234-7234-1234-123456789013\", \"certificateId\": \"cert-123\"}")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(draftId.toString()))
          .andExpect(jsonPath("$.sourceSystem").value("TestClient"))
          .andExpect(jsonPath("$.createdByUserId").value("user-123"))
          .andExpect(jsonPath("$.data.key1").value("value1"))
          .andExpect(jsonPath("$.data.key2").value("value2"))
          .andExpect(jsonPath("$.metadata.meta1").value("value1"))
          .andExpect(jsonPath("$.draftTypeId").value(draftTypeId.toString()))
          .andExpect(jsonPath("$.certificateId").value("cert-123"))
          .andExpect(jsonPath("$.status").value("draft"));

      verify(mockDraftClaimService).createDraft(draftRequestBody);
    }
  }
}
