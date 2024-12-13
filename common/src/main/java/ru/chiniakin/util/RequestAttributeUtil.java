package ru.chiniakin.util;

import jakarta.servlet.http.HttpServletRequest;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.AttributeName;

/**
 * Класс для работы с атрибутами запроса.
 *
 * @author ChiniakinD
 */
public class RequestAttributeUtil {

    /**
     * Получает значение атрибута из запроса.
     *
     * @param request       запрос.
     * @param attributeName имя атрибута.
     * @return значение атрибута или null, если атрибут не найден.
     */
    public static User get(HttpServletRequest request, AttributeName attributeName) {
        return (User) request.getAttribute(attributeName.getValue());
    }

    /**
     * Устанавливает значение атрибута в запросе.
     *
     * @param request       запрос.
     * @param attributeName имя атрибута.
     * @param user          значение атрибута.
     */
    public static void set(HttpServletRequest request, AttributeName attributeName, User user) {
        request.setAttribute(attributeName.getValue(), user);
    }
}
