package ru.chiniakin.repository;

import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.chiniakin.entity.Comment;
import ru.chiniakin.exception.NotFoundException;

import java.util.UUID;

/**
 * Репозиторий для работы с комментариями {@link Comment}.
 *
 * @author ChiniakinD
 */
@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    /**
     * Получает комментарий по его id, иначе выбрасывает ошибку.
     *
     * @param id id комментария.
     * @return комментарий.
     */
    default Comment findByIdOrThrow(UUID id) {

        return findById(id).orElseThrow(() -> new NotFoundException("Комментарий с id " + id + " не найдено"));
    }

    /**
     * Находит все комментарии по указанной задаче с постраничным выводом.
     *
     * @param pageable объект для постраничного вывода.
     * @return страница с комментариями.
     */
    Page<Comment> findAllByTaskIdAndIsActiveTrue(UUID id, Pageable pageable);

    /**
     * Меняет значение isActive у комментария по его id.
     *
     * @param id id комментария.
     */
    @Transactional
    @Modifying
    @Query("update Comment c set c.isActive = false where c.id = :id")
    void deleteCommentById(UUID id);

}
