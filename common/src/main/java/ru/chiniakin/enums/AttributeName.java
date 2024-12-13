package ru.chiniakin.enums;

import lombok.Getter;

/**
 * Перечисление атрибутов для проекта.
 *
 * @author ChiniakinD
 */
@Getter
public enum AttributeName {

    /**
     * JSON Web Token.
     */
    JWT("jwt"),
    /**
     * Логин пользователя.
     */
    LOGIN("login");

    private final String value;

    AttributeName(String value) {
        this.value = value;
    }
}
