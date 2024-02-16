package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Sprints.SprintsExceptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import static org.opm.busybeaver.jooq.Tables.SPRINTS;

@Repository
@Component
public class SprintsRepository {

    private final DSLContext create;

    @Autowired
    public SprintsRepository(DSLContext dslContext) { this.create = dslContext; }

    public void doesSprintExistInProject(int sprintID, int projectID)
            throws SprintsExceptions.SprintDoesNotExistInProject {
        // SELECT EXISTS(
        //      SELECT *
        //      FROM Sprints
        //      WHERE Sprint.sprint_id = sprintID
        //      AND Sprint.project_id = projectID)
        boolean isSprintInProject = create.fetchExists(
                create.selectFrom(SPRINTS)
                        .where(SPRINTS.SPRINT_ID.eq(sprintID))
                        .and(SPRINTS.PROJECT_ID.eq(projectID))
        );

        if (!isSprintInProject) {
            throw new SprintsExceptions.SprintDoesNotExistInProject(
                    ErrorMessageConstants.SPRINT_NOT_IN_PROJECT.getValue());
        }
    }
}
