package ru.chiniakin.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Модель для аутентификации пользователя.
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@RequiredArgsConstructor
@Schema(name = "SignInUserRequest", description = "Модель для аутентификации пользователя.")
public class SignInUserRequest {

  @Schema(name = "login", description = "Логин пользователя", example = "admin")
  private String login;

  @Schema(name = "password", description = "Пароль пользователя", example = "admin")
  private String password;

}

