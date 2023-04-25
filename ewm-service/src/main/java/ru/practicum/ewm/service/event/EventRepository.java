package ru.practicum.ewm.service.event;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.service.event.model.Event;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {
    @Query("SELECT COUNT(e.id) FROM Event e WHERE e.category.id = ?1")
    int getEventsCountByCategoryId(long categoryId);

    List<Event> findByIdIn(List<Long> events);

    List<Event> findByInitiator_idOrderById(long userId, Pageable page);

    Optional<Event> findByIdAndInitiator_id(long eventId, long userId);
}
