package ru.chiniakin.exception;

/**
 * Исключение об ошибке авторизации.
 *
 * @author ChiniakinD
 */
public class AuthorizationException extends RuntimeException {

    /**
     * @param message сообщение об ошибке.
     */
    public AuthorizationException(String message) {
        super(message);
    }
}
