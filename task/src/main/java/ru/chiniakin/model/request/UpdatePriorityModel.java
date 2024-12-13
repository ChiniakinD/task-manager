package ru.chiniakin.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.chiniakin.enums.Priority;

/**
 * Модель для редактирования приоритета задачи.
 *
 * @author ChiniakinD
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePriorityModel {

    /**
     * Приоритет задачи
     */
    @Schema(name = "priority", description = "Приоритет задачи", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("priority")
    private Priority priority;

}
