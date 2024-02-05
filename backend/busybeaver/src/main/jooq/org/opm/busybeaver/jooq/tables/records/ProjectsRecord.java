/*
 * This file is generated by jOOQ.
 */
package org.opm.busybeaver.jooq.tables.records;


import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
import org.opm.busybeaver.jooq.tables.Projects;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class ProjectsRecord extends UpdatableRecordImpl<ProjectsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.projects.project_id</code>.
     */
    public void setProjectId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.projects.project_id</code>.
     */
    public Integer getProjectId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.projects.project_name</code>.
     */
    public void setProjectName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.projects.project_name</code>.
     */
    public String getProjectName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.projects.current_sprint_id</code>.
     */
    public void setCurrentSprintId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.projects.current_sprint_id</code>.
     */
    public Integer getCurrentSprintId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>public.projects.team_id</code>.
     */
    public void setTeamId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.projects.team_id</code>.
     */
    public Integer getTeamId() {
        return (Integer) get(3);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached ProjectsRecord
     */
    public ProjectsRecord() {
        super(Projects.PROJECTS);
    }

    /**
     * Create a detached, initialised ProjectsRecord
     */
    public ProjectsRecord(Integer projectId, String projectName, Integer currentSprintId, Integer teamId) {
        super(Projects.PROJECTS);

        setProjectId(projectId);
        setProjectName(projectName);
        setCurrentSprintId(currentSprintId);
        setTeamId(teamId);
        resetChangedOnNotNull();
    }
}