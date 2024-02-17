package org.opm.busybeaver.dto.Interfaces;

import org.opm.busybeaver.dto.Projects.TeamSummaryInProjectSummaryDto;

public interface ProjectBasicInterface {
    void setLocations(String contextPath);
    String getProjectName();
    int getProjectID();
    String getProjectLocation();
}
