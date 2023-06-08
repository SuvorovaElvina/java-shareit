package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.IncorrectCountException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.reposiroty.ItemRequestRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository repository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper mapper;
    private final ItemMapper itemMapper;

    @Override
    public ItemRequestDto add(long userId, RequestDto requestDto) {
        ItemRequest itemRequest = mapper.toItemRequest(requestDto);
        itemRequest.setOwner(userService.getUser(userId));
        itemRequest.setCreated(LocalDateTime.now());
        return mapper.toItemRequestDto(repository.save(itemRequest));
    }

    @Override
    public ItemRequestDto getById(long userId, long requestId) {
        userService.getById(userId);
        ItemRequestDto requestDto = mapper.toItemRequestDto(reply(requestId));
        requestDto.setItems(itemRepository.findByRequestIdOrderByIdAsc(requestId)
                .stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList()));
        return requestDto;
    }

    @Override
    public List<ItemRequestDto> getAllByUser(long userId, int from, int size) {
        userService.getById(userId);
        int pageNumber = (int) Math.ceil((double) from / size);
        Page<ItemRequest> requests = repository.findByOwnerId(userId, PageRequest.of(pageNumber, size, Sort.by("created")));
        List<ItemRequestDto> requestsDto = requests.stream()
                .map(mapper::toItemRequestDto)
                .collect(Collectors.toList());
        for (ItemRequestDto requestDto : requestsDto) {
            requestDto.setItems(itemRepository.findByRequestIdOrderByIdAsc(requestDto.getId())
                    .stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList()));
        }
        return requestsDto;
    }

    @Override
    public List<ItemRequestDto> getAll(long userId, int from, int size) {
        userService.getById(userId);
        int pageNumber = (int) Math.ceil((double) from / size);
        Page<ItemRequest> requests = repository.findByOwnerIdNot(userId, PageRequest.of(pageNumber, size, Sort.by("created").descending()));
        List<ItemRequestDto> requestsDto = requests.stream()
                .map(mapper::toItemRequestDto)
                .collect(Collectors.toList());
        for (ItemRequestDto requestDto : requestsDto) {
            requestDto.setItems(itemRepository.findByRequestIdOrderByIdAsc(requestDto.getId())
                    .stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList()));
        }
        return requestsDto;
    }

    @Override
    public ItemRequest reply(long requestId) {
        if (requestId < 0) {
            throw new IncorrectCountException("id не должно быть меньше 0.");
        }
        Optional<ItemRequest> optional = repository.findById(requestId);
        return optional.orElseThrow(() -> new NotFoundException(String.format("Запроса с номером %d - не найдено. Возможно не был ещё создан этот запрос.", requestId)));
    }
}
