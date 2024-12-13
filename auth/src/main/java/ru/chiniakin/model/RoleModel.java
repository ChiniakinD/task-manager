package ru.chiniakin.model;

import ru.chiniakin.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

/**
 * Модель для сохранения ролей пользователя.
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@Schema(name = "RoleModel", description = "Модель для сохранения перечня ролей пользователю.")
public class RoleModel {

    @Schema(name = "login", description = "Логин пользователя", example = "user12")
    private String login;

    @Schema(name = "roles", description = "Набор ролей пользователю", example = "[\"ADMIN\", \"USER\"]")
    private Set<RoleEnum> roles;

}