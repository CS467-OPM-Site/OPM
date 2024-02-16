package org.opm.busybeaver.dto.ProjectUsers;

import org.opm.busybeaver.dto.Interfaces.ProjectAndTeamInterface;
import org.opm.busybeaver.dto.Projects.TeamSummaryInProjectSummaryDto;
import org.opm.busybeaver.dto.Users.UserSummaryDto;
import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.time.LocalDateTime;
import java.util.List;

public final class ProjectUserSummaryDto implements ProjectAndTeamInterface {

    private final String projectName;
    private final int projectID;
    private final LocalDateTime lastUpdated;
    private String projectLocation;
    private final TeamSummaryInProjectSummaryDto team;
    private List<UserSummaryDto> users;

    @ConstructorProperties({"project_name", "project_id", "last_updated", "team_id", "team_name"})
    public ProjectUserSummaryDto(String projectName, int projectID, LocalDateTime lastUpdated, int teamID, String teamName) {
        this.projectName = projectName;
        this.projectID = projectID;
        this.lastUpdated = lastUpdated;
        this.team = new TeamSummaryInProjectSummaryDto(teamName, teamID);
    }

    public void setUsers(List<UserSummaryDto> users) {
        this.users = users;
    }

    @Override
    public void setLocations(String contextPath) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.projectLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + getProjectID();

        this.team.setTeamLocation(PATH);
    }

    @Override
    public TeamSummaryInProjectSummaryDto getTeam() {
        return team;
    }

    @Override
    public String getProjectName() {
        return projectName;
    }

    @Override
    public int getProjectID() {
        return projectID;
    }

    @Override
    public String getProjectLocation() {
        return projectLocation;
    }

    @Override
    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public List<UserSummaryDto> getUsers() {
        return users;
    }
}
