package ru.chiniakin.service;

import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import ru.chiniakin.entity.Role;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.RoleEnum;
import ru.chiniakin.exception.AccessDeniedException;
import ru.chiniakin.exception.BadRequestException;
import ru.chiniakin.model.SecurityUser;
import ru.chiniakin.model.SignInUserRequest;
import ru.chiniakin.model.SignUpUserRequest;
import ru.chiniakin.model.UserLogin;
import ru.chiniakin.repository.RoleRepository;
import ru.chiniakin.repository.UserRepository;
import ru.chiniakin.service.interfaces.AuthService;
import ru.chiniakin.service.interfaces.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.chiniakin.util.CheckValidUserData;

/**
 * Реализация сервиса {@link AuthService} для регистрации и аутентификации пользователей.
 *
 * @author ChiniakinD
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final ModelMapper modelMapper;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final CheckValidUserData checkValidUserData;

    /**
     * {@inheritDoc}
     */
    @Override
    public void signUp(SignUpUserRequest userInfoRequest) {
        checkUser(userInfoRequest);
        User user = modelMapper.map(userInfoRequest, User.class);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Role userRole = roleRepository.findByRole(RoleEnum.USER);

        user.addRole(userRole);
        userRepository.save(user);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String signIn(SignInUserRequest signInUserRequest) {
        checkAuthInformation(signInUserRequest);
        return jwtService.generateToken(
                new SecurityUser()
                        .setUserName(signInUserRequest.getLogin())
                        .setPassword(signInUserRequest.getPassword())
        );
    }

    @Override
    public UserLogin validation(String jwt) {
        String login = jwtService.extractUserName(jwt);
        UserDetails userDetails = jwtService.userDetailsService().loadUserByUsername(login);
        if (jwtService.isTokenValid(jwt, userDetails)) {
            return new UserLogin()
                    .setLogin(login);
        } else {
            throw new AccessDeniedException("Невалидный токен");
        }
    }

    /**
     * Проверяет данные при аутентификации.
     *
     * @param signInUserRequest модель для аутентификации.
     */
    private void checkAuthInformation(SignInUserRequest signInUserRequest) {
        User user = userRepository.findUserByLogin(signInUserRequest.getLogin()).orElseThrow(
                () -> new BadRequestException("Пользователя с логином " + signInUserRequest.getLogin() + " не существует."));
        if (!passwordEncoder.matches(signInUserRequest.getPassword(), user.getPassword())) {
            throw new AccessDeniedException("Неправильный пароль");
        }
    }

    /**
     * Проверяет данные на корректность при регистрации.
     *
     * @param userInfoRequest данные для регистрации.
     */
    private void checkUser(SignUpUserRequest userInfoRequest) {
        if (userRepository.existsByLogin(userInfoRequest.getLogin()) || userRepository.existsByEmail(userInfoRequest.getEmail())) {
            throw new BadRequestException("Пользователь уже зарегистрирован");
        }
        if (userInfoRequest.getPassword().length() < 6 || !userInfoRequest.getPassword().matches("^[a-zA-Z0-9]+$")) {
            throw new BadRequestException("Некорректный пароль");
        }
        if (userInfoRequest.getLogin().length() < 6 || !userInfoRequest.getLogin().matches("^[a-zA-Z0-9]+$")) {
            throw new BadRequestException("Некорректный логин");
        }
        if (!EmailValidator.getInstance().isValid(userInfoRequest.getEmail())) {
            throw new BadRequestException("Некорректный email");
        }
    }

}
