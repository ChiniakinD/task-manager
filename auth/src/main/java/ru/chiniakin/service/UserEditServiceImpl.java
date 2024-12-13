package ru.chiniakin.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.AttributeName;
import ru.chiniakin.model.UserEditModel;
import ru.chiniakin.repository.UserRepository;
import ru.chiniakin.service.interfaces.UserEditService;
import ru.chiniakin.util.CheckValidUserData;
import ru.chiniakin.util.RequestAttributeUtil;

/**
 * Реализация сервиса {@link UserEditService} для работы с пользователями.
 *
 * @author ChiniakinD
 */
@Service
@RequiredArgsConstructor
public class UserEditServiceImpl implements UserEditService {

    private final UserRepository userRepository;
    private final ModelMapper mergeMapper;
    private final PasswordEncoder passwordEncoder;
    private final CheckValidUserData checkValidUserData;

    /**
     * {@inheritDoc}
     */
    @Override
    public void editUser(HttpServletRequest request, UserEditModel userEditModel) {
        checkValidUserData.checkUser(userEditModel);
        User user = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        mergeMapper.map(userEditModel, user);
        user.setPassword(passwordEncoder.encode(userEditModel.getPassword()));
        userRepository.save(user);
    }

}
