package org.opm.busybeaver.dto.Columns;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.opm.busybeaver.dto.Interfaces.ColumnInterface;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;

public final class NewColumnDto implements ColumnInterface {
    @NotBlank
    @Size(min = 3, max = 50, message = "Column names must be 3 to 50 characters")
    public final String columnTitle;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public final int columnIndex;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public final int columnID;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    public String columnLocation;

    @ConstructorProperties({"column_title", "column_index", "column_id"})
    public NewColumnDto(String columnTitle, int columnIndex, int columnID) {
        this.columnTitle = columnTitle;
        this.columnIndex = columnIndex;
        this.columnID = columnID;
    }

    @Override
    public void setColumnLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();
        this.columnLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.COLUMNS.getValue() + "/" + getColumnID();
    }

    @Override
    public String getColumnTitle() {
        return columnTitle;
    }

    @Override
    public int getColumnIndex() {
        return columnIndex;
    }

    @Override
    public int getColumnID() {
        return columnID;
    }

    @Override
    public String getColumnLocation() {
        return columnLocation;
    }
}
