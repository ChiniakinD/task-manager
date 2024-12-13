package ru.chiniakin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import ru.chiniakin.model.ApiError;
import ru.chiniakin.model.response.CommentResponse;
import ru.chiniakin.model.request.CommentTextModel;
import ru.chiniakin.service.interfaces.CommentService;

import java.util.UUID;

/**
 * Контроллер для работы с комментариями.
 *
 * @author ChiniakinD
 */
@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@Tag(name = "AdminController", description = "Контроллер админа для управление задачами")
public class CommentController {

    private final CommentService commentService;

    /**
     * Оставляет комментарий к задаче.
     *
     * @param id          - id задачи.
     * @param request     - объект запроса.
     * @param commentText - текст комментария
     */
    @Operation(
            summary = "Оставить новый комментарий к задаче",
            tags = {"CommentController"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Комментарий создано"),
                    @ApiResponse(responseCode = "401", description = "Ошибка авторизации", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Ресурс не найден", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    })
            },
            security = {
                    @SecurityRequirement(name = "jwt")
            }
    )
    @PostMapping("/{taskId}")
    public void addComment(@PathVariable(name = "taskId") UUID id, HttpServletRequest request, @RequestBody CommentTextModel commentText) {
        commentService.addComment(id, request, commentText);
    }

    /**
     * Получает комментарии к задаче.
     *
     * @param id - id задачи.
     * @return список комментариев к задаче.
     */
    @Operation(
            summary = "Получить комментарии к задаче",
            tags = {"CommentController"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Комментарии к задаче получены"),
                    @ApiResponse(responseCode = "401", description = "Ошибка авторизации", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "403", description = "Нет доступа", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Ресурс не найден", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    })
            },
            security = {
                    @SecurityRequirement(name = "jwt")
            }
    )
    @GetMapping("/{taskId}")
    public Page<CommentResponse> getComment(@PathVariable(name = "taskId") UUID id,
                                            HttpServletRequest request,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "5") int size) {
        return commentService.getComments(id, request, page, size);
    }

    /**
     * Удалить комментарий к задаче.
     *
     * @param id      - id комментария
     * @param request - объект запроса.
     */
    @Operation(
            summary = "Удалить комментарий к задаче",
            tags = {"CommentController"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Комментарий удален"),
                    @ApiResponse(responseCode = "401", description = "Ошибка авторизации", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "403", description = "Нет доступа", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "404", description = "Ресурс не найден", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    }),
                    @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
                    })
            },
            security = {
                    @SecurityRequirement(name = "jwt")
            }
    )
    @DeleteMapping("/{commentId}")
    public void deleteComment(@PathVariable(name = "commentId") UUID id, HttpServletRequest request) {
        commentService.deleteCommentById(id, request);
    }

}
