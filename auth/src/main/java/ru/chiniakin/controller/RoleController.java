package ru.chiniakin.controller;

import ru.chiniakin.model.ApiError;
import ru.chiniakin.model.RoleModel;
import ru.chiniakin.service.interfaces.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с ролями пользователей.
 *
 * @author ChiniakinD
 */
@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
@Tag(name = "RoleController", description = "Контроллер для работы с ролями пользователей.")
public class RoleController {

    private final RoleService roleService;

    /**
     * Выполняет сохранение перечня ролей пользователю.
     *
     * @param roleModel модель для сохранения.
     * @param request   запрос для авторизации.
     */
    @Operation(summary = "Сохранение ролей пользователя", security = @SecurityRequirement(name = "jwt"))
    @ApiResponses(value = {
            @ApiResponse(responseCode = "400", description = "Некорректный запрос", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            }),
            @ApiResponse(responseCode = "401", description = "Ошибка авторизации", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            }),
            @ApiResponse(responseCode = "403", description = "Нет доступа", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            }),
            @ApiResponse(responseCode = "500", description = "Внутренняя ошибка сервера", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ApiError.class))
            })
    })
    @PutMapping("/save")
    public void saveRole(@RequestBody RoleModel roleModel, HttpServletRequest request) {
        roleService.saveRole(roleModel, request);
    }

}