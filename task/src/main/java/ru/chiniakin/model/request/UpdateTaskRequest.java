package ru.chiniakin.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Модель для редактирования задачи.
 *
 * @author ChiniakinD
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class UpdateTaskRequest {

    /**
     * Заголовок задачи
     */
    @Schema(name = "title", description = "Заголовок задачи", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("title")
    private String title;

    /**
     * Описание задачи
     */
    @Schema(name = "description", description = "Описание задачи", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("description")
    private String description;

}

