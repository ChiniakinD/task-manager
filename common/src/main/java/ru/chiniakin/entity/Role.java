package ru.chiniakin.entity;

import ru.chiniakin.enums.RoleEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

/**
 * Роль пользователя.
 *
 * @author ChiniakinD
 */
@Entity
@Getter
@Setter
@Table(name = "roles", schema = "auth")
@Schema(name = "Роль пользователя.")
public class Role {

    @Id
    @Schema(name = "Id роли пользователя.")
    private UUID id;

    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    @Schema(name = "Значение роли пользователя.")
    private RoleEnum role;

    @Column(name = "description")
    @Schema(name = "Описание роли")
    private String description;

}
