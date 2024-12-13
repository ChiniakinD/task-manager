package ru.chiniakin.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Модель для редактирования исполнителя задачи.
 *
 * @author ChiniakinD
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateAssigneeId {

    /**
     * id исполнителя задачи
     */
    @Schema(name = "assigneeId", description = "id исполнителя задачи", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("assignee_id")
    private String assignee;

}
