package ru.chiniakin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import ru.chiniakin.exception.BadRequestException;

/**
 * Приоритет задачи
 */
@Getter
public enum Priority {

    HIGH("high"),

    MEDIUM("medium"),

    LOW("low");

    private final String value;

    Priority(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static Priority fromValue(String value) {
        for (Priority b : Priority.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new BadRequestException("Unexpected value '" + value + "'");
    }

}
