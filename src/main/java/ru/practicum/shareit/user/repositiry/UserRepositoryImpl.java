package ru.practicum.shareit.user.repositiry;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.DuplicateException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.Map;

@Repository
public class UserRepositoryImpl implements UserRepository {
    private final HashMap<Long, User> users = new HashMap<>();
    private long index = 1;

    @Override
    public User add(User user) {
        for (User user1 : users.values()) {
            if (user1.getEmail().equals(user.getEmail())) {
                throw new DuplicateException("Эта почта уже используется, введите другую.");
            }
        }
        user.setId(index++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(long id) {
        users.remove(id);
    }

    @Override
    public Map<Long, User> getAll() {
        return users;
    }
}
