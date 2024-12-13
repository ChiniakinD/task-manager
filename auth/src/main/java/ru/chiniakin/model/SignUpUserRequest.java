package ru.chiniakin.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Модель для регистрации нового пользователя.
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@Accessors(chain = true)
@NoArgsConstructor
@Schema(name = "SignUpUserRequest", description = "Модель для регистрации пользователя.")
public class SignUpUserRequest {

  @Schema(name = "login", description = "Логин пользователя", example = "user123")
  private String login;

  @Schema(name = "password", description = "Пароль пользователя", example = "user123")
  private String password;

  @Schema(name = "email", description = "Email пользователя", example = "user123@test.me")
  private String email;

}

