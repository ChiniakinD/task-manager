package ru.chiniakin.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.chiniakin.entity.Task;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.AttributeName;
import ru.chiniakin.exception.AccessDeniedException;
import ru.chiniakin.model.response.TaskResponse;
import ru.chiniakin.model.request.UpdateStatusModel;
import ru.chiniakin.model.request.UserTaskFilterRequest;
import ru.chiniakin.repository.TaskRepository;
import ru.chiniakin.service.interfaces.AdminTaskService;
import ru.chiniakin.service.interfaces.UserTaskService;
import ru.chiniakin.service.specification.UserTaskSpecification;
import ru.chiniakin.util.RequestAttributeUtil;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Реализация сервиса админа {@link UserTaskService} для работы с задачами.
 *
 * @author ChiniakinD
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserTaskServiceImpl implements UserTaskService {

    private final ModelMapper modelMapper;
    private final ModelMapper mergeMapper;
    private final TaskRepository taskRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TaskResponse> getTasks(HttpServletRequest request, UserTaskFilterRequest userTaskFilterRequest) {
        User currentUser = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        Pageable pageable = PageRequest.of(userTaskFilterRequest.page(), userTaskFilterRequest.size());
        Page<Task> tasks = taskRepository.findAll(UserTaskSpecification.
                build(userTaskFilterRequest, currentUser.getId()), pageable);
        return tasks.map(task -> modelMapper.map(task, TaskResponse.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskResponse getTaskById(HttpServletRequest request, UUID id) {
        User currentUser = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        Task task = taskRepository.findActiveByIdOrThrow(id);
        isAssignee(task, currentUser);
        return modelMapper.map(task, TaskResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTaskStatus(HttpServletRequest request, UUID id, UpdateStatusModel updateStatusModel) {
        Task task = taskRepository.findActiveByIdOrThrow(id);
        User currentUser = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        isAssignee(task, currentUser);
        task.setUpdatedAt(OffsetDateTime.now());
        mergeMapper.map(updateStatusModel, task);
        taskRepository.save(task);
    }

    private void isAssignee(Task task, User user) {
        if (!task.getAssignee().getId().equals(user.getId())) {
            throw new AccessDeniedException();
        }
    }

}
