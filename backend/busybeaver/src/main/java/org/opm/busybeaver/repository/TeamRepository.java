package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Teams.MemberInTeamDto;
import org.opm.busybeaver.dto.Teams.ProjectByTeamDto;
import org.opm.busybeaver.dto.Teams.TeamSummaryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.opm.busybeaver.jooq.Tables.*;

@Repository
@Component
public class TeamRepository {
    private final DSLContext create;

    @Autowired
    public TeamRepository(DSLContext dslContext) { this.create = dslContext; }

    public List<TeamSummaryDto> getUserHomePageTeams(Integer userId) {
        // TO model:
        // SELECT Teams.team_id, Teams.team_name, Teams.team_creator
        // FROM Teams
        // WHERE Teams.team_id IN (
        //      SELECT TeamUsers.team_id
        //      FROM TeamUsers
        //      WHERE TeamUsers.user_id = 1
        //      );
        return create
                .select(TEAMS.TEAM_ID, TEAMS.TEAM_NAME, TEAMS.TEAM_CREATOR)
                .from(TEAMS)
                .where(TEAMS.TEAM_ID.in(
                                create.select(TEAMUSERS.TEAM_ID)
                                        .from(TEAMUSERS)
                                        .where(TEAMUSERS.USER_ID.eq(userId))
                        )
                )
                .fetchInto(TeamSummaryDto.class);
    }

    public Boolean isUserInTeamAndDoesTeamExist(Integer userID, Integer teamID) {
        // SELECT EXISTS(
        //      SELECT TeamUsers.team_id,TeamUsers.user_id
        //      FROM TeamUsers
        //      WHERE TeamUsers.team_id = teamID
        //      AND TeamUsers.user_id = userID
        return create.fetchExists(
                    create.selectFrom(TEAMUSERS)
                        .where(TEAMUSERS.TEAM_ID.eq(teamID))
                        .and(TEAMUSERS.USER_ID.eq(userID))
                    );
    }

    public List<ProjectByTeamDto> getAllProjectsAssociatedWithTeam(Integer userID, Integer teamID) {
        // SELECT Teams.team_name, Teams.team_id, Projects.project_name, Projects.project_id, Projects.last_updated
        // FROM Teams
        // LEFT JOIN Projects
        // ON Teams.team_id = Projects.team_id
        // LEFT JOIN ProjectUsers
        // ON Projects.project_id = ProjectUsers.project_id
        // AND ProjectUsers.user_id = userID
        // WHERE Teams.team_id = teamID;

        // Select all projects associated with a team, but also verify that the user is still in that project

        return create.select(TEAMS.TEAM_NAME, TEAMS.TEAM_ID, PROJECTS.PROJECT_NAME, PROJECTS.PROJECT_ID, PROJECTS.LAST_UPDATED)
                .from(TEAMS)
                .leftJoin(PROJECTS)
                .on(TEAMS.TEAM_ID.eq(PROJECTS.TEAM_ID))
                .leftJoin(PROJECTUSERS)
                .on(PROJECTS.PROJECT_ID.eq(PROJECTUSERS.PROJECT_ID))
                .and(PROJECTUSERS.USER_ID.eq(userID))
                .where(TEAMS.TEAM_ID.eq(teamID))
                .fetchInto(ProjectByTeamDto.class);
    }

    public List<MemberInTeamDto> getAllMembersInTeam(Integer teamID) {
        // SELECT Teams.team_name, Teams.team_id, Teams.team_creator, BeaverUsers.username, BeaverUsers.user_id
        // FROM Teams
        // JOIN TeamUsers
        // ON TeamUsers.team_id = Teams.team_id
        // JOIN BeaverUsers
        // ON BeaverUsers.user_id = TeamUsers.user_id
        // WHERE Teams.team_id = teamID;

        return create.select(TEAMS.TEAM_NAME, TEAMS.TEAM_ID, TEAMS.TEAM_CREATOR, BEAVERUSERS.USERNAME, BEAVERUSERS.USER_ID)
                .from(TEAMS)
                .join(TEAMUSERS)
                .on(TEAMUSERS.TEAM_ID.eq(TEAMS.TEAM_ID))
                .join(BEAVERUSERS)
                .on(BEAVERUSERS.USER_ID.eq(TEAMUSERS.USER_ID))
                .where(TEAMS.TEAM_ID.eq(teamID))
                .fetchInto(MemberInTeamDto.class);
    }
}
