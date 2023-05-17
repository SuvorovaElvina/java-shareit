package ru.practicum.shareit.booking.model;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.booking.enums.Status;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "bookings")
@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    long id;
    @Column(name = "start_date")
    LocalDate start;
    @Column(name = "end_date")
    LocalDate end;
    @OneToOne(fetch = FetchType.LAZY)
    User booker;
    @OneToOne(fetch = FetchType.LAZY)
    Item item;
    @Enumerated(EnumType.STRING)
    Status status;
}
