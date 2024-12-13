package ru.chiniakin.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Комментарий.
 *
 * @author ChiniakinD
 */
@Getter
@Setter
@Entity
@Table(name = "comments", schema = "task")
@RequiredArgsConstructor
@Accessors(chain = true)
public class Comment {

    @Id
    @GeneratedValue(generator = "UUID")
    private UUID id;

    @ManyToOne
    private Task task;

    @ManyToOne
    private User user;

    private String text;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

}
