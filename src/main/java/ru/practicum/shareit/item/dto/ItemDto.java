package ru.practicum.shareit.item.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.dto.ItemsBookingDto;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validation.Create;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemDto {
    Long id;
    User owner;
    @NotBlank(groups = Create.class)
    @Size(max = 255)
    String name;
    @NotBlank(groups = Create.class)
    @Size(max = 512)
    String description;
    @NotNull(groups = Create.class)
    Boolean available;
    ItemsBookingDto lastBooking;
    ItemsBookingDto nextBooking;
    List<CommentDto> comments;
    Long requestId;
}
