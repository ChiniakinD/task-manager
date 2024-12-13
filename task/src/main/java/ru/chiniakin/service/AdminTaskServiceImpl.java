package ru.chiniakin.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.chiniakin.entity.Task;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.AttributeName;
import ru.chiniakin.enums.RoleEnum;
import ru.chiniakin.exception.AccessDeniedException;
import ru.chiniakin.exception.NotFoundException;
import ru.chiniakin.model.response.TaskResponse;
import ru.chiniakin.model.request.UpdateAssigneeId;
import ru.chiniakin.model.request.UpdatePriorityModel;
import ru.chiniakin.model.request.UpdateStatusModel;
import ru.chiniakin.model.request.CreateTaskRequest;
import ru.chiniakin.model.request.TasksFilterRequest;
import ru.chiniakin.model.request.UpdateTaskRequest;
import ru.chiniakin.repository.TaskRepository;
import ru.chiniakin.repository.UserRepository;
import ru.chiniakin.service.interfaces.AdminTaskService;
import ru.chiniakin.service.specification.AdminTaskSpecification;
import ru.chiniakin.util.RequestAttributeUtil;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * Реализация сервиса админа {@link AdminTaskService} для работы с задачами.
 *
 * @author ChiniakinD
 */
@Service
@RequiredArgsConstructor
public class AdminTaskServiceImpl implements AdminTaskService {

    private final ModelMapper modelMapper;
    private final ModelMapper mergeMapper;
    private final UserRepository userRepository;
    private final TaskRepository taskRepository;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createTask(HttpServletRequest request, CreateTaskRequest taskRequest) {
        User taskCreator = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        checkAdmin(taskCreator);

        Task task = modelMapper.map(taskRequest, Task.class);
        task.setCreator(taskCreator);
        if (taskRequest.getAssigneeId() != null) {
            task.setAssignee(
                    userRepository.findById(taskRequest.getAssigneeId())
                            .orElseThrow(() -> new NotFoundException("Невозможно создать задачу для данного пользователя "
                                    + taskRequest.getAssigneeId() + ". Пользователь не найден")
                            ));
        }
        taskRepository.save(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Page<TaskResponse> getTasks(HttpServletRequest request, TasksFilterRequest tasksFilterRequest) {
        checkAdmin(RequestAttributeUtil.get(request, AttributeName.LOGIN));
        Pageable pageable = PageRequest.of(tasksFilterRequest.page(), tasksFilterRequest.size());
        Page<Task> tasks = taskRepository.findAll(AdminTaskSpecification.build(tasksFilterRequest), pageable);
        return tasks.map(task -> modelMapper.map(task, TaskResponse.class));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskResponse getTaskById(HttpServletRequest request, UUID id) {
        taskRepository.findActiveByIdOrThrow(id);
        User taskCreator = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        checkAdmin(taskCreator);
        Task task = taskRepository.findActiveByIdOrThrowEg(id);
        return modelMapper.map(task, TaskResponse.class);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTask(HttpServletRequest request, UUID id, UpdateTaskRequest taskRequest) {
        Task task = taskRepository.findActiveByIdOrThrow(id);
        task.setUpdatedAt(OffsetDateTime.now());
        User currentUser = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        checkAdmin(currentUser);
        mergeMapper.map(taskRequest, task);
        taskRepository.save(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTaskPriority(HttpServletRequest request, UUID id, UpdatePriorityModel updatePriorityModel) {
        Task task = taskRepository.findActiveByIdOrThrow(id);
        task.setUpdatedAt(OffsetDateTime.now());
        User currentUser = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        checkAdmin(currentUser);
        mergeMapper.map(updatePriorityModel, task);
        taskRepository.save(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTaskStatus(HttpServletRequest request, UUID id, UpdateStatusModel updateStatusModel) {
        Task task = taskRepository.findActiveByIdOrThrow(id);
        task.setUpdatedAt(OffsetDateTime.now());
        User currentUser = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        checkAdmin(currentUser);
        mergeMapper.map(updateStatusModel, task);
        taskRepository.save(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void updateTaskAssigneeId(HttpServletRequest request, UUID id, UpdateAssigneeId updateAssigneeId) {
        userRepository.findUserByIdOrThrow(UUID.fromString(updateAssigneeId.getAssignee()));
        Task task = taskRepository.findActiveByIdOrThrow(id);
        task.setUpdatedAt(OffsetDateTime.now());
        User currentUser = RequestAttributeUtil.get(request, AttributeName.LOGIN);
        checkAdmin(currentUser);
        mergeMapper.map(updateAssigneeId, task);
        taskRepository.save(task);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteTask(HttpServletRequest request, UUID id) {
        taskRepository.findActiveByIdOrThrow(id);
        taskRepository.markTaskAsDeletedById(id);
    }

    private void checkAdmin(User user) {
        user.getRoles().stream()
                .filter(x -> x.getRole().equals(RoleEnum.ADMIN))
                .findFirst()
                .orElseThrow(AccessDeniedException::new);
    }

}
