package org.opm.busybeaver.dto.Interfaces;

public interface ColumnInterface {
    public String getColumnTitle();
    public int getColumnIndex();
    public int getColumnID();
    public String getColumnLocation();
    public void setColumnLocation(String contextPath, int projectID);
}
