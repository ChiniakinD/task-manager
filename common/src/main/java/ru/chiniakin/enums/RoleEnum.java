package ru.chiniakin.enums;

import lombok.Getter;

/**
 * Перечисление ролей пользователей.
 *
 * @author ChiniakinD
 */
@Getter
public enum RoleEnum {

    ADMIN("ADMIN"),
    USER("USER");

    private final String value;

    RoleEnum(String value) {
        this.value = value;
    }

}
