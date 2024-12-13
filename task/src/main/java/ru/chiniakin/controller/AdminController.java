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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import ru.chiniakin.model.ApiError;
import ru.chiniakin.model.request.UpdateAssigneeId;
import ru.chiniakin.model.request.CreateTaskRequest;
import ru.chiniakin.model.response.TaskResponse;
import ru.chiniakin.model.request.TasksFilterRequest;
import ru.chiniakin.model.request.UpdatePriorityModel;
import ru.chiniakin.model.request.UpdateStatusModel;
import ru.chiniakin.model.request.UpdateTaskRequest;
import ru.chiniakin.service.interfaces.AdminTaskService;

import java.util.UUID;

/**
 * Контроллер админа для управление задачами.
 *
 * @author ChiniakinD
 */
@Validated
@RestController
@RequestMapping("/task")
@RequiredArgsConstructor
@Tag(name = "AdminController", description = "Контроллер админа для управление задачами")
public class AdminController {

    private final AdminTaskService adminTaskService;

    /**
     * Получает задачи согласно фильтрам.
     *
     * @param tasksFilterRequest {@link TasksFilterRequest} объект, содержащий параметры для фильтра.
     * @return страница с задачами, согласно фильтрам.
     */
    @Operation(
            summary = "Получить все задачи согласно фильтрам",
            tags = {"AdminController"},
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
    public Page<TaskResponse> tasksGet(HttpServletRequest request, @Valid @ParameterObject TasksFilterRequest tasksFilterRequest) {
        return adminTaskService.getTasks(request, tasksFilterRequest);
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
            tags = {"AdminController"},
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
        return adminTaskService.getTaskById(request, id);

    }

    /**
     * Создает новую задачу.
     *
     * @param request           объект запроса.
     * @param createTaskRequest {@link CreateTaskRequest} - модель данных для создания задачи.
     */
    @Operation(
            summary = "Создать новую задачу",
            tags = {"AdminController"},
            responses = {
                    @ApiResponse(responseCode = "201", description = "Задача создана"),
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
    @PostMapping
    public void tasksPost(HttpServletRequest request,
                          @Parameter(name = "CreateTaskRequest", description = "Модель для добавления", required = true)
                          @Valid @RequestBody CreateTaskRequest createTaskRequest) {
        adminTaskService.createTask(request, createTaskRequest);
    }


    /**
     * Удаляет задачу по её id.
     *
     * @param id      id задачи.
     * @param request объект запроса.
     */
    @Operation(
            summary = "Удалить задачу",
            tags = {"AdminController"},
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача удалена"),
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
    @DeleteMapping("/{id}")
    public void deleteTask(
            HttpServletRequest request,
            @Parameter(name = "id", description = "id задачи для удаления", required = true, in = ParameterIn.PATH) @PathVariable("id") UUID id
    ) {
        adminTaskService.deleteTask(request, id);

    }

    /**
     * Редактирует задачу по ее id.
     *
     * @param id                id задачи.
     * @param request           тело запроса.
     * @param updateTaskRequest {@link UpdateTaskRequest} модель для редактирования задачи.
     */
    @Operation(
            summary = "Редактировать задачу",
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
    @PatchMapping("/{id}")
    public void editTask(
            HttpServletRequest request,
            @Parameter(name = "id", description = "id задачи для редактирования", required = true, in = ParameterIn.PATH) @PathVariable("id") UUID id,
            @Parameter(name = "UpdateTaskRequest", description = "модель для редактирования", required = true)
            @Valid @RequestBody UpdateTaskRequest updateTaskRequest
    ) {
        adminTaskService.updateTask(request, id, updateTaskRequest);

    }

    /**
     * Редактирует приоритет задачи.
     *
     * @param id                  id задачи.
     * @param request             тело запроса.
     * @param updatePriorityModel {@link UpdatePriorityModel} модель для редактирования задачи.
     */
    @Operation(
            summary = "Редактировать приоритет задачи",
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
    @PatchMapping("/priority/{id}")
    public void editTaskPriority(
            HttpServletRequest request,
            @Parameter(name = "id", description = "id задачи для редактирования", required = true, in = ParameterIn.PATH) @PathVariable("id") UUID id,
            @Parameter(name = "UpdatePriorityModel", description = "модель для редактирования", required = true)
            @Valid @RequestBody UpdatePriorityModel updatePriorityModel) {
        adminTaskService.updateTaskPriority(request, id, updatePriorityModel);
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
            @Parameter(name = "UpdatePriorityModel", description = "модель для редактирования", required = true)
            @Valid @RequestBody UpdateStatusModel updateStatusModel) {
        adminTaskService.updateTaskStatus(request, id, updateStatusModel);

    }

    /**
     * Редактирует исполнителя задачи.
     *
     * @param id               id задачи.
     * @param request          тело запроса.
     * @param updateAssigneeId {@link UpdateAssigneeId} модель для редактирования задачи.
     */
    @Operation(
            summary = "Редактировать исполнителя задачи",
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
    @PatchMapping("/assignee/{id}")
    public void editTaskStatus(
            HttpServletRequest request,
            @Parameter(name = "id", description = "id задачи для редактирования", required = true, in = ParameterIn.PATH) @PathVariable("id") UUID id,
            @Parameter(name = "UpdatePriorityModel", description = "модель для редактирования", required = true)
            @Valid @RequestBody UpdateAssigneeId updateAssigneeId) {
        adminTaskService.updateTaskAssigneeId(request, id, updateAssigneeId);
    }

}
