package org.opm.busybeaver.dto.Teams;

import org.opm.busybeaver.enums.BusyBeavPaths;

import java.util.List;

public class ProjectsByTeamDto {
    private final String teamName;
    private final Integer teamId;
    private final List<ProjectByTeamDto> projects;

    private String teamLocation;

    public ProjectsByTeamDto(
            String teamName, Integer teamId,
            List<ProjectByTeamDto> projects) {

        this.teamName = teamName;
        this.teamId = teamId;
        this.projects = projects;

        this.projects.removeIf(project ->
            project.getProjectID() == null
        );
    }

    public void setProjectAndTeamLocations(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.teamLocation = PATH + BusyBeavPaths.TEAMS.getValue() + "/" + getTeamId();
        projects.forEach(project -> project.setProjectLocation(contextPath));
    }

    public String getTeamName() {
        return teamName;
    }

    public Integer getTeamId() {
        return teamId;
    }

    public List<ProjectByTeamDto> getProjects() {
        return projects;
    }

    public String getTeamLocation() {
        return teamLocation;
    }
}
