package ru.chiniakin.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.chiniakin.enums.Priority;
import ru.chiniakin.enums.Status;

import java.util.UUID;

/**
 * Модель для новой задачи.
 *
 * @author ChiniakinD
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CreateTaskRequest {

    /**
     * Заголовок задачи
     */
    @Schema(name = "title", description = "Заголовок задачи", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("title")
    private String title;

    /**
     * Описание задачи
     */
    @NotNull
    @Schema(name = "description", description = "Описание задачи", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("description")
    private String description;

    /**
     * Статус задачи
     */
    @NotNull
    @Schema(name = "status", description = "Статус задачи", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("status")
    private Status status;

    /**
     * Приоритет задачи
     */
    @NotNull
    @Schema(name = "priority", description = "Приоритет задачи", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("priority")
    private Priority priority;

    /**
     * ID исполнителя задачи
     */
    @Schema(name = "assigneeId", description = "ID исполнителя задачи", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("assigneeId")
    private UUID assigneeId;

}

