package ru.chiniakin.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import ru.chiniakin.model.response.TaskResponse;
import ru.chiniakin.model.request.UpdateStatusModel;
import ru.chiniakin.model.request.UserTaskFilterRequest;

import java.util.UUID;

/**
 * Интерфейс сервиса исполнителя для работы с задачами.
 *
 * @author ChiniakinD
 */
public interface UserTaskService {

    /**
     * Получает страницу задач с применением фильтров.
     *
     * @param userTaskFilterRequest объект фильтрации.
     * @return страница задач.
     */
    Page<TaskResponse> getTasks(HttpServletRequest request, UserTaskFilterRequest userTaskFilterRequest);

    /**
     * Получает задачу по ее id.
     *
     * @param request объект запроса.
     * @param id      id задачи.
     * @return задача.
     */
    TaskResponse getTaskById(HttpServletRequest request, UUID id);

    /**
     * Обновляет статус задачи.
     */
    void updateTaskStatus(HttpServletRequest request, UUID id, UpdateStatusModel updateStatusModel);

}
