package org.opm.busybeaver.dto.Columns;

import jakarta.validation.constraints.*;

public record NewColumnIndexDto (
        @NotNull(message = "Missing 'columnIndex' attribute to generate a new project")
        @Min(value = 0, message = "columnIndex must be a positive, or zero, integer index of the new position of this column")
        Integer columnIndex
) {
    @Override
    public Integer columnIndex() {
        return columnIndex;
    }
}

