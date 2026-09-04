package uk.gov.justice.laa.data.claims.certificated.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.data.claims.certificated.api.BaseIntegrationTest;
import uk.gov.justice.laa.data.claims.certificated.api.entity.DraftClaimEntity;
import uk.gov.justice.laa.data.claims.certificated.api.repository.DraftClaimRepository;

@Transactional
class DraftClaimControllerIntegrationTest extends BaseIntegrationTest {
  @Autowired private DraftClaimRepository draftClaimRepository;
  @Autowired private MockMvc mockMvc;

  private final ObjectMapper objectMapper = new ObjectMapper();

  @Test
  @DisplayName("POST /api/v1/drafts creates a new draft claim")
  void shouldCreateDraftClaim() throws Exception {
    String requestBody =
        "{"
            + "\"sourceSystem\": \"TestClient\","
            + "\"createdByUserId\": \"user-123\","
            + "\"data\": {\"key1\": \"value1\", \"key2\": \"value2\"},"
            + "\"metadata\": {\"meta1\": \"value1\"},"
            + "\"draftTypeId\": \"12345678-1234-7234-1234-123456789013\","
            + "\"certificateId\": \"cert-123\""
            + "}";

    MvcResult result =
        mockMvc
            .perform(
                post("/api/v1/drafts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isCreated())
            .andExpect(
                header().string("Location", Matchers.startsWith("http://localhost/api/v1/drafts/")))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.sourceSystem").value("TestClient"))
            .andExpect(jsonPath("$.createdByUserId").value("user-123"))
            .andExpect(jsonPath("$.data.key1").value("value1"))
            .andExpect(jsonPath("$.data.key2").value("value2"))
            .andExpect(jsonPath("$.metadata.meta1").value("value1"))
            .andExpect(jsonPath("$.draftTypeId").value("12345678-1234-7234-1234-123456789013"))
            .andExpect(jsonPath("$.certificateId").value("cert-123"))
            .andExpect(jsonPath("$.status").value("draft"))
            .andReturn();

    String responseContent = result.getResponse().getContentAsString();
    JsonNode responseJson = objectMapper.readTree(responseContent);
    UUID createdDraftId = UUID.fromString(responseJson.get("id").asText());

    DraftClaimEntity dbEntity = draftClaimRepository.findById(createdDraftId).orElseThrow();

    assertThat(dbEntity.getSourceSystem()).isEqualTo("TestClient");
    assertThat(dbEntity.getCreatedByUserId()).isEqualTo("user-123");
    assertThat(dbEntity.getCertificateId()).isEqualTo("cert-123");
    assertThat(dbEntity.getDraftTypeId())
        .isEqualTo(UUID.fromString("12345678-1234-7234-1234-123456789013"));
    assertThat(dbEntity.getStatus()).isEqualTo(DraftClaimEntity.DraftClaimStatus.DRAFT);

    assertThat(dbEntity.getData()).containsEntry("key1", "value1").containsEntry("key2", "value2");
    assertThat(dbEntity.getMetadata()).containsEntry("meta1", "value1");

    assertThat(dbEntity.getCreatedAt()).isNotNull();
    assertThat(dbEntity.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName(
      "POST /api/v1/drafts with missing required field (sourceSystem) returns 400 Bad Request")
  void shouldReturnBadRequestForMissingRequiredFields() throws Exception {
    String requestBody =
        "{"
            + "\"createdByUserId\": \"user-123\","
            + "\"data\": {\"key1\": \"value1\", \"key2\": \"value2\"},"
            + "\"metadata\": {\"meta1\": \"value1\"},"
            + "\"draftTypeId\": \"12345678-1234-7234-1234-123456789013\","
            + "\"certificateId\": \"cert-123\""
            + "}";

    mockMvc
        .perform(
            post("/api/v1/drafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody)
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    assertThat(draftClaimRepository.count()).isEqualTo(0);
  }

  @Test
  @DisplayName("POST /api/v1/drafts with malformed JSON returns 400 Bad Request")
  void shouldReturn400WhenJsonIsMalformed() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/drafts")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{ invalid json syntax }")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isBadRequest());

    assertThat(draftClaimRepository.count()).isEqualTo(0);
  }
}
