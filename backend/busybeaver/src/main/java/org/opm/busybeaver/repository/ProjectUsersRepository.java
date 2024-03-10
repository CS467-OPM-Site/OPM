package org.opm.busybeaver.repository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jooq.DSLContext;
import org.opm.busybeaver.dto.ProjectUsers.ProjectUserShortDto;
import org.opm.busybeaver.dto.ProjectUsers.ProjectUserSummaryDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.ProjectUsers.ProjectUsersExceptions;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.jooq.impl.DSL.val;
import static org.opm.busybeaver.jooq.Tables.*;

@Repository
@Component
@Slf4j
public class ProjectUsersRepository {
    private final DSLContext create;
    private final ProjectsRepository projectsRepository;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public ProjectUsersRepository(DSLContext dslContext, ProjectsRepository projectsRepository) {
        this.create = dslContext;
        this.projectsRepository = projectsRepository;
    }

    public ProjectUserShortDto getUserInProject(int projectID, BeaverusersRecord user, HttpServletRequest request)
        throws ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException {
        // SELECT BeaverUsers.username, Project ProjectUsers.user_id, ProjectUsers.user_project_id
        // FROM ProjectUsers
        // JOIN BeaverUsers
        // ON ProjectUsers.user_id = BeaverUsers.userID
        // WHERE ProjectUsers.project_id = projectid
        // AND ProjectUsers.user_id = user.user_id;
        ProjectUserShortDto projectUserShortDto =
                create.select(BEAVERUSERS.USERNAME, PROJECTUSERS.USER_ID, PROJECTUSERS.USER_PROJECT_ID)
                        .from(PROJECTUSERS)
                        .join(BEAVERUSERS)
                        .on(PROJECTUSERS.USER_ID.eq(BEAVERUSERS.USER_ID))
                        .where(PROJECTUSERS.PROJECT_ID.eq(projectID))
                        .and(PROJECTUSERS.USER_ID.eq(user.getUserId()))
                        .fetchOneInto(ProjectUserShortDto.class);

        if (projectUserShortDto == null) {
            ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException userNotInProjectOrProjectDoesNotExistException =
                    new ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException(
                            ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    userNotInProjectOrProjectDoesNotExistException);

            throw userNotInProjectOrProjectDoesNotExistException;
        }

        return projectUserShortDto;
    }

    public ProjectUserSummaryDto getAllUsersInProject(int projectID) {
        // Get project and team details
        ProjectUserSummaryDto projectUserSummaryDto = projectsRepository.getProjectAndTeamSummary(projectID);

        // Get all users in project
        // SELECT BeaverUsers.username, ProjectUsers.user_id, ProjectUsers.user_project_id
        // FROM ProjectUsers
        // JOIN BeaverUsers
        // ON ProjectUsers.user_id = BeaverUsers.user_id
        // WHERE ProjectUsers.project_id = projectID;
        List<ProjectUserShortDto> projectUsers =
                create.select(BEAVERUSERS.USERNAME, PROJECTUSERS.USER_ID, PROJECTUSERS.USER_PROJECT_ID)
                    .from(PROJECTUSERS)
                    .join(BEAVERUSERS)
                    .on(PROJECTUSERS.USER_ID.eq(BEAVERUSERS.USER_ID))
                    .where(PROJECTUSERS.PROJECT_ID.eq(projectID))
                    .fetchInto(ProjectUserShortDto.class);

        projectUserSummaryDto.setUsers(projectUsers);

        return projectUserSummaryDto;
    }

    public void addUserToProject(int projectID, int userID) {
        create.insertInto(PROJECTUSERS, PROJECTUSERS.PROJECT_ID, PROJECTUSERS.USER_ID)
                .values(projectID, userID)
                .execute();
    }

    public void removeUserFromProject(int projectID, int userID) {
        create.deleteFrom(PROJECTUSERS)
                .where(PROJECTUSERS.PROJECT_ID.eq(projectID))
                .and(PROJECTUSERS.USER_ID.eq(userID))
                .execute();
    }

    public Boolean doesProjectStillHaveUsers(int projectID) {
        // SELECT COUNT(*)
        // FROM ProjectUsers
        // WHERE ProjectUsers.project_id = projectID
        // LIMIT 1;
        int projectUserCount = create
                .selectCount()
                .from(PROJECTUSERS)
                .where(PROJECTUSERS.PROJECT_ID.eq(projectID))
                .fetchSingleInto(int.class);

        return (projectUserCount > 1);
    }

    public boolean isUserInProject(int userID, int projectID) {
        return create.fetchExists(
                create.selectFrom(PROJECTUSERS)
                        .where(PROJECTUSERS.PROJECT_ID.eq(projectID))
                        .and(PROJECTUSERS.USER_ID.eq(userID))
        );
    }

    public boolean isUserInProjectAndDoesProjectExist(int userID, int projectID, HttpServletRequest request)
            throws ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException {
        // SELECT EXISTS(
        //      SELECT ProjectUsers.project_id,ProjectUsers.user_id
        //      FROM ProjectUsers
        //      WHERE ProjectUsers.project_id = projectID
        //      AND ProjectUsers.user_id = userID
        boolean isValidUserInValidProject = create.fetchExists(
                create.selectFrom(PROJECTUSERS)
                        .where(PROJECTUSERS.PROJECT_ID.eq(projectID))
                        .and(PROJECTUSERS.USER_ID.eq(userID))
        );

        if (!isValidUserInValidProject) {
            ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException userNotInProjectOrProjectDoesNotExistException =
                    new ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException(
                            ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    userNotInProjectOrProjectDoesNotExistException);

            throw userNotInProjectOrProjectDoesNotExistException;
        }
        return true;
    }

    public void isAssignedToUserInProject(int userProjectID, int projectID, HttpServletRequest request)
            throws ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException {
        // SELECT EXISTS(
        //      SELECT ProjectUsers.project_id,ProjectUsers.user_id
        //      FROM ProjectUsers
        //      WHERE ProjectUsers.project_id = projectID
        //      AND ProjectUsers.user_project_id = userProjectID
        boolean isValidUserInValidProject = create.fetchExists(
                create.selectFrom(PROJECTUSERS)
                        .where(PROJECTUSERS.PROJECT_ID.eq(projectID))
                        .and(PROJECTUSERS.USER_PROJECT_ID.eq(userProjectID))
        );

        if (!isValidUserInValidProject) {
            ProjectUsersExceptions.AssignedToUserNotInProjectOrNonexistent assignedToUserNotInProjectOrNonexistent =
                    new ProjectUsersExceptions.AssignedToUserNotInProjectOrNonexistent(
                            ErrorMessageConstants.ASSIGNED_TO_USER_NOT_IN_PROJECT_OR_USER_NOT_EXIST.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.ASSIGNED_TO_USER_NOT_IN_PROJECT_OR_USER_NOT_EXIST.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    assignedToUserNotInProjectOrNonexistent);

            throw assignedToUserNotInProjectOrNonexistent;
        }
    }

    @Transactional
    public void addUserToAllProjectsOfTeam(int userID, int teamID) {
        // INSERT INTO ProjectUsers (user_id, project_id)
        //  SELECT userID AS user_id, project_id
        //  FROM Projects
        //  WHERE team_id = teamID:
        create.insertInto(PROJECTUSERS, PROJECTUSERS.USER_ID, PROJECTUSERS.PROJECT_ID)
                .select(
                        create.select(val(userID).as(PROJECTUSERS.USER_ID), PROJECTS.PROJECT_ID)
                                .from(PROJECTS)
                                .where(PROJECTS.TEAM_ID.eq(teamID))
                ).execute();
    }
}
