package org.opm.busybeaver.repository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Teams.MemberInTeamDto;
import org.opm.busybeaver.dto.Teams.ProjectByTeamDto;
import org.opm.busybeaver.dto.Teams.TeamSummaryDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.DatabaseConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Teams.TeamsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.TeamsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.opm.busybeaver.jooq.Tables.*;

@Repository
@Component
@Slf4j
public class TeamsRepository {
    private final DSLContext create;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public TeamsRepository(DSLContext dslContext) { this.create = dslContext; }

    public TeamsRecord makeNewTeam(int userID, String teamName, HttpServletRequest request)
            throws TeamsExceptions.TeamAlreadyExistsForUserException {
        // INSERT INTO Teams (Teams.team_name, Teams.team_creator)
        // VALUES
        // (teamName, userID);
        try {
            TeamsRecord newTeamRecord = create.insertInto(TEAMS, TEAMS.TEAM_NAME, TEAMS.TEAM_CREATOR)
                    .values(teamName, userID)
                    .returningResult(TEAMS.TEAM_NAME, TEAMS.TEAM_ID, TEAMS.TEAM_CREATOR)
                    .fetchSingleInto(TeamsRecord.class);

            // Trigger option - Add the creator to the TeamUsers table, role of Creator
            create.insertInto(TEAMUSERS, TEAMUSERS.TEAM_ID, TEAMUSERS.USER_ID, TEAMUSERS.USER_TEAM_ROLE)
                    .values(newTeamRecord.getTeamId(), newTeamRecord.getTeamCreator(), DatabaseConstants.TEAMUSERS_CREATOR_ROLE.getValue())
                    .execute();

            return newTeamRecord;

        } catch (DuplicateKeyException e) {
            TeamsExceptions.TeamAlreadyExistsForUserException teamAlreadyExistsForUserException =
                    new TeamsExceptions.TeamAlreadyExistsForUserException(
                            ErrorMessageConstants.TEAM_ALREADY_EXISTS_FOR_USER.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.TEAM_ALREADY_EXISTS_FOR_USER.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    teamAlreadyExistsForUserException);

            throw teamAlreadyExistsForUserException;
        }
    }

    public List<TeamSummaryDto> getUserHomePageTeams(int userId) {
        // SELECT Teams.team_id, Teams.team_name, Teams.team_creator
        // FROM Teams
        // WHERE Teams.team_id IN (
        //      SELECT TeamUsers.team_id
        //      FROM TeamUsers
        //      WHERE TeamUsers.user_id = userId
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

    public void isUserInTeamAndDoesTeamExist(int userID, int teamID, HttpServletRequest request)
            throws TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException {
        // SELECT EXISTS(
        //      SELECT TeamUsers.team_id,TeamUsers.user_id
        //      FROM TeamUsers
        //      WHERE TeamUsers.team_id = teamID
        //      AND TeamUsers.user_id = userID)
        boolean isUserInTeamAndDoesTeamExist = create.fetchExists(
                    create.selectFrom(TEAMUSERS)
                        .where(TEAMUSERS.TEAM_ID.eq(teamID))
                        .and(TEAMUSERS.USER_ID.eq(userID))
                    );

        if (!isUserInTeamAndDoesTeamExist) {
            TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException userNotInTeamOrTeamDoesNotExistException =
                    new TeamsExceptions.UserNotInTeamOrTeamDoesNotExistException(
                            ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.USER_NOT_IN_TEAM_OR_TEAM_NOT_EXIST.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    userNotInTeamOrTeamDoesNotExistException);

            throw userNotInTeamOrTeamDoesNotExistException;
        }
    }

    public Boolean isUserCreatorOfTeam(int userID, int teamID) {
        // SELECT EXISTS(
        //      SELECT *
        //      FROM Teams
        //      WHERE Teams.team_id = teamID
        //      AND Teams.team_creator = userID)
        return create.fetchExists(
                create.selectFrom(TEAMS)
                        .where(TEAMS.TEAM_ID.eq(teamID))
                        .and(TEAMS.TEAM_CREATOR.eq(userID))
            );
    }

    public TeamsRecord getSingleTeam(int teamID) {
        return create.selectFrom(TEAMS).where(TEAMS.TEAM_ID.eq(teamID)).fetchOne();
    }

    public void deleteSingleTeam(int teamID) {
        create.deleteFrom(TEAMS).where(TEAMS.TEAM_ID.eq(teamID)).execute();
    }

    public void deleteSingleTeamMember(int userID, int teamID) {
        // DELETE FROM TeamUsers
        // WHERE TeamUsers.team_id = teamID
        // AND TeamUsers.user_id = userID;
        create.deleteFrom(TEAMUSERS)
                .where(TEAMUSERS.TEAM_ID.eq(teamID))
                .and(TEAMUSERS.USER_ID.eq(userID))
                .execute();
    }

    public void doesTeamStillHaveMembers(int teamID, HttpServletRequest request)
            throws TeamsExceptions.TeamStillHasMembersException {
        // SELECT COUNT(*)
        // FROM TeamUsers
        // WHERE TeamUsers.team_id = teamID
        // LIMIT 1;
        int memberCount = create
                .selectCount()
                .from(TEAMUSERS)
                .where(TEAMUSERS.TEAM_ID.eq(teamID))
                .fetchSingleInto(int.class);

        if (memberCount > 1) {
            TeamsExceptions.TeamStillHasMembersException teamStillHasMembersException =
                    new TeamsExceptions.TeamStillHasMembersException(
                            ErrorMessageConstants.TEAM_STILL_HAS_MEMBERS.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.TEAM_STILL_HAS_MEMBERS.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    teamStillHasMembersException);

            throw teamStillHasMembersException;
        }
    }

    public List<ProjectByTeamDto> getAllProjectsAssociatedWithTeam(int userID, int teamID) {
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

    public List<MemberInTeamDto> getAllMembersInTeam(int teamID) {
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

    public void addMemberToTeam(BeaverusersRecord userToAdd, int teamID, HttpServletRequest request)
            throws TeamsExceptions.UserAlreadyInTeamException {
        // INSERT INTO TeamUsers (user_id, team_id, user_team_role)
        // VALUES (
        //      teamID,
        //      userToAdd.getUserId()
        // );
        try {
            create.insertInto(TEAMUSERS, TEAMUSERS.TEAM_ID, TEAMUSERS.USER_ID)
                    .values(teamID, userToAdd.getUserId())
                    .execute();
        } catch (DuplicateKeyException e) {
            TeamsExceptions.UserAlreadyInTeamException userAlreadyInTeamException =
                    new TeamsExceptions.UserAlreadyInTeamException(
                            ErrorMessageConstants.USER_ALREADY_IN_TEAM.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.USER_ALREADY_IN_TEAM.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    userAlreadyInTeamException);

            throw userAlreadyInTeamException;
        }
    }
}
