package org.opm.busybeaver.dto.Interfaces;

import org.opm.busybeaver.dto.Projects.TeamSummaryInProjectSummaryDto;

public interface ProjectBasicInterface {
    public void setLocations(String contextPath);

    public String getProjectName();
    public int getProjectID();
    public String getProjectLocation();
}
