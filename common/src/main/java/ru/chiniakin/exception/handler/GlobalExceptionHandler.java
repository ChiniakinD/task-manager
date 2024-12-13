package ru.chiniakin.exception.handler;

import org.postgresql.util.PSQLException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.chiniakin.exception.*;
import ru.chiniakin.model.ApiError;

/**
 * Глобальный обработчик исключений.
 *
 * @author ChiniakinD
 */
@ControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDeniedException(AccessDeniedException e) {
        return buildExceptionResponse(
                 HttpStatus.FORBIDDEN,
                "Нет прав доступа",
                e
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> httpMessageNotReadableException(HttpMessageNotReadableException e) {
        return buildExceptionResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка полученной сущности",
                e
        );
    }

    @ExceptionHandler(AuthorizationException.class)
    public ResponseEntity<ApiError> authorizationException(AuthorizationException e) {
        return buildExceptionResponse(
                HttpStatus.UNAUTHORIZED,
                "UNAUTHORIZED",
                e
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequestException(BadRequestException e) {
        return buildExceptionResponse(
                HttpStatus.BAD_REQUEST,
                "Некорректный запрос",
                e
        );
    }

    @ExceptionHandler(PSQLException.class)
    public ResponseEntity<ApiError> pSQLException(PSQLException e) {
        return buildExceptionResponse(
                HttpStatus.BAD_REQUEST,
                "Ошибка ограничений базы данных",
                e
        );
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> notFoundException(NotFoundException e) {
        return buildExceptionResponse(
                HttpStatus.NOT_FOUND,
                "Данные не найдены",
                e
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        return buildExceptionResponse(
                HttpStatus.BAD_REQUEST,
                "Некорректные данные",
                e
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> httpRequestMethodNotSupportedException(HttpRequestMethodNotSupportedException e) {
        return buildExceptionResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "Тип запроса не поддерживается",
                e
        );
    }


    /**
     * Создает объект с информацией об ошибке.
     *
     * @param httpStatus статус ответа.
     * @param errorText  текст сообщения об ошибке.
     * @param throwable  исключение, содержащее информацию об ошибке.
     * @return объеки {@link ApiError}.
     */
    private ResponseEntity<ApiError> buildExceptionResponse(HttpStatus httpStatus, String errorText, Throwable throwable) {
        ApiError apiError = new ApiError()
                .setStatus(httpStatus.getReasonPhrase())
                .setDetails(throwable.getMessage())
                .setMessage(errorText);
        return ResponseEntity
                .status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(apiError);
    }
}
