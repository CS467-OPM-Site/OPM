package org.opm.busybeaver.dto.Columns;

import org.opm.busybeaver.enums.BusyBeavPaths;

public final class ColumnInTaskDto {
    private final String columnTitle;
    private final int columnID;
    private final int columnIndex;
    private String columnLocation;

    public ColumnInTaskDto(String columnTitle, int columnID, int columnIndex) {
        this.columnTitle = columnTitle;
        this.columnID = columnID;
        this.columnIndex = columnIndex;
    }

    public void setColumnLocation(String contextPath, int projectID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.columnLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.COLUMNS.getValue() + "/" + getColumnID();
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
}
