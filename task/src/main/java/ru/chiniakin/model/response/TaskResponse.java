package ru.chiniakin.model.response;

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

import java.util.List;

/**
 * Task
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class TaskResponse {

    /**
     * id задачи
     */
    @NotNull
    @Schema(name = "id", description = "id задачи", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    private String id;

    /**
     * Заголовок задачи
     */
    @NotNull
    @Schema(name = "title", description = "Заголовок задачи", requiredMode = Schema.RequiredMode.REQUIRED)
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
     * ID автора задачи
     */
    @Schema(name = "creatorId", description = "id автора задачи", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("creatorId")
    private String creatorId;

    /**
     * ID исполнителя задачи
     */
    @Schema(name = "assigneeId", description = "id исполнителя задачи", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("assigneeId")
    private String assigneeId;

    @Schema(name = "comments", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("comments")
    private List<CommentResponse> comments;

}

