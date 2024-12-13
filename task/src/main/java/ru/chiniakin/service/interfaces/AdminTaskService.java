package ru.chiniakin.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import ru.chiniakin.model.request.*;
import ru.chiniakin.model.response.TaskResponse;

import java.util.UUID;

/**
 * Интерфейс сервиса админа для работы с задачами.
 *
 * @author ChiniakinD
 */
public interface AdminTaskService {

    /**
     * Добавляет новую задачу.
     *
     * @param request     объект запроса.
     * @param taskRequest модель задачи.
     */
    void createTask(HttpServletRequest request, CreateTaskRequest taskRequest);

    /**
     * Получает страницу задач с применением фильтров.
     *
     * @param tasksFilterRequest объект фильтрации.
     * @return страница задач.
     */
    Page<TaskResponse> getTasks(HttpServletRequest request, TasksFilterRequest tasksFilterRequest);

    /**
     * Получает задачу по ее id.
     *
     * @param request объект запроса.
     * @param id      id задачи.
     * @return задача.
     */
    TaskResponse getTaskById(HttpServletRequest request, UUID id);

    /**
     * Обновляет задачу по её id.
     *
     * @param id          id задачи.
     * @param request     объект запроса.
     * @param taskRequest модель задачи для обновления.
     */
    void updateTask(HttpServletRequest request, UUID id, UpdateTaskRequest taskRequest);

    /**
     * Обновляет приоритет задачи.
     */
    void updateTaskPriority(HttpServletRequest request, UUID id, UpdatePriorityModel updatePriorityModel);

    /**
     * Обновляет статус задачи.
     */
    void updateTaskStatus(HttpServletRequest request, UUID id, UpdateStatusModel updateStatusModel);

    /**
     * Обновляет исполнителя задачи.
     */
    void updateTaskAssigneeId(HttpServletRequest request, UUID id, UpdateAssigneeId updateAssigneeId);

    /**
     * Удаляет задачу по ее id.
     */
    void deleteTask(HttpServletRequest request, UUID id);

}
