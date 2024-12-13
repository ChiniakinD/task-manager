package ru.chiniakin.repository;

import ru.chiniakin.entity.Role;
import ru.chiniakin.enums.RoleEnum;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Репозиторий для работы с Ролями.
 */
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Возвращает роль по RoleEnum.
     */
    Role findByRole(RoleEnum roleEnum);

}
