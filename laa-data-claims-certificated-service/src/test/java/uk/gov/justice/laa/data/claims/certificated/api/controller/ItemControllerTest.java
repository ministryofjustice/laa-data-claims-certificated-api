package uk.gov.justice.laa.data.claims.certificated.api.controller;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import uk.gov.justice.laa.data.claims.certificated.api.model.Item;
import uk.gov.justice.laa.data.claims.certificated.api.model.ItemRequestBody;
import uk.gov.justice.laa.data.claims.certificated.api.service.ItemService;

@WebMvcTest(ItemController.class)
@DisplayName("ItemController")
class ItemControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean private ItemService mockItemService;

  @Nested
  @DisplayName("GET /api/v1/items")
  class GetItems {

    @Test
    @DisplayName("returns 200 OK with all items")
    void getItemsReturnsOkStatusAndAllItems() throws Exception {
      List<Item> items =
          List.of(
              Item.builder()
                  .id(1L)
                  .name("Item One")
                  .description("This is a test item one.")
                  .build(),
              Item.builder()
                  .id(2L)
                  .name("Item Two")
                  .description("This is a test item two.")
                  .build());
      when(mockItemService.getAllItems()).thenReturn(items);

      mockMvc
          .perform(get("/api/v1/items"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.*", hasSize(2)));
    }
  }

  @Nested
  @DisplayName("GET /api/v1/items/{id}")
  class GetItemById {

    @Test
    @DisplayName("returns 200 OK with the requested item")
    void getItemByIdReturnsOkStatusAndOneItem() throws Exception {
      when(mockItemService.getItem(1L))
          .thenReturn(
              Item.builder()
                  .id(1L)
                  .name("Item One")
                  .description("This is a test item one.")
                  .build());

      mockMvc
          .perform(get("/api/v1/items/1"))
          .andExpect(status().isOk())
          .andExpect(content().contentType(MediaType.APPLICATION_JSON))
          .andExpect(jsonPath("$.id").value(1))
          .andExpect(jsonPath("$.name").value("Item One"))
          .andExpect(jsonPath("$.description").value("This is a test item one."));
    }
  }

  @Nested
  @DisplayName("POST /api/v1/items")
  class CreateItem {

    @Test
    @DisplayName("returns 201 Created with a Location header")
    void createItemReturnsCreatedStatusAndLocationHeader() throws Exception {
      ItemRequestBody itemRequestBody =
          ItemRequestBody.builder()
              .name("Item Three")
              .description("This is an updated item three.")
              .build();
      when(mockItemService.createItem(itemRequestBody)).thenReturn(3L);

      mockMvc
          .perform(
              post("/api/v1/items")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\"name\": \"Item Three\", \"description\": \"This is an updated item three.\"}")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isCreated())
          .andExpect(header().string("Location", containsString("/api/v1/items/3")));
    }

    @Test
    @DisplayName("returns 400 Bad Request when the body is invalid")
    void createItemReturnsBadRequestStatus() throws Exception {
      mockMvc
          .perform(
              post("/api/v1/items")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"name\": \"Item Three\"}")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest())
          .andExpect(
              content()
                  .json(
                      "{\"type\":\"about:blank\",\"title\":\"Bad Request\","
                          + "\"status\":400,\"detail\":\"Invalid request content.\",\"instance\":\"/api/v1/items\"}"));

      verify(mockItemService, never()).createItem(any(ItemRequestBody.class));
    }
  }

  @Nested
  @DisplayName("PUT /api/v1/items/{id}")
  class UpdateItem {

    @Test
    @DisplayName("returns 204 No Content when the update succeeds")
    void updateItemReturnsNoContentStatus() throws Exception {
      mockMvc
          .perform(
              put("/api/v1/items/2")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content(
                      "{\"name\": \"Item Two\", \"description\": \"This is an updated item two.\"}")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isNoContent());

      verify(mockItemService).updateItem(eq(2L), any(ItemRequestBody.class));
    }

    @Test
    @DisplayName("returns 400 Bad Request when the body is invalid")
    void updateItemReturnsBadRequestStatus() throws Exception {
      mockMvc
          .perform(
              put("/api/v1/items/2")
                  .contentType(MediaType.APPLICATION_JSON)
                  .content("{\"description\": \"This is an updated item two.\"}")
                  .accept(MediaType.APPLICATION_JSON))
          .andExpect(status().isBadRequest())
          .andExpect(
              content()
                  .json(
                      "{\"type\":\"about:blank\",\"title\":\"Bad Request\","
                          + "\"status\":400,\"detail\":\"Invalid request content.\",\"instance\":\"/api/v1/items/2\"}"));

      verify(mockItemService, never()).updateItem(eq(2L), any(ItemRequestBody.class));
    }
  }

  @Nested
  @DisplayName("DELETE /api/v1/items/{id}")
  class DeleteItem {

    @Test
    @DisplayName("returns 204 No Content when the delete succeeds")
    void deleteItemReturnsNoContentStatus() throws Exception {
      mockMvc.perform(delete("/api/v1/items/3")).andExpect(status().isNoContent());

      verify(mockItemService).deleteItem(3L);
    }
  }
}
