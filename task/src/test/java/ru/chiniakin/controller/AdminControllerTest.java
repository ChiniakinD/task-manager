package ru.chiniakin.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.chiniakin.entity.Role;
import ru.chiniakin.entity.Task;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.Priority;
import ru.chiniakin.enums.RoleEnum;
import ru.chiniakin.enums.Status;
import ru.chiniakin.interceptor.AuthInterceptor;
import ru.chiniakin.model.request.CreateTaskRequest;
import ru.chiniakin.model.request.TasksFilterRequest;
import ru.chiniakin.model.request.UpdateTaskRequest;
import ru.chiniakin.repository.TaskRepository;
import ru.chiniakin.repository.UserRepository;

import java.util.Set;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class AdminControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    UserRepository userRepository;

    @MockBean
    AuthInterceptor authInterceptor;

    @Mock
    HttpServletRequest request;

    String adminLogin;

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
    void setUp() throws Exception {
        jdbcTemplate.update("truncate table task.tasks cascade ");
        jdbcTemplate.update("truncate table auth.users cascade ");
        jdbcTemplate.update("truncate table auth.roles cascade ");
        Role role = new Role();
        role.setId(UUID.randomUUID());
        role.setDescription("Админ");
        role.setRole(RoleEnum.ADMIN);
        admin = new User()
                .setLogin("userLogin")
                .setPassword("password")
                .setEmail("user@email.com")
                .setRoles(Set.of(role));

        userRepository.saveAndFlush(admin);
        adminLogin = admin.getLogin();
        MockitoAnnotations.openMocks(this);
        when(authInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        when(request.getAttribute(Mockito.anyString())).thenReturn("admin");
    }

    @Test
    void getTasksWithoutFilterShouldReturnAllTasks() throws Exception {
        Task task1 = new Task()
                .setTitle("Задача 1")
                .setDescription("Описание задачи 1")
                .setStatus(Status.IN_PROGRESS)
                .setAssignee(admin);
        taskRepository.save(task1);
        Task task2 = new Task()
                .setTitle("Задача 2")
                .setDescription("Описание задачи 2")
                .setStatus(Status.IN_PROGRESS)
                .setAssignee(admin);
        taskRepository.save(task2);
        Task task3 = new Task()
                .setTitle("Задача 3")
                .setDescription("Описание задачи 3")
                .setStatus(Status.IN_PROGRESS)
                .setAssignee(admin);
        taskRepository.save(task3);

        mockMvc.perform(get("/task")
                        .param("page", "1")
                        .param("size", "5")
                        .requestAttr("login", admin))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(3));
    }

    @Test
    void getTasksWithFilterCorrectTasksTest() throws Exception {
        Task task1 = new Task()
                .setTitle("Задача 1")
                .setDescription("Описание задачи 1")
                .setStatus(Status.IN_PROGRESS)
                .setAssignee(admin)
                .setCreator(admin)
                .setPriority(Priority.LOW);
        taskRepository.save(task1);
        Task task2 = new Task()
                .setTitle("Задача 2")
                .setDescription("Описание задачи 2")
                .setStatus(Status.IN_PROGRESS)
                .setAssignee(admin)
                .setCreator(admin)
                .setPriority(Priority.HIGH);
        taskRepository.save(task2);
        Task task3 = new Task()
                .setTitle("Задача 3")
                .setDescription("Описание задачи 3")
                .setStatus(Status.IN_PROGRESS)
                .setAssignee(admin)
                .setCreator(admin)
                .setPriority(Priority.HIGH);
        taskRepository.save(task3);

        TasksFilterRequest filterRequest = new TasksFilterRequest(admin.getId(), admin.getId(), Status.IN_PROGRESS.getValue(), Priority.HIGH.getValue(), 2, null);
        mockMvc.perform(get("/task")
                        .queryParam("authorId", filterRequest.authorId().toString())
                        .queryParam("assigneeId", filterRequest.assigneeId().toString())
                        .queryParam("status", filterRequest.status())
                        .queryParam("priority", filterRequest.priority())
                        .queryParam("page", filterRequest.page().toString())
                        .queryParam("size", filterRequest.size().toString())
                        .requestAttr("login", admin))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2));
        TasksFilterRequest filterRequest1 = new TasksFilterRequest(admin.getId(), admin.getId(), Status.IN_PROGRESS.getValue(), Priority.LOW.getValue(), 2, null);
        mockMvc.perform(get("/task")
                        .queryParam("authorId", filterRequest1.authorId().toString())
                        .queryParam("assigneeId", filterRequest1.assigneeId().toString())
                        .queryParam("status", filterRequest1.status())
                        .queryParam("priority", filterRequest1.priority())
                        .queryParam("page", filterRequest1.page().toString())
                        .queryParam("size", filterRequest1.size().toString())
                        .requestAttr("login", admin))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(1));
    }

    @Test
    void createTaskTest() throws Exception {
        CreateTaskRequest createTaskRequest = new CreateTaskRequest()
                .setTitle("Новая задача")
                .setDescription("Описание новой задачи")
                .setStatus(Status.IN_PROGRESS);
        String requestJson = new ObjectMapper().writeValueAsString(createTaskRequest);

        mockMvc.perform(post("/task")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson)
                        .requestAttr("login", admin))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void deleteTaskTest() throws Exception {
        Task task = new Task()
                .setTitle("Задача для удаления")
                .setDescription("Описание задачи для удаления")
                .setStatus(Status.IN_PROGRESS)
                .setAssignee(admin)
                .setIsActive(Boolean.TRUE);
        taskRepository.save(task);
        assertTrue(task.getIsActive());


        mockMvc.perform(delete("/task/" + task.getId())
                        .requestAttr("login", admin)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
        Task byIdOrThrow = taskRepository.findById(task.getId()).orElse(null);
        assertFalse(byIdOrThrow.getIsActive());
    }

    @Test
    void editTaskTest() throws Exception {
        Task task = new Task()
                .setTitle("Задача для редактирования")
                .setDescription("Описание задачи для редактирования")
                .setStatus(Status.IN_PROGRESS)
                .setAssignee(admin);
        taskRepository.save(task);

        UpdateTaskRequest updateTaskRequest = new UpdateTaskRequest()
                .setTitle("Отредактированная задача")
                .setDescription("Описание отредактированной задачи");
        String requestJson = new ObjectMapper().writeValueAsString(updateTaskRequest);

        mockMvc.perform(patch("/task/" + task.getId())
                        .requestAttr("login", admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getTaskByIdTest() throws Exception {
        Task task = new Task()
                .setTitle("Задача для получения")
                .setDescription("Описание задачи для получения")
                .setStatus(Status.IN_PROGRESS)
                .setAssignee(admin);
        taskRepository.save(task);

        mockMvc.perform(get("/task/" + task.getId())
                        .requestAttr("login", admin)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.title").value(task.getTitle()));
    }

}
