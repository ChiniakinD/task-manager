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
import ru.chiniakin.entity.Comment;
import ru.chiniakin.entity.Task;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.Priority;
import ru.chiniakin.enums.Status;
import ru.chiniakin.interceptor.AuthInterceptor;
import ru.chiniakin.model.request.CommentTextModel;
import ru.chiniakin.repository.CommentRepository;
import ru.chiniakin.repository.TaskRepository;
import ru.chiniakin.repository.UserRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class CommentControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    CommentRepository commentRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @MockBean
    AuthInterceptor authInterceptor;

    @Mock
    HttpServletRequest request;

    String userLogin;

    User user;

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
        jdbcTemplate.update("truncate table task.tasks cascade");
        jdbcTemplate.update("truncate table auth.users cascade");
        jdbcTemplate.update("truncate table task.comments cascade");

        user = new User()
                .setLogin("userLogin")
                .setPassword("password")
                .setEmail("user@email.com");

        userRepository.save(user);
        userLogin = user.getLogin();

        MockitoAnnotations.openMocks(this);
        when(authInterceptor.preHandle(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(true);
        when(request.getAttribute(Mockito.anyString())).thenReturn(user);
    }

    @Test
    void addCommentTest() throws Exception {
        Task task = new Task()
                .setTitle("Исправить баг")
                .setDescription("Описание задачи")
                .setStatus(Status.IN_PROGRESS)
                .setPriority(Priority.HIGH)
                .setCreator(user)
                .setAssignee(user);

        taskRepository.save(task);

        CommentTextModel commentTextModel = new CommentTextModel();
        commentTextModel.setText("Задача выполнена отлично");

        String requestJson = new ObjectMapper().writeValueAsString(commentTextModel);
        userLogin = "username";

        mockMvc.perform(post("/comment/" + task.getId().toString())
                        .requestAttr("login", user)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getAllCommentsTest() throws Exception {
        Task task = new Task()
                .setTitle("Рефакторинг кода")
                .setDescription("Описание рефакторинга")
                .setStatus(Status.IN_PROGRESS)
                .setPriority(Priority.MEDIUM)
                .setCreator(user)
                .setAssignee(user);

        taskRepository.save(task);

        Comment comment1 = new Comment()
                .setUser(user)
                .setText("Комментарий 1")
                .setTask(task)
                .setActive(false);


        Comment comment2 = new Comment()
                .setUser(user)
                .setText("Комментарий 2")
                .setTask(task)
                .setActive(true);

        Comment comment3 = new Comment()
                .setUser(user)
                .setText("Комментарий 3")
                .setTask(task)
                .setActive(true);

        commentRepository.save(comment1);
        commentRepository.save(comment2);
        commentRepository.save(comment3);

        mockMvc.perform(get("/comment/" + task.getId().toString())
                        .requestAttr("login", user)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content.length()").value(2));
    }

    @Test
    void deleteCommentTest() throws Exception {
        Task task = new Task()
                .setTitle("Написать тесты")
                .setDescription("Описание тестов")
                .setStatus(Status.IN_PROGRESS)
                .setPriority(Priority.LOW)
                .setCreator(user)
                .setAssignee(user);

        taskRepository.save(task);

        Comment comment = new Comment()
                .setUser(user)
                .setText("Комментарий")
                .setTask(task)
                .setActive(false);

        commentRepository.save(comment);

        mockMvc.perform(delete("/comment/" + comment.getId().toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .requestAttr("login", user))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}
