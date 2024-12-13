package ru.chiniakin.repository;

import ru.chiniakin.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.chiniakin.exception.NotFoundException;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий для работы с пользователями.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByLogin(String username);

    /**
     * Получает пользователя по логину.
     *
     * @param login логин пользователя.
     */
    Optional<User> findUserByLogin(String login);

    /**
     * Выполняет проверку существования пользователя с данным логином.
     *
     * @param login логин пользователя.
     * @return true, если пользователь существует, иначе false.
     */
    boolean existsByLogin(String login);

    /**
     * Выполняет проверку существования пользователя с данным email.
     *
     * @param email email пользователя.
     * @return true, если пользователь существует, иначе false.
     */
    boolean existsByEmail(String email);

    /**
     * Получает пользователя по логину или выбрасывает исключение.
     *
     * @param login логин пользователя.
     */
    default User findUserByLoginOrThrow(String login) {
        return findUserByLogin(login).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

    /**
     * Получает пользователя по id или выбрасывает исключение.
     *
     * @param id id пользователя.
     */
    default User findUserByIdOrThrow(UUID id) {
        return findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }

}
