package ru.chiniakin.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.chiniakin.entity.Task;
import ru.chiniakin.exception.NotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface TaskRepository extends JpaRepository<Task, UUID> {

    /**
     * Находит все задачи по указанным спецификациям с постраничным выводом.
     *
     * @param spec     спецификация для фильтрации задач.
     * @param pageable объект для постраничного вывода.
     * @return страница с задачами.
     */
    Page<Task> findAll(Specification<Task> spec, Pageable pageable);

    @Query("select t from Task t where t.id = :id and t.isActive = true")
    Optional<Task> findActiveById(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"comments", "creator", "assignee"})
    @Query("select t from Task t where t.id = :id and t.isActive = true")
    Optional<Task> findActiveByIdEg(@Param("id") UUID id);

    default Task findActiveByIdOrThrow(UUID id) {
        return findActiveById(id)
                .orElseThrow(() -> new NotFoundException("Задача с id " + id + " не найдена"));
    }

    default Task findActiveByIdOrThrowEg(UUID id) {
        return findActiveByIdEg(id)
                .orElseThrow(() -> new NotFoundException("Задача с id " + id + " не найдена"));
    }

    /**
     * Меняет статус на false у задачи по указанному id.'
     *
     * @param id id задачи.
     */
    @Transactional
    @Modifying
    @Query("update Task t set t.isActive = false where t.id = :id")
    void markTaskAsDeletedById(UUID id);

}
