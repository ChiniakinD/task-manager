package ru.chiniakin.model;

import ru.chiniakin.entity.Role;
import ru.chiniakin.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.UUID;

/**
 * Реализация интерфейса {@link UserDetails} для работы с пользовательскими данными в Spring Security.
 * Модель пользователя с необходимыми для авторизации и аутентификации данными.
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class SecurityUser implements UserDetails {

    private UUID id;
    private String userName;
    private String password;
    private String email;
    private Set<Role> roles;

    /**
     * Создает UserDetailsImpl на основе user.
     */
    public static SecurityUser build(User user) {
        return new SecurityUser(
                user.getId(),
                user.getLogin(),
                user.getPassword(),
                user.getEmail(),
                user.getRoles());
    }

    /**
     * @return Коллекция объектов, представляющих роли пользователя.
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return new ArrayList<>(
                roles.stream()
                        .map(role -> new SimpleGrantedAuthority(role.getRole().name()))
                        .toList()
        );
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return userName;
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
