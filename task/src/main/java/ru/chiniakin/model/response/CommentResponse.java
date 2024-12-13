package ru.chiniakin.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

/**
 * Comment
 */
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class CommentResponse {

    /**
     * id комментария
     */
    @NotNull
    @Schema(name = "id", description = "id комментария", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("id")
    private String id;

    /**
     * id задачи, к которой относится комментарий
     */
    @NotNull
    @Schema(name = "taskId", description = "id задачи, к которой относится комментарий", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("taskId")
    private String taskId;

    /**
     * id автора комментария
     */
    @Schema(name = "authorId", description = "id автора комментария", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    @JsonProperty("authorId")
    private String userId;

    /**
     * Текст комментария
     */
    @NotNull
    @Schema(name = "content", description = "Текст комментария", requiredMode = Schema.RequiredMode.REQUIRED)
    @JsonProperty("content")
    private String text;

}

