package org.opm.busybeaver.dto.Comments;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewCommentBodyDto (
        @NotBlank(message = "Comment must not be empty")
        @Size(max = 2000, message = "Comments have a maximum length of 2000 characters")
        String commentBody
) {
    @Override
    public String commentBody() {
        return commentBody;
    }
}
