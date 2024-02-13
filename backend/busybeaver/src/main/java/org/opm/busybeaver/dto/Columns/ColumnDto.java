package org.opm.busybeaver.dto.Columns;

import org.opm.busybeaver.dto.Tasks.TaskSummaryDto;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public final class ColumnDto {
    private final String columnTitle;
    private final int columnID;
    private final int columnIndex;

    private final List<TaskSummaryDto> tasks = new ArrayList<>();
    private String columnLocation;

    @ConstructorProperties({"column_title", "column_id", "column_index"})
    public ColumnDto(String columnTitle, int columnID, int columnIndex) {
        this.columnTitle = columnTitle;
        this.columnID = columnID;
        this.columnIndex = columnIndex;
    }

    public void setColumnLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.columnLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.COLUMNS.getValue() + "/" + getColumnID();

        if (!tasks.isEmpty()) {
            tasks.forEach(task -> task.setTaskLocation(contextPath, projectID));
        }
    }

    public void addTask(TaskSummaryDto task) {
        tasks.add(task);
    }

    public String getColumnTitle() {
        return columnTitle;
    }

    public int getColumnID() {
        return columnID;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public String getColumnLocation() {
        return columnLocation;
    }

    public List<TaskSummaryDto> getTasks() {
        return tasks;
    }
}
