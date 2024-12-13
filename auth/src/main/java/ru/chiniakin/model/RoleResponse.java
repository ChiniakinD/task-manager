package ru.chiniakin.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import ru.chiniakin.enums.RoleEnum;

/**
 * Модель роли пользователя для ответа.
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
public class RoleResponse {

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Schema(name = "Значение роли пользователя.")
    private RoleEnum role;

    @Column(name = "description")
    @Schema(name = "Описание роли")
    private String description;

}
