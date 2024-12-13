package ru.chiniakin.service.interfaces;

import ru.chiniakin.model.RoleModel;
import jakarta.servlet.http.HttpServletRequest;
import ru.chiniakin.model.RoleResponse;

import java.util.Set;

/**
 * Интерфейс сервиса для работы с ролями пользователей.
 */
public interface RoleService {

    /**
     * Выполняет сохранение ролей пользователя.
     *
     * @param roleModel модельс ролями.
     * @param request   запрос для получения токена.
     */
    void saveRole(RoleModel roleModel, HttpServletRequest request);

    /**
     * Выполняет получения ролей пользователя по его логину.
     *
     * @param login   логин пользователя.
     * @param request запрос для получения токена.
     * @return роли пользователя.
     */
    Set<RoleResponse> getRoles(String login, HttpServletRequest request);

}
