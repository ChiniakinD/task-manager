package ru.chiniakin.service.interfaces;

import jakarta.servlet.http.HttpServletRequest;
import ru.chiniakin.model.UserEditModel;

/**
 * Интерфейс сервиса для редактирования пользователей.
 *
 * @author ChiniakinD
 */
public interface UserEditService {

    /**
     * Редактирует профиль пользователя.
     *
     * @param request       объект запроса.
     * @param userEditModel модель для редактирования пользователя.
     */
    void editUser(HttpServletRequest request, UserEditModel userEditModel);
}
