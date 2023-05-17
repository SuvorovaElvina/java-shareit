package ru.practicum.shareit.item.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

@Entity
@Table(name = "items")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;

    @OneToOne(fetch = FetchType.LAZY)
    User owner;

    @Column(nullable = false)
    @NotBlank(message = "Имя не должно быть пустым.")
    String name;

    @Column(nullable = false)
    @NotBlank(message = "Описание не должно быть пустым.")
    String description;

    @Column(name = "is_available")
    boolean available;

    @ManyToOne(fetch = FetchType.LAZY)
    ItemRequest request;
}
