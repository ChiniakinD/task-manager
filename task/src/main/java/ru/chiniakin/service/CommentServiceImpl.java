package ru.chiniakin.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.chiniakin.entity.Comment;
import ru.chiniakin.entity.Task;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.AttributeName;
import ru.chiniakin.enums.RoleEnum;
import ru.chiniakin.exception.AccessDeniedException;
import ru.chiniakin.model.response.CommentResponse;
import ru.chiniakin.model.request.CommentTextModel;
import ru.chiniakin.repository.CommentRepository;
import ru.chiniakin.repository.TaskRepository;
import ru.chiniakin.repository.UserRepository;
import ru.chiniakin.service.interfaces.CommentService;
import ru.chiniakin.util.RequestAttributeUtil;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Реализация сервиса {@link CommentService} для работы с комментариями.
 *
 * @author ChiniakinD
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;
    private final ModelMapper modelMapper;

    /**
     * {@inheritDoc}
     */
    @Override
    public void addComment(UUID id, HttpServletRequest request, CommentTextModel commentText) {
        User user = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        if (!isUserAdmin(user) && !isAssignee(id, user)) {
            throw new AccessDeniedException("Отсутствуют права для выполнения операции.");
        }
        Task task = taskRepository.findActiveByIdOrThrow(id);
        Comment comment = new Comment()
                .setTask(task)
                .setUser(user)
                .setText(commentText.getText())
                .setCreatedAt(OffsetDateTime.now())
                .setActive(true);
        commentRepository.save(comment);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<CommentResponse> getComments(UUID id, HttpServletRequest request, int page, int size) {
        User user = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        if (!isUserAdmin(user) && !isAssignee(id, user)) {
            throw new AccessDeniedException("Отсутствуют права для выполнения операции.");
        }
        Pageable pageable = PageRequest.of(page, size);
        taskRepository.findActiveByIdOrThrow(id);
        Page<Comment> comments = commentRepository.findAllByTaskIdAndIsActiveTrue(id,  pageable);
        List<CommentResponse> commentResponse = comments.stream()
                .map(comment -> modelMapper.map(comment, CommentResponse.class))
                .toList();
        return new PageImpl<>(commentResponse, pageable, comments.getTotalElements());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteCommentById(UUID id, HttpServletRequest request) {
        User currentUser = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        UUID commentUserId = commentRepository.findByIdOrThrow(id).getUser().getId();
        if (currentUser.getId().equals(commentUserId) || isUserAdmin(currentUser)) {
            commentRepository.deleteCommentById(id);
            return;
        }
        throw new AccessDeniedException("Отсутствуют права для выполнения операции.");
    }

    /**
     * Определяет, является ли пользователь админом по его логину.
     *
     * @return true, если является.
     */
    private boolean isUserAdmin(User user) {
        return user.getRoles().stream()
                .anyMatch(role -> role.getRole() == RoleEnum.ADMIN);
    }

    private boolean isAssignee(UUID taskId, User user) {
        Task task  =  taskRepository.findActiveByIdOrThrow(taskId);
        return task.getAssignee().getId().equals(user.getId());
    }

}
