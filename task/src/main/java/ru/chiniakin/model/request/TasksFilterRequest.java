package ru.chiniakin.model.request;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestParam;

import java.beans.ConstructorProperties;
import java.util.UUID;

/**
 * Для фильтрации задач на основе параметров.
 *
 * @author ChiniakinD
 */
public record TasksFilterRequest(
        @Parameter(name = "authorId", description = "ID автора задач", in = ParameterIn.QUERY) @Valid @RequestParam(value = "authorId", required = false) UUID authorId,
        @Parameter(name = "assigneeId", description = "ID исполнителя задач", in = ParameterIn.QUERY) @Valid @RequestParam(value = "assigneeId", required = false) UUID assigneeId,
        @Parameter(name = "status", description = "Статус задач", in = ParameterIn.QUERY) @Valid @RequestParam(value = "status", required = false) String status,
        @Parameter(name = "priority", description = "Приоритет задач", in = ParameterIn.QUERY) @Valid @RequestParam(value = "priority", required = false) String priority,
        @Parameter(name = "page", description = "Номер страницы", in = ParameterIn.QUERY) @Valid @RequestParam(value = "page", required = false, defaultValue = "0") Integer page,
        @Parameter(name = "size", description = "Размер страницы", in = ParameterIn.QUERY) @Valid @RequestParam(value = "size", required = false, defaultValue = "10") Integer size

) {

    @ConstructorProperties(
            value = {"authorId", "assigneeId", "status", "priority", "page", "size"}
    )
    public TasksFilterRequest(UUID authorId, UUID assigneeId, String status, String priority, Integer page, Integer size) {
        this.authorId = authorId;
        this.assigneeId = assigneeId;
        this.status = status;
        this.priority = priority;
        this.page = page == null ? 0 : page - 1;
        this.size = size == null ? 5 : size;
    }

}
