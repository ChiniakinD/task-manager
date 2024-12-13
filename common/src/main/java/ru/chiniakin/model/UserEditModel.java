package ru.chiniakin.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Модель пользователя для редактирования профиля.
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@RequiredArgsConstructor
public class UserEditModel {

    @Schema(name = "name", example = "Dmitrii", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("name")
    private String name;

    @Schema(name = "email", example = "Dmitrii123@mail.ru", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("email")
    private String email;

    @Schema(name = "password", example = "123456", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("password")
    private String password;

}
