package org.opm.busybeaver.dto.Columns;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewColumnTitleDto(
        @NotBlank(message = "Missing 'columnTitle' attribute to modify column title")
        @Size(min = 3, max = 50, message = "Column names must be 3 to 50 characters")
        String columnTitle
) {
    @Override
    public String columnTitle() {
        return columnTitle;
    }
}
