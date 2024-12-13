package ru.chiniakin.service;

import org.modelmapper.ModelMapper;
import ru.chiniakin.entity.Role;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.RoleEnum;
import ru.chiniakin.exception.AccessDeniedException;
import ru.chiniakin.exception.AuthorizationException;
import ru.chiniakin.model.RoleModel;
import ru.chiniakin.model.RoleResponse;
import ru.chiniakin.repository.RoleRepository;
import ru.chiniakin.repository.UserRepository;
import ru.chiniakin.service.interfaces.RoleService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Реализация сервиса {@link RoleService} для работы с ролями пользователей.
 *
 * @author ChiniakinD
 */
@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final JwtServiceImpl jwtCore;

    private final ModelMapper modelMapper;


    /**
     * {@inheritDoc}
     */
    @Override
    public void saveRole(RoleModel roleModel, HttpServletRequest request) {
        String authLogin = jwtCore.extractUserName(request.getAttribute("jwt").toString());
        Optional<User> userByLogin = userRepository.findUserByLogin(authLogin);
        checkPermissionsByRole(userByLogin, RoleEnum.ADMIN);

        User changedUser = userRepository.findUserByLoginOrThrow(roleModel.getLogin());
        for (RoleEnum roleEnum : roleModel.getRoles()) {
            Role role = roleRepository.findByRole(roleEnum);
            changedUser.addRole(role);
        }
        userRepository.save(changedUser);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<RoleResponse> getRoles(String login, HttpServletRequest request) {
        userRepository.findUserByLoginOrThrow(login);
        String jwt = jwtCore.extractJwtFromRequest(request);
        String currentUserLogin = jwtCore.extractUserName(jwt);
        if (!login.equals(currentUserLogin)) {
            checkPermissionsByRole(userRepository.findUserByLogin(currentUserLogin), RoleEnum.ADMIN);
        }
        User user = userRepository.findUserByLoginOrThrow(login);
        return user.getRoles()
                .stream()
                .map(x -> modelMapper.map(x, RoleResponse.class))
                .collect(Collectors.toSet());
    }


    private void checkPermissionsByRole(Optional<User> user, RoleEnum requiredRole) {
        if (user.isEmpty()) {
            throw new AuthorizationException("Пользователь не найден");
        }

        boolean hasRequiredRole = user.get().getRoles().stream()
                .anyMatch(role -> requiredRole.equals(role.getRole()));

        if (!hasRequiredRole) {
            throw new AccessDeniedException("У пользователя нет прав");
        }
    }

}
