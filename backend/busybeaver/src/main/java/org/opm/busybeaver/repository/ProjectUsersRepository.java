package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.ProjectUsers.ProjectUserSummaryDto;
import org.opm.busybeaver.dto.Users.UserSummaryDto;
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
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectUsersRepository(DSLContext dslContext, ProjectRepository projectRepository) {
        this.create = dslContext;
        this.projectRepository = projectRepository;
    }

    public ProjectUserSummaryDto getAllUsersInProject(int projectID) {
        // Get project and team details
        ProjectUserSummaryDto projectUserSummaryDto = projectRepository.getProjectAndTeamSummary(projectID);

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

    public Boolean isUserInProjectAndDoesProjectExist(int userID, int projectID) {
        // SELECT EXISTS(
        //      SELECT ProjectUsers.project_id,ProjectUsers.user_id
        //      FROM ProjectUsers
        //      WHERE ProjectUsers.project_id = projectID
        //      AND ProjectUsers.user_id = userID
        return create.fetchExists(
                create.selectFrom(PROJECTUSERS)
                        .where(PROJECTUSERS.PROJECT_ID.eq(projectID))
                        .and(PROJECTUSERS.USER_ID.eq(userID))
        );
    }
}
