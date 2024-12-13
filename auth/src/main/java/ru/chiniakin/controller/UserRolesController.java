package ru.chiniakin.controller;

import ru.chiniakin.model.ApiError;
import ru.chiniakin.model.RoleResponse;
import ru.chiniakin.service.interfaces.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * Контроллер для получения ролей пользователя.
 *
 * @author ChiniakinD
 */
@RestController
@AllArgsConstructor
@RequestMapping("/user-roles")
@Tag(name = "UserRolesController", description = "Контроллер для работы с ролями пользователей.")
public class UserRolesController {

    private final RoleService roleService;

    /**
     * Возвращает роли указанного пользователя.
     *
     * @param login   логин пользователя.
     * @param request запрос для авторизации.
     * @return роли пользвателя.
     */
    @Operation(summary = "Получение ролей пользователя", security = @SecurityRequirement(name = "jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            }),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            }),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            })
    })
    @GetMapping("/{login}")
    public Set<RoleResponse> getRoles(@PathVariable String login, HttpServletRequest request) {
        return roleService.getRoles(login, request);
    }

}
