package ru.practicum.ewm.service.comments;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.service.comments.model.Comment;
import ru.practicum.ewm.service.comments.model.dto.NewCommentDto;
import ru.practicum.ewm.service.comments.model.dto.UpdateCommentDto;
import ru.practicum.ewm.service.common.models.EventState;
import ru.practicum.ewm.service.common.pagination.PaginationCalculator;
import ru.practicum.ewm.service.event.EventService;
import ru.practicum.ewm.service.event.model.Event;
import ru.practicum.ewm.service.exceptions.CommentEventIsNotPublishedException;
import ru.practicum.ewm.service.exceptions.CommentNotFoundException;
import ru.practicum.ewm.service.user.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CommentService {
    public final CommentRepository commentRepository;
    public final EventService eventService;
    public final UserService userService;

    public Comment getCommentById(long commentId) {
        Optional<Comment> optionalComment = commentRepository.findById(commentId);
        if (optionalComment.isEmpty()) {
            throw new CommentNotFoundException("Комментарий " + commentId + " не найден");
        } else {
            return optionalComment.get();
        }
    }

    /**
     * получить страницу комментариев для события
     *
     * @param eventId id события
     * @param from    начало выборки
     * @param size    размер выборки
     * @return список объектов Comment
     */
    public List<Comment> getCommentsForEvent(long eventId, int from, int size) {
        Pageable page = PaginationCalculator.getPage(from, size);
        return commentRepository.findByEvent_IdOrderByCreated(eventId, page);
    }

    /**
     * Создать новый комментарий. Событие должно быть опубликовано
     *
     * @param newCommentDto данные комментария
     * @param eventId       ид события
     * @param authorId      ид автора комментария
     * @return объект Comment
     */
    public Comment createNewComment(NewCommentDto newCommentDto, long eventId, long authorId) {
        Event event = eventService.getEventById(eventId);

        if (event.getState() != EventState.PUBLISHED) {
            throw new CommentEventIsNotPublishedException("Невозможно добавить комментарий. Событие " + eventId + " не опубликовано");
        }

        Comment newComment = Comment.builder()
                .event(event)
                .author(userService.getUserById(authorId))
                .text(newCommentDto.getText())
                .created(LocalDateTime.now())
                .build();

        return commentRepository.save(newComment);
    }

    /**
     * редактировать текст комментария
     *
     * @param updateCommentDto данные для обновления
     * @param userId           id текущего пользователя
     * @return обновлённый объект Comment
     */
    public Comment updateCommentByAuthor(UpdateCommentDto updateCommentDto, long eventId, long userId) {
        Comment storageComment = getCommentById(updateCommentDto.getId());
        if (storageComment.getEvent().getId() != eventId || storageComment.getAuthor().getId() != userId) {
            throw new CommentNotFoundException("Комментарий " + updateCommentDto.getId() + " от пользователя " + userId + " на событие " + eventId + " не найден");
        }

        storageComment.setText(updateCommentDto.getText());
        return commentRepository.save(storageComment);
    }

    /**
     * удаление комментария
     *
     * @param commentId ид комментария
     * @param eventId   ид события
     * @param authorId  ид автора
     */
    public void deleteComment(long commentId, long eventId, long authorId) {
        Comment storageComment = getCommentById(commentId);
        if (storageComment.getEvent().getId() != eventId || storageComment.getAuthor().getId() != authorId) {
            throw new CommentNotFoundException("Комментарий " + commentId + " от пользователя " + authorId + " на событие " + eventId + " не найден");
        }

        commentRepository.delete(storageComment);
    }
}
