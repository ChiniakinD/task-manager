package ru.chiniakin.service.interfaces;

import ru.chiniakin.model.SignInUserRequest;
import ru.chiniakin.model.SignUpUserRequest;
import ru.chiniakin.model.UserLogin;

/**
 * Интерфейс сервиса для регистрации и аутентификации пользователей.
 */
public interface AuthService {

    /**
     * Выполняет регистрацию нового пользователя.
     *
     * @param userInfoRequest модель для регистрации.
     */
    void signUp(SignUpUserRequest userInfoRequest);

    /**
     * Выполняет аутентификацию пользователя.
     *
     * @param signInUserRequest модель для аутентификации.
     * @return JWT токен для аутентифицированного пользователя.
     */
    String signIn(SignInUserRequest signInUserRequest);

    UserLogin validation(String jwt);
}