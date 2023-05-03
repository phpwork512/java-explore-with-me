package ru.practicum.ewm.service.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.service.comments.CommentService;
import ru.practicum.ewm.service.comments.model.dto.CommentDto;
import ru.practicum.ewm.service.comments.model.dto.CommentDtoMapper;
import ru.practicum.ewm.service.comments.model.dto.NewCommentDto;
import ru.practicum.ewm.service.comments.model.dto.UpdateCommentDto;
import ru.practicum.ewm.service.event.model.dto.EventDtoMapper;
import ru.practicum.ewm.service.event.model.dto.EventFullDto;
import ru.practicum.ewm.service.event.model.dto.EventShortDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewm.service.common.EwmConstants.DATE_TIME_FORMAT;

@RestController
@RequestMapping(path = "/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventPublicController {
    private final EventService eventService;
    private final CommentService commentService;

    @GetMapping("/{eventId}")
    @ResponseStatus(HttpStatus.OK)
    public EventFullDto getEventByIdPublic(@PathVariable long eventId, HttpServletRequest request) {
        String remoteIp = request.getRemoteAddr();
        String requestUri = request.getRequestURI();

        log.info("Public get event: eventId = {}, remoteIp = {}, requestUri = {}", eventId, remoteIp, requestUri);
        return EventDtoMapper.toEventFullDto(eventService.getEventByIdPublic(eventId, requestUri, remoteIp));
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<EventShortDto> getAllEventsPublic(@RequestParam(required = false) String text,
                                                  @RequestParam(name = "categories", required = false) List<Integer> categoriesIdList,
                                                  @RequestParam(required = false) Boolean paid,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeStart,
                                                  @RequestParam(required = false) @DateTimeFormat(pattern = DATE_TIME_FORMAT) LocalDateTime rangeEnd,
                                                  @RequestParam(required = false) Boolean onlyAvailable,
                                                  @RequestParam(required = false) String sort,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "10") int size,
                                                  HttpServletRequest request) {
        String remoteIp = request.getRemoteAddr();
        String requestUri = request.getRequestURI();

        log.info("Public get all events: text = {}, categoriesIdList = {}, paid = {}, rangeStart = {}, rangeEnd = {}, onlyAvailable = {}, sort = {}, from = {}, size = {}",
                text, categoriesIdList, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
        log.info("Public get all events: remoteIp = {}, requestUri = {}", remoteIp, requestUri);

        return EventDtoMapper.toEventShortDtoList(
                eventService.getAllEventsPublic(text, categoriesIdList, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, requestUri, remoteIp)
        );
    }

    /**
     * API запрос на создание нового комментария
     * ручка POST /events/{eventId}/comments/{authorId}
     *
     * @param newCommentDto данные комментария
     * @param eventId       ид события
     * @param authorId      ид автора комментария
     * @return CommentDto
     */
    @PostMapping("/{eventId}/comments/{authorId}")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createNewComment(@Valid @RequestBody NewCommentDto newCommentDto,
                                       @Positive @PathVariable long eventId,
                                       @Positive @PathVariable long authorId) {
        log.info("Create new comment for event: eventId = {}, authorId = {}, newCommentDto = {}", authorId, eventId, newCommentDto);
        return CommentDtoMapper.toCommentDto(commentService.createNewComment(newCommentDto, eventId, authorId));
    }

    /**
     * API запрос на редактирование своего комментария
     * ручка PATCH /events/{eventId}/comments/{authorId}
     *
     * @param updateCommentDto данные комментария
     * @param eventId          ид события
     * @param authorId         ид автора комментария
     * @return CommentDto
     */
    @PatchMapping("/{eventId}/comments/{authorId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentDto updateCommentByAuthor(@Valid @RequestBody UpdateCommentDto updateCommentDto,
                                            @Positive @PathVariable long eventId,
                                            @Positive @PathVariable long authorId) {
        log.info("Update comment by author for event: eventId = {}, authorId = {}, updateCommentDto = {}", authorId, eventId, updateCommentDto);
        return CommentDtoMapper.toCommentDto(commentService.updateCommentByAuthor(updateCommentDto, eventId, authorId));
    }

    /**
     * API запрос на получение комментариев для события
     * ручка GET /events/{eventId}/comments
     *
     * @param eventId id события
     * @param from    начало выборки
     * @param size    размер выборки
     * @return список объектов CommentDto
     */
    @GetMapping("/{eventId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getCommentsForEvent(@Positive @PathVariable long eventId,
                                                @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Get comments for event: eventId = {}, from = {}, size = {}", eventId, from, size);
        return CommentDtoMapper.toCommentDtoList(commentService.getCommentsForEvent(eventId, from, size));
    }

    /**
     * API запрос на удаление автором комментария для события
     * ручка DELETE /{eventId}/comments/{authorId}/{commentId}
     *
     * @param eventId   id события
     * @param authorId  ид автора комментария
     * @param commentId ид комментария
     */
    @DeleteMapping("/{eventId}/comments/{authorId}/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(@Positive @PathVariable long eventId,
                              @Positive @PathVariable long authorId,
                              @Positive @PathVariable long commentId) {
        log.info("Delete comment for event: eventId = {}, authorId = {}, commentId = {}", eventId, authorId, commentId);
        commentService.deleteComment(commentId, eventId, authorId);
    }

}