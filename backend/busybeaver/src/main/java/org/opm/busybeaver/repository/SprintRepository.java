package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import static org.opm.busybeaver.jooq.Tables.SPRINTS;

@Repository
@Component
public class SprintRepository {

    private final DSLContext create;

    @Autowired
    public SprintRepository(DSLContext dslContext) { this.create = dslContext; }

    public Boolean doesSprintExistInProject(int sprintID, int projectID) {
        // SELECT EXISTS(
        //      SELECT *
        //      FROM Sprints
        //      WHERE Sprint.sprint_id = sprintID
        //      AND Sprint.project_id = projectID)
        return create.fetchExists(
                create.selectFrom(SPRINTS)
                        .where(SPRINTS.SPRINT_ID.eq(sprintID))
                        .and(SPRINTS.PROJECT_ID.eq(projectID))
        );
    }
}
