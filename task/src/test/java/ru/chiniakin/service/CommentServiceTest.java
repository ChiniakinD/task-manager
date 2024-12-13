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
import ru.chiniakin.entity.Comment;
import ru.chiniakin.entity.Task;
import ru.chiniakin.entity.User;
import ru.chiniakin.enums.Priority;
import ru.chiniakin.enums.Status;
import ru.chiniakin.exception.AccessDeniedException;
import ru.chiniakin.exception.NotFoundException;
import ru.chiniakin.model.request.CommentTextModel;
import ru.chiniakin.repository.CommentRepository;
import ru.chiniakin.repository.TaskRepository;
import ru.chiniakin.repository.UserRepository;
import ru.chiniakin.service.interfaces.CommentService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@SpringBootTest
@Testcontainers
class CommentServiceTest {

    @Autowired
    UserRepository userRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    CommentService commentService;

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    CommentRepository commentRepository;

    @Mock
    HttpServletRequest request;

    UUID taskId;

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
    void setUp() {
        jdbcTemplate.update("truncate table task.tasks cascade");
        jdbcTemplate.update("truncate table auth.users cascade");
        jdbcTemplate.update("truncate table task.comments cascade");

        String email = "user" + UUID.randomUUID() + "@email.com";
        userLogin = "user" + UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String password = "secure_password";
        user = new User()
                .setId(userId)
                .setLogin(userLogin)
                .setEmail(email)
                .setPassword(password);
        jdbcTemplate.update(
                "INSERT INTO auth.users (id, login, password, email) VALUES (?, ?, ?, ?)",
                userId, userLogin, password, email
        );

        taskId = UUID.randomUUID();
        Task task = new Task()
                .setId(taskId)
                .setTitle("Задача 1")
                .setDescription("Описание задачи 1")
                .setStatus(Status.IN_PROGRESS)
                .setPriority(Priority.HIGH)
                .setCreator(user);
        taskRepository.save(task);
    }

    @Test
    void addCommentTest() {
        Task task = new Task()
                .setTitle("Задача 2")
                .setDescription("Описание задачи 2")
                .setStatus(Status.IN_PROGRESS)
                .setPriority(Priority.HIGH)
                .setCreator(user)
                .setAssignee(user);
        taskRepository.save(task);

        when(request.getAttribute(Mockito.anyString())).thenReturn(user);
        CommentTextModel commentTextModel = new CommentTextModel();
        commentTextModel.setText("Отличная задача");

        commentService.addComment(task.getId(), request, commentTextModel);

        Comment comment = commentRepository.findAll()
                .stream()
                .filter(x -> x.getText().equals("Отличная задача"))
                .findFirst()
                .get();
        assertNotNull(comment);
        assertNotNull(comment.getText());
    }

    @Test
    void addCommentByNotExistUserNotFoundExceptionTest() {
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setLogin("testLogin");
        when(request.getAttribute(Mockito.anyString())).thenReturn(newUser);
        assertThrows(NotFoundException.class,
                () -> commentService.addComment(UUID.randomUUID(), request, new CommentTextModel()));
    }

    @Test
    void getCommentByNotExistTaskNotFoundExceptionTest() {
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setLogin("testLogin");
        when(request.getAttribute(Mockito.anyString())).thenReturn(newUser);
        assertThrows(NotFoundException.class,
                () -> commentService.getComments(UUID.randomUUID(), request, 0, 5));
    }

    @Test
    void deleteNotExistCommentNotFoundExceptionTest() {
        User newUser = new User();
        newUser.setId(UUID.randomUUID());
        newUser.setLogin("testLogin");
        when(request.getAttribute(Mockito.anyString())).thenReturn(newUser);
        assertThrows(NotFoundException.class,
                () -> commentService.deleteCommentById(UUID.randomUUID(), request));
    }

    @Test
    void deleteCommentWithoutAccessShouldThrowAccessDeniedException() {
        User currentUser = new User()
                .setLogin("test")
                .setPassword("test")
                .setEmail("test@email.com");
        userRepository.save(currentUser);
        currentUser.setId(UUID.randomUUID());
        when(request.getAttribute(Mockito.anyString())).thenReturn(currentUser);

        Task task = taskRepository.findAll()
                .stream()
                .filter(x -> x.getTitle().equals("Задача 1"))
                .findFirst()
                .get();
        Comment comment = new Comment()
                .setActive(true)
                .setTask(task)
                .setUser(user)
                .setText("Комментарий текст");
        commentRepository.save(comment);

        List<Comment> commentList = commentRepository.findAll();
        Comment comment1 = commentList.stream()
                .filter(x -> x.getText().equals(comment.getText()))
                .findFirst()
                .get();

        assertThrows(AccessDeniedException.class, () -> commentService.deleteCommentById(comment1.getId(), request));
    }

}
