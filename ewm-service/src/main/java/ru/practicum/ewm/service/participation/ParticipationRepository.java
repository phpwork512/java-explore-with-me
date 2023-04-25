package ru.practicum.ewm.service.participation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.service.participation.model.Participation;

import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByEvent_IdAndStatusOrderById(long eventId, String confirmed);

    List<Participation> findByEvent_IdOrderById(long eventId);

    @Modifying
    @Query("UPDATE Participation p SET p.status = 'REJECTED' WHERE p.event.id = :eventId AND p.status= 'PENDING'")
    void rejectAllPendingRequests(@Param("eventId") long eventId);

    List<Participation> findByUser_IdOrderById(long userId);

    Optional<Participation> findByEvent_IdAndUser_Id(long eventId, long requesterId);
}
