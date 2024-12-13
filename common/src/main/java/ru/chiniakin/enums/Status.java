package ru.chiniakin.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import ru.chiniakin.exception.BadRequestException;

/**
 * Статус задачи
 */
@Getter
public enum Status {

    PENDING("pending"),

    IN_PROGRESS("in_progress"),

    COMPLETED("completed");

    private final String value;

    Status(String value) {
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
    public static Status fromValue(String value) {
        for (Status b : Status.values()) {
            if (b.value.equals(value)) {
                return b;
            }
        }
        throw new BadRequestException("Unexpected value '" + value + "'");
    }

}
