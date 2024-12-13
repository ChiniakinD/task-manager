package ru.chiniakin.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.chiniakin.exception.AccessDeniedException;
import ru.chiniakin.model.UserLogin;

/**
 * Сервис для выполнения аутентификации пользователя.
 *
 * @author ChiniakinD
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HttpAuthService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    @Value("${auth.url}")
    private String url;
    @Value("${auth.endpoint}")
    private String endPoint;

    /**
     * Выполняет аутентификацию пользователя по заданному JWT токену.
     *
     * @param jwt JWT токен для аутентификации
     * @return логин пользователя, если аутентификация прошла успешно
     */
    public String auth(String jwt) {
        ResponseEntity<String> response = restTemplate.postForEntity(url + endPoint, jwt, String.class);
        if (response.getStatusCode().isError()) {
            log.error("Auth error: {}, status: {}", response.getBody(), response.getStatusCode());
            throw new AccessDeniedException("Невалидный токен");
        }
        UserLogin body;
        try {
            body = objectMapper.readValue(response.getBody(), UserLogin.class);
        } catch (JsonProcessingException e) {
            throw new AccessDeniedException(e.getMessage());
        }
        return body.getLogin();
    }
}
