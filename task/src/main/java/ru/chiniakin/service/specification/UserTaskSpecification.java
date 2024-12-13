package ru.chiniakin.service.specification;

import jakarta.validation.Valid;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import ru.chiniakin.entity.Task;
import ru.chiniakin.enums.Priority;
import ru.chiniakin.enums.Status;
import ru.chiniakin.model.request.UserTaskFilterRequest;

import java.util.UUID;

/**
 * Класс для создания спецификация для задач на основе фильтра.
 *
 * @author ChiniakinD
 */
@NoArgsConstructor
public class UserTaskSpecification {

    /**
     * Создает спецификацию для фильтрации задач на основе переданного фильтра.
     *
     * @param userTaskFilterRequest фильтр.
     * @return спецификация для задач.
     */
    public static Specification<Task> build(UserTaskFilterRequest userTaskFilterRequest, UUID userId) {
        return Specification.where(byAssigneeId(userId))
                .and(byStatus(userTaskFilterRequest.status()))
                .and(byPriority(userTaskFilterRequest.priority()))
                .and(byIsActive());
    }

    private static Specification<Task> byIsActive() {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isActive"), true);
    }

    private static Specification<Task> byPriority(@Valid String priority) {
        return (root, query, criteriaBuilder) -> {
            if (priority == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("priority"), Priority.fromValue(priority));

        };
    }

    private static Specification<Task> byStatus(@Valid String status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), Status.fromValue(status));
        };
    }

    private static Specification<Task> byAssigneeId(@Valid UUID assigneeId) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("assignee").get("id"), assigneeId);
    }

}




