package org.opm.busybeaver.dto.Columns;

import org.opm.busybeaver.dto.Interfaces.ColumnInterface;
import org.opm.busybeaver.dto.Tasks.TaskBasicDto;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.util.ArrayList;
import java.util.List;

public final class ColumnDto implements ColumnInterface {
    private final String columnTitle;
    private final int columnID;
    private final int columnIndex;
    private final List<TaskBasicDto> tasks = new ArrayList<>();
    private String columnLocation;

    @ConstructorProperties({"column_title", "column_id", "column_index"})
    public ColumnDto(String columnTitle, int columnID, int columnIndex) {
        this.columnTitle = columnTitle;
        this.columnID = columnID;
        this.columnIndex = columnIndex;
    }

    @Override
    public void setColumnLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.columnLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.COLUMNS.getValue() + "/" + getColumnID();

        if (!tasks.isEmpty()) {
            tasks.forEach(task -> task.setTaskLocation(contextPath, projectID));
        }
    }

    public void addTask(TaskBasicDto task) {
        tasks.add(task);
    }

    @Override
    public String getColumnTitle() {
        return columnTitle;
    }

    @Override
    public int getColumnID() {
        return columnID;
    }

    @Override
    public int getColumnIndex() {
        return columnIndex;
    }

    @Override
    public String getColumnLocation() {
        return columnLocation;
    }

    public List<TaskBasicDto> getTasks() {
        return tasks;
    }
}
