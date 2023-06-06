package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.status.Status;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.UnknownStateException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.ValidationException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingMapper mapper;
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository repository;

    @Override
    public BookingDto create(long userId, BookingDto bookingDto) {
        Booking booking = mapper.toBooking(bookingDto);
        booking.setItem(itemService.getItem(bookingDto.getItemId()));
        if (booking.getItem().isAvailable()) {
            if (booking.getItem().getOwner().getId() != userId) {
                validateTime(booking.getStart(), booking.getEnd());
                booking.setBooker(userService.getUser(userId));
                booking.setStatus(Status.WAITING);
                return mapper.toBookingDto(repository.save(booking));
            } else {
                throw new NotFoundException("Вы являетесь владельцем вещи - бронирование невозможно.");
            }
        } else {
            throw new ValidationException("Эта вещь уже забронирована.");
        }
    }

    @Override
    public BookingDto update(long userId, long bookingId, Boolean approved) {
        if (Optional.ofNullable(approved).isPresent()) {
            Booking booking = getBooking(bookingId);
            if (booking.getItem().getOwner().getId() == userId) {
                if (booking.getStatus().equals(Status.APPROVED) || booking.getStatus().equals(Status.REJECTED)) {
                    throw new ValidationException("Вы уже подвертили или отказали бронирование. Повторное действие не возможно.");
                }
                if (approved) {
                    booking.setStatus(Status.APPROVED);
                } else {
                    booking.setStatus(Status.REJECTED);
                }
                repository.save(booking);
                return mapper.toBookingDto(booking);
            } else {
                throw new NotFoundException("Вы не являетесь владельцем вещи.");
            }
        } else {
            throw new UnknownStateException("Обязательно должен быть указан approved");
        }
    }

    @Override
    public BookingDto getById(long userId, long bookingId) {
        Booking booking = getBooking(bookingId);
        if (booking.getBooker().getId() == userId || booking.getItem().getOwner().getId() == userId) {
            return mapper.toBookingDto(booking);
        } else {
            throw new NotFoundException("Вы не являетесь автором бронирования или владельцем вещи.");
        }
    }

    @Override
    public List<BookingDto> getAllByUser(long bookerId, String state, int from, int size) {
        userService.getById(bookerId);
        if (from < 0 || size <= 0) {
            throw new ValidationException("Значения указанные в from или size не должы быть отрицательными.");
        }
        Page<Booking> bookings;
        int pageNumber = (int) Math.ceil((double) from / size);
        switch (state) {
            case "ALL":
                bookings = repository.findByBookerId(bookerId, PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case "FUTURE":
                bookings = repository.findByBookerIdAndStatusIn(bookerId, Set.of(Status.WAITING, Status.APPROVED),
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case "REJECTED":
                bookings = repository.findByBookerIdAndStatusIs(bookerId, Status.REJECTED,
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case "WAITING":
                bookings = repository.findByBookerIdAndStatusIs(bookerId, Status.WAITING,
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case "CURRENT":
                bookings = repository.findByBookerIdAndStartBeforeAndEndAfter(bookerId, LocalDateTime.now(),
                        LocalDateTime.now(), PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case "PAST":
                bookings = repository.findByBookerIdAndEndBefore(bookerId, LocalDateTime.now(),
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
        return bookings
                .stream()
                .map(mapper::toBookingDto)
                .collect(toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(long ownerId, String state, int from, int size) {
        userService.getById(ownerId);
        if (from < 0 || size <= 0) {
            throw new ValidationException("Значения указанные в from или size не должы быть отрицательными.");
        }
        Page<Booking> bookings;
        int pageNumber = (int) Math.ceil((double) from / size);
        switch (state) {
            case "ALL":
                bookings = repository.findByOwnerId(ownerId, PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case "FUTURE":
                bookings = repository.findByOwnerIdAndStatusIn(ownerId, Set.of(Status.WAITING, Status.APPROVED),
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case "REJECTED":
                bookings = repository.findByOwnerIdAndStatus(ownerId, Status.REJECTED,
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case "WAITING":
                bookings = repository.findByOwnerIdAndStatus(ownerId, Status.WAITING,
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case "CURRENT":
                bookings = repository.findByOwnerIdCurrent(ownerId, LocalDateTime.now(),
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            case "PAST":
                bookings = repository.findByOwnerIdPast(ownerId, LocalDateTime.now(),
                        PageRequest.of(pageNumber, size, Sort.by("start").descending()));
                break;
            default:
                throw new UnknownStateException("Unknown state: " + state);
        }
        return bookings
                .stream()
                .map(mapper::toBookingDto)
                .collect(toList());
    }

    private Booking getBooking(long id) {
        Optional<Booking> booking = repository.findById(id);
        if (booking.isEmpty()) {
            if (id < 0) {
                throw new IncorrectCountException("id не должно быть меньше 0.");
            } else {
                throw new NotFoundException(String.format("Бронирования с id %d - не существует.", id));
            }
        } else {
            return booking.get();
        }
    }

    private void validateTime(LocalDateTime start, LocalDateTime end) {
        if (end.isBefore(start)) {
            throw new ValidationException("Конец бронирования не может быть раньше старта.");
        }
        if (end.equals(start)) {
            throw new ValidationException("Конец бронирования не может быть одинаков с стартом.");
        }
        if (start.isBefore(LocalDateTime.now())) {
            throw new ValidationException("Начало бронирования может начатся только с текущего времени.");
        }
    }
}
