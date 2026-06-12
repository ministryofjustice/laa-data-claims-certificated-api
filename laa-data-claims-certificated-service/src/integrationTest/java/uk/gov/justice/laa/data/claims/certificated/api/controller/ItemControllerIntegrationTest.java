package uk.gov.justice.laa.data.claims.certificated.api.controller;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.justice.laa.data.claims.certificated.api.BaseIntegrationTest;

@Transactional
@DisplayName("Item API integration")
class ItemControllerIntegrationTest extends BaseIntegrationTest {

  @Test
  @DisplayName("GET /api/v1/items returns all seeded items")
  void shouldGetAllItems() throws Exception {
    mockMvc
        .perform(get("/api/v1/items"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.*", hasSize(5)));
  }

  @Test
  @DisplayName("GET /api/v1/items/{id} returns the requested item")
  void shouldGetItem() throws Exception {
    mockMvc
        .perform(get("/api/v1/items/1"))
        .andExpect(status().isOk())
        .andExpect(content().contentType(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$.id").value(1))
        .andExpect(jsonPath("$.name").value("Item One"))
        .andExpect(jsonPath("$.description").value("This is a description of Item One."));
  }

  @Test
  @DisplayName("POST /api/v1/items creates a new item")
  void shouldCreateItem() throws Exception {
    mockMvc
        .perform(
            post("/api/v1/items")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"name\": \"Item Six\", \"description\": \"This is a description of Item Six.\"}")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isCreated());
  }

  @Test
  @DisplayName("PUT /api/v1/items/{id} updates an existing item")
  void shouldUpdateItem() throws Exception {
    mockMvc
        .perform(
            put("/api/v1/items/2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(
                    "{\"id\": 2, \"name\": \"Item Two\", \"description\": \"This is a updated description of Item Three.\"}")
                .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isNoContent());
  }

  @Test
  @DisplayName("DELETE /api/v1/items/{id} removes an item")
  void shouldDeleteItem() throws Exception {
    mockMvc.perform(delete("/api/v1/items/3")).andExpect(status().isNoContent());
  }
}
