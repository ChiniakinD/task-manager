package ru.chiniakin.exception;

/**
 * Исключение, которое выбрасывается в случае некорректного запроса.
 *
 * @author ChiniakinD
 */
public class BadRequestException extends RuntimeException {

    /**
     * @param message сообщение об ошибке.
     */
    public BadRequestException(String message) {
        super(message);
    }

}