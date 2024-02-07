package org.opm.busybeaver.dto;

import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;

public class HomePageProjectDto {
    private final String projectName;
    private final Integer projectID;
    private String projectLocation;
    private final HomePageProjectTeamDto team;

    @ConstructorProperties({"project_name", "project_id", "team_id", "team_name"})
    public HomePageProjectDto(String projectName, int projectID, int teamID, String teamName) {
        this.projectName = projectName;
        this.projectID = projectID;
        this.team = new HomePageProjectTeamDto(teamName, teamID);
    }

    public void setProjectAndTeamLocation(String contextPath) {
        final String PATH = contextPath +
                BusyBeavPaths.V1.getValue();

        this.projectLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + getProjectID();

        this.team.setTeamLocation(PATH);
    }

    public HomePageProjectTeamDto getTeam() {
        return team;
    }

    public String getProjectName() {
        return projectName;
    }

    public Integer getProjectID() {
        return projectID;
    }

    public String getProjectLocation() {
        return projectLocation;
    }
}
