package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.dto.ItemsBookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

@Component
public class ItemMapper {
    public ItemDto toSimpleItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();
    }


    public ItemDto toBookingItemDto(Item item, ItemsBookingDto lastBooking, ItemsBookingDto nextBooking) {
        return ItemDto.builder()
                .id(item.getId())
                .owner(item.getOwner())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .lastBooking(lastBooking)
                .nextBooking(nextBooking)
                .build();
    }

    public Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }
}
