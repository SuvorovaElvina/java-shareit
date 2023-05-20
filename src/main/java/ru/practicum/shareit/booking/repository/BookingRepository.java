package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.booking.model.Booking;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Transactional
    Booking findFirst1ByItemIdAndStartBeforeOrderByStartDesc(Long itemId, LocalDateTime time);

    @Transactional
    Booking findFirst1ByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime time);

    List<Booking> findByBookerIdAndStatusInOrderByStartDesc(Long bookerId, Set<Status> states);

    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status state);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime start);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.status in (?2) " +
            "order by b.start desc")
    List<Booking> findByOwnerIdAndStatusIn(Long ownerId, Set<Status> states);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 " +
            "order by b.start desc")
    List<Booking> findByOwnerId(Long ownerId);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.status = ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerIdAndStatus(Long ownerId, Status state);

    @Query("select b from Booking b " +
            "left join Item i on i.id = b.item " +
            "left join User u on i.owner = u.id " +
            "where u.id = ?1 and b.start < ?2 " +
            "order by b.start desc")
    List<Booking> findByOwnerIdAndEndIsBefore(Long ownerId, LocalDateTime start);
}
