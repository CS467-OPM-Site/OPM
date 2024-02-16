package org.opm.busybeaver.dto.Interfaces;

import java.time.LocalDate;

public interface TaskExtendedInterface extends TaskBasicInterface {
    public String getTitle();
    public String getDescription();
    public LocalDate getDueDate();
    public String getPriority();
    public Integer getSprintID();
    public Integer getAssignedTo();
    public Integer getColumnID();
}
