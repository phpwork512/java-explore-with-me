package ru.practicum.ewm.service.user;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.service.user.model.User;

import java.net.ContentHandler;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findByIdInOrderById(List<Long> userIdList, Pageable page);
}