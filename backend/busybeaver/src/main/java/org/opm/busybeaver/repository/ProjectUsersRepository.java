package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import static org.opm.busybeaver.jooq.Tables.PROJECTUSERS;
import static org.opm.busybeaver.jooq.Tables.TEAMUSERS;

@Repository
@Component
public class ProjectUsersRepository {
    private final DSLContext create;
    @Autowired
    public ProjectUsersRepository(DSLContext dslContext) {
        this.create = dslContext;
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
