package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.ProjectUsers.ProjectUserSummaryDto;
import org.opm.busybeaver.dto.Users.UserSummaryDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.util.List;

import static org.opm.busybeaver.jooq.Tables.BEAVERUSERS;
import static org.opm.busybeaver.jooq.Tables.PROJECTUSERS;

@Repository
@Component
public class ProjectUsersRepository {
    private final DSLContext create;
    private final ProjectsRepository projectsRepository;

    @Autowired
    public ProjectUsersRepository(DSLContext dslContext, ProjectsRepository projectsRepository) {
        this.create = dslContext;
        this.projectsRepository = projectsRepository;
    }

    public ProjectUserSummaryDto getAllUsersInProject(int projectID) {
        // Get project and team details
        ProjectUserSummaryDto projectUserSummaryDto = projectsRepository.getProjectAndTeamSummary(projectID);

        // Get all users in project
        // SELECT BeaverUsers.username, ProjectUsers.user_id,
        // FROM ProjectUsers
        // JOIN BeaverUsers
        // ON ProjectUsers.user_id = BeaverUsers.user_id
        // WHERE ProjectUsers.project_id = projectID;
        List<UserSummaryDto> projectUsers = create.select(BEAVERUSERS.USERNAME, PROJECTUSERS.USER_ID)
                .from(PROJECTUSERS)
                .join(BEAVERUSERS)
                .on(PROJECTUSERS.USER_ID.eq(BEAVERUSERS.USER_ID))
                .where(PROJECTUSERS.PROJECT_ID.eq(projectID))
                .fetchInto(UserSummaryDto.class);

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

    public boolean isUserInProjectAndDoesProjectExist(int userID, int projectID)
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
            throw new ProjectsExceptions.UserNotInProjectOrProjectDoesNotExistException(
                    ErrorMessageConstants.USER_NOT_IN_PROJECT_OR_PROJECT_NOT_EXIST.getValue());
        }
        return true;
    }
}
