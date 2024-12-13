package ru.chiniakin.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.chiniakin.enums.Status;

/**
 * Модель для редактирования статуса задачи.
 *
 * @author ChiniakinD
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusModel {

    /**
     * Статус задачи
     */
    @Schema(name = "status", description = "Статус задачи", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("status")
    private Status status;

}
