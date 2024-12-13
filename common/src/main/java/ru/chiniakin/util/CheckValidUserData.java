package ru.chiniakin.util;

import lombok.RequiredArgsConstructor;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import ru.chiniakin.exception.BadRequestException;
import ru.chiniakin.model.UserEditModel;
import ru.chiniakin.repository.UserRepository;

/**
 * Сервис для проверки корректности данных пользователя.
 *
 * @author ChiniakinD
 */
@Component
@RequiredArgsConstructor
public class CheckValidUserData {

    private final UserRepository userRepository;

    /**
     * Проверяет данные на корректность при редактировании пользователя.
     *
     * @param userEditModel данные для редактирования.
     */
    public void checkUser(UserEditModel userEditModel) {
        if (userRepository.existsByLogin(userEditModel.getName()) || userRepository.existsByEmail(userEditModel.getEmail())) {
            throw new BadRequestException("Пользователь уже зарегистрирован");
        }
        if (userEditModel.getPassword().length() < 6 || !userEditModel.getPassword().matches("^[a-zA-Z0-9]+$")) {
            throw new BadRequestException("Некорректный пароль");
        }
        if (userEditModel.getName().length() < 6 || !userEditModel.getName().matches("^[a-zA-Z0-9]+$")) {
            throw new BadRequestException("Некорректный логин");
        }
        if (!EmailValidator.getInstance().isValid(userEditModel.getEmail())) {
            throw new BadRequestException("Некорректный email");
        }
    }

}
