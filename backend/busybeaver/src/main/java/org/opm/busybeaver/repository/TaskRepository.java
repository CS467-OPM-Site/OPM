package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Tasks.NewTaskDto;
import org.opm.busybeaver.dto.Tasks.TaskCreatedDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import static org.opm.busybeaver.jooq.tables.Tasks.TASKS;

@Repository
@Component
public class TaskRepository {
    private final DSLContext create;

    @Autowired
    public TaskRepository(DSLContext dslContext) { this.create = dslContext; }

    public TaskCreatedDto addTask(NewTaskDto newTaskDto, int projectID) {
        // INSERT INTO Tasks (title, description, project_id, column_id, assigned_to, sprint_id, priority, due_date)
        // VALUES....
        return create.insertInto(
                TASKS,
                TASKS.TITLE,
                TASKS.DESCRIPTION,
                TASKS.PROJECT_ID,
                TASKS.COLUMN_ID,
                TASKS.ASSIGNED_TO,
                TASKS.SPRINT_ID,
                TASKS.PRIORITY,
                TASKS.DUE_DATE)
                .values(
                        newTaskDto.getTitle(),
                        newTaskDto.getDescription(),
                        projectID,
                        newTaskDto.getColumnID(),
                        newTaskDto.getAssignedTo(),
                        newTaskDto.getSprintID(),
                        newTaskDto.getPriority(),
                        newTaskDto.getDueDate()
                ).returningResult(
                        TASKS.TITLE,
                        TASKS.TASK_ID,
                        TASKS.COLUMN_ID,
                        TASKS.PRIORITY,
                        TASKS.DESCRIPTION,
                        TASKS.DUE_DATE,
                        TASKS.SPRINT_ID,
                        TASKS.ASSIGNED_TO)
                .fetchSingleInto(TaskCreatedDto.class);
    }
}
