package org.opm.busybeaver.dto.Teams;

import org.opm.busybeaver.dto.Interfaces.TeamInterface;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.util.List;

public class ProjectsByTeamDto implements TeamInterface {
    private final String teamName;
    private final int teamId;
    private final List<ProjectByTeamDto> projects;

    private String teamLocation;

    public ProjectsByTeamDto(
            String teamName, int teamId,
            List<ProjectByTeamDto> projects) {

        this.teamName = teamName;
        this.teamId = teamId;
        this.projects = projects;

        this.projects.removeIf(project ->
            project.getProjectID() == null
        );
    }

    @Override
    public void setLocations(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.teamLocation = PATH + BusyBeavPaths.TEAMS.getValue() + "/" + getTeamID();
        projects.forEach(project -> project.setLocations(contextPath));
    }

    @Override
    public String getTeamName() {
        return teamName;
    }

    @Override
    public int getTeamID() {
        return teamId;
    }

    public List<ProjectByTeamDto> getProjects() {
        return projects;
    }

    public String getTeamLocation() {
        return teamLocation;
    }
}
