package ru.chiniakin.service;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ru.chiniakin.entity.Role;
import ru.chiniakin.entity.Task;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.Priority;
import ru.chiniakin.enums.RoleEnum;
import ru.chiniakin.enums.Status;
import ru.chiniakin.exception.AccessDeniedException;
import ru.chiniakin.exception.NotFoundException;
import ru.chiniakin.model.request.CreateTaskRequest;
import ru.chiniakin.model.response.TaskResponse;
import ru.chiniakin.model.request.UpdateTaskRequest;
import ru.chiniakin.repository.TaskRepository;
import ru.chiniakin.repository.UserRepository;
import ru.chiniakin.service.interfaces.AdminTaskService;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class AdminServiceTest {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    AdminTaskService adminTaskService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Mock
    HttpServletRequest request;

    UUID taskId;

    String adminLogin;

    User user;

    User admin;

    @Container
    static PostgreSQLContainer<?> postgreSQLContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("postgres")
            .withUsername("postgres")
            .withPassword("postgres");

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgreSQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgreSQLContainer::getUsername);
        registry.add("spring.datasource.password", postgreSQLContainer::getPassword);
    }

    @BeforeAll
    public static void setup() {
        System.setProperty("DB_URL", postgreSQLContainer.getJdbcUrl());
        System.setProperty("DB_USERNAME", postgreSQLContainer.getUsername());
        System.setProperty("DB_PASSWORD", postgreSQLContainer.getPassword());
    }

    @BeforeEach
    void setUp() {
        jdbcTemplate.update("truncate table task.tasks cascade ");
        jdbcTemplate.update("truncate table auth.users cascade ");
        jdbcTemplate.update("truncate table auth.roles cascade ");
        user = new User()
                .setLogin("userLogin")
                .setPassword("password")
                .setEmail("user@email.com");
        userRepository.save(user);
        adminLogin = "admin" + UUID.randomUUID();
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setDescription("Админ");
        role.setRole(RoleEnum.ADMIN);
        admin = new User()
                .setLogin("adminLogin")
                .setPassword("password")
                .setEmail("admin@email.com")
                .setRoles(Set.of(role));
        userRepository.save(admin);

        Task task = new Task()
                .setCreator(admin)
                .setTitle("Задание 1")
                .setCreatedAt(OffsetDateTime.now())
                .setAssignee(user)
                .setStatus(Status.PENDING)
                .setPriority(Priority.LOW);

        taskRepository.save(task);
        taskId = task.getId();
    }

    @Test
    void createTaskShouldCreateTaskSuccessfully() {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest()
                .setTitle("Задание 1")
                .setAssigneeId(user.getId())
                .setStatus(Status.PENDING)
                .setPriority(Priority.LOW);

        when(request.getAttribute(Mockito.anyString())).thenReturn(admin);

        adminTaskService.createTask(request, createTaskRequest);

        Task task = taskRepository.findAll().stream()
                .filter(x -> x.getTitle().equals("Задание 1"))
                .findFirst().get();

        assertEquals("Задание 1", task.getTitle());
        assertEquals(Status.PENDING, task.getStatus());
        assertEquals(Priority.LOW, task.getPriority());
    }


    @Test
    void getTaskByIdShouldReturnTaskSuccessfully() {
        when(request.getAttribute(Mockito.anyString())).thenReturn(admin);
        Task task = taskRepository.findAll().stream()
                .filter(x -> x.getTitle().equals("Задание 1"))
                .findFirst()
                .get();

        TaskResponse taskResponse = adminTaskService.getTaskById(request, task.getId());

        assertEquals(task.getId().toString(), taskResponse.getId());
    }

    @Test
    void updateTaskShouldUpdateTaskSuccessfully() {
        String newTaskName = "Обновленное задание";

        when(request.getAttribute(Mockito.anyString())).thenReturn(admin);

        UpdateTaskRequest taskRequest = new UpdateTaskRequest();
        taskRequest.setTitle(newTaskName);

        adminTaskService.updateTask(request, taskId, taskRequest);

        Task updatedTask = taskRepository.findById(taskId).orElseThrow();
        assertEquals(newTaskName, updatedTask.getTitle());
    }

    @Test
    void updateTaskShouldThrowNotFoundExceptionWhenTaskNotFound() {
        UUID invalidTaskId = UUID.randomUUID();
        assertThrows(NotFoundException.class, () ->
                adminTaskService.updateTask(request, invalidTaskId, new UpdateTaskRequest()));
    }

    @Test
    void deleteTaskShouldDeleteTaskSuccessfully() {
        when(request.getAttribute(Mockito.anyString())).thenReturn(admin);

        adminTaskService.deleteTask(request, taskId);

        assertThrows(NotFoundException.class, () -> taskRepository.findActiveByIdOrThrow(taskId));
    }

    @Test
    void deleteTaskShouldThrowNotFoundExceptionWhenTaskNotFound() {
        UUID invalidTaskId = UUID.randomUUID();
        assertThrows(NotFoundException.class, () ->
                adminTaskService.deleteTask(request, invalidTaskId));
    }

    @Test
    void createTaskShouldThrowAccessDeniedWhenUserIsNotAdmin() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setLogin("тест");
        when(request.getAttribute(Mockito.anyString())).thenReturn(user);

        CreateTaskRequest createTaskRequest = new CreateTaskRequest();
        createTaskRequest.setTitle("Задание от обычного пользователя");

        assertThrows(AccessDeniedException.class, () -> adminTaskService.createTask(request, createTaskRequest));
    }

}
