package ru.practicum.shareit.request.reposiroty;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {
    Page<ItemRequest> findByOwnerId(Long userId, Pageable pageable);

    Page<ItemRequest> findByOwnerIdNot(Long userId, Pageable pageable);
}
