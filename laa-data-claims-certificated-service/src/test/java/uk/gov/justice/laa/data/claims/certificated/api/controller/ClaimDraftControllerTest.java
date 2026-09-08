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
import uk.gov.justice.laa.data.claims.certificated.api.model.ClaimDraft;
import uk.gov.justice.laa.data.claims.certificated.api.model.ClaimDraftCreateRequest;
import uk.gov.justice.laa.data.claims.certificated.api.service.ClaimDraftService;

@WebMvcTest(ClaimDraftController.class)
@DisplayName("ClaimDraftController")
class ClaimDraftControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ClaimDraftService mockClaimDraftService;

  @Nested
  @DisplayName("POST /api/v1/claim-drafts")
  class CreateClaimDraft {
    @Test
    @DisplayName("returns 201 Created with the persisted draft")
    void createClaimDraftReturnsCreatedStatus() throws Exception {
      UUID draftId = UUID.fromString("12345678-1234-7234-1234-123456789012");
      UUID draftTypeId = UUID.fromString("12345678-1234-7234-1234-123456789013");
      ClaimDraftCreateRequest draftRequestBody =
          ClaimDraftCreateRequest.builder()
              .sourceSystem("TestClient")
              .createdByUserId("user-123")
              .data(Map.of("key1", "value1", "key2", "value2"))
              .metadata(Map.of("meta1", "value1"))
              .claimTypeId(draftTypeId)
              .certificateId("cert-123")
              .build();
      when(mockClaimDraftService.createClaimDraft(draftRequestBody))
          .thenReturn(
              ClaimDraft.builder()
                  .id(draftId)
                  .sourceSystem(draftRequestBody.getSourceSystem())
                  .createdByUserId(draftRequestBody.getCreatedByUserId())
                  .data(draftRequestBody.getData())
                  .metadata(draftRequestBody.getMetadata())
                  .claimTypeId(draftRequestBody.getClaimTypeId())
                  .certificateId(draftRequestBody.getCertificateId())
                  .status(ClaimDraft.StatusEnum.DRAFT)
                  .build());

      mockMvc
          .perform(
              post("/api/v1/claim-drafts")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\"sourceSystem\": \"TestClient\", \"createdByUserId\": \"user-123\", \"data\": {\"key1\": \"value1\", \"key2\": \"value2\"}, \"metadata\": {\"meta1\": \"value1\"}, \"claimTypeId\": \"12345678-1234-7234-1234-123456789013\", \"certificateId\": \"cert-123\"}")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(jsonPath("$.id").value(draftId.toString()))
          .andExpect(jsonPath("$.sourceSystem").value("TestClient"))
          .andExpect(jsonPath("$.createdByUserId").value("user-123"))
          .andExpect(jsonPath("$.data.key1").value("value1"))
          .andExpect(jsonPath("$.data.key2").value("value2"))
          .andExpect(jsonPath("$.metadata.meta1").value("value1"))
          .andExpect(jsonPath("$.claimTypeId").value(draftTypeId.toString()))
          .andExpect(jsonPath("$.certificateId").value("cert-123"))
          .andExpect(jsonPath("$.status").value("draft"));

      verify(mockClaimDraftService).createClaimDraft(draftRequestBody);
    }
  }
}
