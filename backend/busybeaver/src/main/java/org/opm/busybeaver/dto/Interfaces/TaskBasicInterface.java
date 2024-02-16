package org.opm.busybeaver.dto.Interfaces;

import java.time.LocalDate;

public interface TaskBasicInterface {
    public String getTitle();
    public LocalDate getDueDate();
    public String getPriority();
}
