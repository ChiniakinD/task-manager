package ru.chiniakin.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import ru.chiniakin.model.response.CommentResponse;
import ru.chiniakin.model.request.CommentTextModel;

import java.util.UUID;

/**
 * Интерфейс сервиса для работы с комментариями.
 *
 * @author ChiniakinD
 */
public interface CommentService {

    /**
     * Отправляет комментарий к задаче по её id.
     *
     * @param id          id задачи.
     * @param request     объект запроса.
     * @param commentText объект текста комментария.
     */
    void addComment(UUID id, HttpServletRequest request, CommentTextModel commentText);

    /**
     * Получает комментарии задачи по его id.
     *
     * @param id id задачи.
     * @return комментарии.
     */
    Page<CommentResponse> getComments(UUID id, HttpServletRequest request, int page, int size);

    /**
     * Удаляет комментарий по его id.
     *
     * @param id      id комментария.
     * @param request объект запроса.
     */
    void deleteCommentById(UUID id, HttpServletRequest request);

}

