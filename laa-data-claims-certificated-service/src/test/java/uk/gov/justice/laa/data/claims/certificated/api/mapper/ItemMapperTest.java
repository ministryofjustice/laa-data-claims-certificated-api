package uk.gov.justice.laa.data.claims.certificated.api.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.justice.laa.data.claims.certificated.api.entity.ItemEntity;
import uk.gov.justice.laa.data.claims.certificated.api.model.Item;

@ExtendWith(MockitoExtension.class)
@DisplayName("ItemMapper")
class ItemMapperTest {
  private static final Long ITEM_ID = 123L;
  private static final String ITEM_NAME = "Item One";
  private static final String ITEM_DESCRIPTION = "This is Item One.";

  @InjectMocks private ItemMapper itemMapper = new ItemMapperImpl();

  @Test
  @DisplayName("maps an Item to an ItemEntity")
  void shouldMapToItemEntity() {
    Item item = Item.builder().id(ITEM_ID).name(ITEM_NAME).description(ITEM_DESCRIPTION).build();

    ItemEntity result = itemMapper.toItemEntity(item);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(ITEM_ID);
    assertThat(result.getName()).isEqualTo(ITEM_NAME);
  }

  @Test
  @DisplayName("maps an ItemEntity to an Item")
  void shouldMapToItem() {
    ItemEntity itemEntity = new ItemEntity(ITEM_ID, ITEM_NAME, ITEM_DESCRIPTION);

    Item result = itemMapper.toItem(itemEntity);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(ITEM_ID);
    assertThat(result.getName()).isEqualTo(ITEM_NAME);
  }
}
