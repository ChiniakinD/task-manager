package ru.chiniakin.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.UUID;

/**
 * Связь между пользователем и ролью.
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@Accessors(chain = true)
@Entity
@Table(name = "user_roles", schema = "auth")
public class UserRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id = UUID.randomUUID();

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

}
