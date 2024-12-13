package ru.chiniakin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.chiniakin.model.ApiError;
import ru.chiniakin.model.response.TaskResponse;
import ru.chiniakin.model.request.UpdateStatusModel;
import ru.chiniakin.model.request.UpdateTaskRequest;
import ru.chiniakin.model.request.UserTaskFilterRequest;
import ru.chiniakin.service.interfaces.UserTaskService;
import java.util.UUID;

/**
 * Контроллер исполнителя для управление задачами.
 *
 * @author ChiniakinD
 */
@Validated
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "UserController", description = "Контроллер исполнителя для управление задачами")
public class UserController {

    private final UserTaskService userTaskService;

    /**
     * Получает задачи согласно фильтрам.
     *
     * @param userTaskFilterRequest {@link UserTaskFilterRequest} объект, содержащий параметры для фильтра.
     * @return страница с задачами, согласно фильтрам.
     */
    @Operation(
            summary = "Получить все задачи согласно фильтрам",
            tags = {"UserController"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список задач", content = {
                            @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = TaskResponse.class)))
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
    @GetMapping
    public Page<TaskResponse> tasksGet(HttpServletRequest request, @Valid @ParameterObject UserTaskFilterRequest userTaskFilterRequest) {
        return userTaskService.getTasks(request, userTaskFilterRequest);
    }

    /**
     * Получает задачу по ее id.
     *
     * @param id      id задачи.
     * @param request тело запроса.
     * @return задача.
     */
    @Operation(
            summary = "Получить задачу по ID",
            tags = {"UserController"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Информация о задаче", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = TaskResponse.class))
                    }),
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
    @GetMapping("/{id}")
    public TaskResponse getTaskById(
            HttpServletRequest request,
            @Parameter(name = "id", description = "id задачи", required = true, in = ParameterIn.PATH) @PathVariable("id") UUID id) {
        return userTaskService.getTaskById(request, id);
    }

    /**
     * Редактирует статус задачи.
     *
     * @param id                id задачи.
     * @param request           тело запроса.
     * @param updateStatusModel {@link UpdateStatusModel} модель для редактирования задачи.
     */
    @Operation(
            summary = "Редактировать статус задачи",
            tags = {"AdminController"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача обновлена", content = {
                            @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateTaskRequest.class))
                    }),
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
    @PatchMapping("/status/{id}")
    public void editTaskStatus(
            HttpServletRequest request,
            @Parameter(name = "id", description = "id задачи для редактирования", required = true, in = ParameterIn.PATH) @PathVariable("id") UUID id,
            @Parameter(name = "UpdateStatusModel", description = "модель для редактирования", required = true)
            @Valid @RequestBody UpdateStatusModel updateStatusModel) {
        userTaskService.updateTaskStatus(request, id, updateStatusModel);
    }

}
