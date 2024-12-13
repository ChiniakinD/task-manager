package ru.chiniakin.exception;

/**
 * Исключение, что запрашиваемый ресурс не найден.
 *
 * @author ChiniakinD
 */
public class NotFoundException extends RuntimeException {

    /**
     * @param message сообщение об ошибке.
     */
    public NotFoundException(String message) {
        super(message);
    }
}
