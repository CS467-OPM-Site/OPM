package org.opm.busybeaver.dto;

import org.opm.busybeaver.enums.BusyBeavPaths;

import java.util.List;

public class HomePageFilterProjectsByTeamDto {
    private final String teamName;
    private final Integer teamId;
    private List<HomePageFilterProjectByTeamDto> projects;

    private String teamLocation;

    public HomePageFilterProjectsByTeamDto(
            String teamName, Integer teamId,
            List<HomePageFilterProjectByTeamDto> projects) {

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

    public List<HomePageFilterProjectByTeamDto> getProjects() {
        return projects;
    }

    public String getTeamLocation() {
        return teamLocation;
    }
}
