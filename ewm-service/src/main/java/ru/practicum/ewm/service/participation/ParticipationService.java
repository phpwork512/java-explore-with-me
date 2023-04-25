package ru.practicum.ewm.service.participation;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.common.models.EventState;
import ru.practicum.ewm.service.common.models.RequestStatus;
import ru.practicum.ewm.service.event.EventRepository;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.exceptions.*;
import ru.practicum.ewm.service.participation.model.Participation;
import ru.practicum.ewm.service.participation.model.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.service.participation.model.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.service.participation.model.dto.ParticipationDtoMapper;
import ru.practicum.ewm.service.user.UserService;
import ru.practicum.ewm.service.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ParticipationService {
    private final ParticipationRepository participationRepository;
    private final EventRepository eventRepository;
    private final EventService eventService;
    private final UserService userService;

    public Participation getRequestById(long requestId) {
        Optional<Participation> optionalRequest = participationRepository.findById(requestId);
        if (optionalRequest.isEmpty()) {
            throw new ParticipationRequestNotFoundException("Запрос " + requestId + " не найден");
        } else {
            return optionalRequest.get();
        }
    }

    /**
     * Получение информации о заявках текущего пользователя на участие в чужих событиях
     *
     * @param userId id пользователя
     * @return список заявок. В случае, если по заданным фильтрам не найдено ни одной заявки, возвращает пустой список
     */
    public List<Participation> getRequestsByUserId(long userId) {
        userService.getUserById(userId);
        return participationRepository.findByUser_IdOrderById(userId);
    }

    public List<Participation> getRequestsByEventIdAndInitiatorId(long eventId, long initiatorId) {
        eventService.getEventByIdAndInitiatorId(eventId, initiatorId);
        return participationRepository.findByEvent_IdOrderById(eventId);
    }

    /**
     * получить запрос на участие пользователя в событии
     *
     * @param eventId     id события
     * @param requesterId id пользователя-участника
     * @return объект Participation или null если не найдено
     */
    private Participation getRequestByEventIdAndRequesterId(long eventId, long requesterId) {
        eventService.getEventById(eventId);
        Optional<Participation> optionalRequest = participationRepository.findByEvent_IdAndUser_Id(eventId, requesterId);
        if (optionalRequest.isEmpty()) {
            return null;
        } else {
            return optionalRequest.get();
        }
    }

    public EventRequestStatusUpdateResult updateRequestsStatus(EventRequestStatusUpdateRequest updateRequest, long eventId, long initiatorId) {
        Event event = eventService.getEventByIdAndInitiatorId(eventId, initiatorId);
        String newStatus = updateRequest.getStatus();
        int participantLimit = event.getParticipantLimit();
        int confirmedRequests = event.getConfirmedRequests();

        //если для события лимит заявок равен 0 или отключена пре-модерация заявок, то подтверждение заявок не требуется
        if (participantLimit > 0 && event.getRequestModeration()) {

            //нельзя подтвердить заявку, если уже достигнут лимит по заявкам на данное событие (Ожидается код ошибки 409)
            if (confirmedRequests >= participantLimit) {
                throw new ParticipationRequestLimitException("Достигнут лимит по заявкам на событие " + eventId);
            }

            for (Long requestId : updateRequest.getRequestIds()) {
                Participation storageRequest = getRequestById(requestId);

                //статус можно изменить только у заявок, находящихся в состоянии ожидания
                if (RequestStatus.PENDING.toString().equals(storageRequest.getStatus())) {
                    if (confirmedRequests++ < participantLimit) {
                        storageRequest.setStatus(newStatus);

                        //если при подтверждении данной заявки, лимит заявок для события исчерпан,
                        // то все неподтверждённые заявки необходимо отклонить
                        if (confirmedRequests == participantLimit) {
                            rejectAllPendingRequests(eventId);
                            break;
                        }
                    }
                } else {
                    throw new ParticipationRequestInvalidStateException("Неверное состояние заявки " + requestId + " перед модерацией");
                }
            }
        }

        //обновить количество подтвержденных заявок на событие в БД
        EventRequestStatusUpdateResult updateResult = getEventRequestStatusUpdateResult(eventId);
        event.setConfirmedRequests(updateResult.getConfirmedRequests().size());
        eventRepository.save(event);

        return updateResult;
    }

    private void rejectAllPendingRequests(long eventId) {
        participationRepository.rejectAllPendingRequests(eventId);
    }

    private EventRequestStatusUpdateResult getEventRequestStatusUpdateResult(long eventId) {
        List<Participation> confirmed = participationRepository.findByEvent_IdAndStatusOrderById(eventId, RequestStatus.CONFIRMED.toString());
        List<Participation> rejected = participationRepository.findByEvent_IdAndStatusOrderById(eventId, RequestStatus.REJECTED.toString());

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(ParticipationDtoMapper.toParticipationRequestDtoList(confirmed))
                .rejectedRequests(ParticipationDtoMapper.toParticipationRequestDtoList(rejected))
                .build();
    }

    /**
     * Добавление запроса от текущего пользователя на участие в событии
     *
     * @param eventId     id события
     * @param requesterId id пользователя
     * @return созданный объект Participation
     */
    public Participation createParticipationRequest(long eventId, long requesterId) {
        Event event = eventService.getEventById(eventId);
        User requester = userService.getUserById(requesterId);

        //инициатор события не может добавить запрос на участие в своём событии (Ожидается код ошибки 409)
        if (event.getInitiator().getId() == requesterId) {
            throw new ParticipationRequestInitiatorException("Инициатор события " + eventId + "не может добавить запрос на участие в своём событии");
        }

        //нельзя участвовать в неопубликованном событии (Ожидается код ошибки 409)
        if (event.getState() != EventState.PUBLISHED) {
            throw new ParticipationRequestEventNotPublishedException("Событие " + eventId + "не опубликовано");
        }

        //нельзя добавить повторный запрос (Ожидается код ошибки 409)
        Participation oldRequest = getRequestByEventIdAndRequesterId(eventId, requesterId);
        if (oldRequest != null) {
            throw new ParticipationRequestDuplicationException("Нельзя добавить повторный запрос в событие" + eventId);
        }

        //если у события достигнут лимит запросов на участие - необходимо вернуть ошибку (Ожидается код ошибки 409)
        if (event.getParticipantLimit() > 0 && event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new ParticipationRequestLimitReachedException("Достигнут лимит запросов на участие в событии " + eventId);
        }

        //если для события отключена пре-модерация запросов на участие, то запрос должен автоматически перейти в состояние подтвержденного
        String newStatus = event.getParticipantLimit() > 0 && event.getRequestModeration() ? RequestStatus.PENDING.toString() : RequestStatus.CONFIRMED.toString();

        Participation request = Participation.builder()
                .created(LocalDateTime.now())
                .event(event)
                .user(requester)
                .status(newStatus)
                .build();

        return participationRepository.save(request);
    }

    /**
     * Отмена своего запроса на участие в событии
     *
     * @param requestId id запроса
     * @param userId    id пользователя
     * @return объект Participation
     */
    public Participation cancelParticipationRequest(long requestId, long userId) {
        Participation request = getRequestById(requestId);
        if (request.getUser().getId() != userId) {
            throw new ParticipationRequestNotFoundException("Запрос " + requestId + " не найден");
        }

        if (!RequestStatus.PENDING.toString().equals(request.getStatus())) {
            String oldStatus = request.getStatus();
            request.setStatus(RequestStatus.PENDING.toString());
            Participation storageRequest = participationRepository.save(request);

            //если заявка была подтверждена и это событие с ограничением участников и модерацией то уменьшить счетчик подтверждений
            if (RequestStatus.CONFIRMED.toString().equals(oldStatus)) {
                Event event = storageRequest.getEvent();
                if (event.getRequestModeration() && event.getParticipantLimit() > 0) {
                    event.setConfirmedRequests(event.getConfirmedRequests() - 1);
                }
                eventRepository.save(event);
            }
            return storageRequest;
        }

        return null;
    }
}