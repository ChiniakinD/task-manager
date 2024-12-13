package ru.chiniakin.service.interfaces;

import org.springframework.security.core.userdetails.UserDetailsService;
import ru.chiniakin.model.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.userdetails.UserDetails;

public interface JwtService {

    /**
     * Извлекаем JWT из заголовка Authorization
     */
    String extractJwtFromRequest(HttpServletRequest request);

    /**
     * Извлекает имя пользователя из JWT токена.
     */
    String extractUserName(String token);

    /**
     * Проверка токена на валидность
     *
     * @param token       токен
     * @param userDetails данные пользователя
     * @return true, если токен валиден
     */
    boolean isTokenValid(String token, UserDetails userDetails);

    /**
     * Создает токен для пользователя.
     *
     * @param userDetails пользователь.
     * @return токен.
     */
    String generateToken(SecurityUser userDetails);

    /**
     * Получение пользователя по имени пользователя
     * Нужен для Spring Security
     *
     * @return пользователь
     */
    UserDetailsService userDetailsService();

}
