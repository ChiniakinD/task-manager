package ru.chiniakin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.chiniakin.model.ApiError;
import ru.chiniakin.model.SignInUserRequest;
import ru.chiniakin.model.SignUpUserRequest;
import ru.chiniakin.model.UserLogin;
import ru.chiniakin.service.interfaces.AuthService;

/**
 * Контроллер для регистрации и аутентификации пользователей.
 *
 * @author ChiniakinD
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "AuthController", description = "Контроллер для регистрации и аутентификации пользователей.")
public class AuthController {

    private final AuthService authService;

    /**
     * Выполняет регистрацию нового пользователя.
     *
     * @param signUpUserRequest модель для регистрации.
     */
    @Operation(summary = "Регистрация нового пользователя", description = "Создает нового пользователя согласно модели.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь зарегистрирован"),
            @ApiResponse(responseCode = "400", description = "Неверные данные.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            }),
            @ApiResponse(responseCode = "500", description = "Connection refused.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            })
    })
    @PostMapping("/signup")
    public void signup(@RequestBody SignUpUserRequest signUpUserRequest) {
        authService.signUp(signUpUserRequest);
    }

    /**
     * Выполняет аутентификацию пользователя и возвращает JWT токен.
     *
     * @param signInUserRequest модель для входа.
     * @return JWT токен.
     */
    @Operation(summary = "Аутентификация пользователя", description = "Возвращает токен при успешной аутентификации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Аутентификация прошла успешно"),
            @ApiResponse(responseCode = "401", description = "Неверный пароль.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            }),
            @ApiResponse(responseCode = "400", description = "Неверные данные.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            }),
            @ApiResponse(responseCode = "500", description = "Connection refused.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            })
    })
    @PostMapping("/signin")
    public String signIn(@RequestBody SignInUserRequest signInUserRequest) {
        return authService.signIn(signInUserRequest);
    }

    /**
     * Выполняет аутентификацию пользователя и возвращает JWT токен.
     *
     * @return UserLogin
     */
    @Operation(summary = "Валидация токена", description = "Проверяет токен и возвращает имя пользователя при удачной валидации.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Валидация прошла успешно", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = UserLogin.class))
            }),
            @ApiResponse(responseCode = "401", description = "Токен невалидный.", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            })
    })
    @PostMapping("/validation")
    public UserLogin validation(@RequestBody String jwt) {
        return authService.validation(jwt);
    }

}