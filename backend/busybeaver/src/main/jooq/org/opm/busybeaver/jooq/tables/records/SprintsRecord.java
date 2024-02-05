/*
 * This file is generated by jOOQ.
 */
package org.opm.busybeaver.jooq.tables.records;


import java.time.LocalDate;

import org.jooq.Record1;
import org.jooq.impl.UpdatableRecordImpl;
import org.opm.busybeaver.jooq.tables.Sprints;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes", "this-escape" })
public class SprintsRecord extends UpdatableRecordImpl<SprintsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>public.sprints.sprint_id</code>.
     */
    public void setSprintId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>public.sprints.sprint_id</code>.
     */
    public Integer getSprintId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>public.sprints.sprint_name</code>.
     */
    public void setSprintName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>public.sprints.sprint_name</code>.
     */
    public String getSprintName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>public.sprints.project_id</code>.
     */
    public void setProjectId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>public.sprints.project_id</code>.
     */
    public Integer getProjectId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>public.sprints.begin_date</code>.
     */
    public void setBeginDate(LocalDate value) {
        set(3, value);
    }

    /**
     * Getter for <code>public.sprints.begin_date</code>.
     */
    public LocalDate getBeginDate() {
        return (LocalDate) get(3);
    }

    /**
     * Setter for <code>public.sprints.end_date</code>.
     */
    public void setEndDate(LocalDate value) {
        set(4, value);
    }

    /**
     * Getter for <code>public.sprints.end_date</code>.
     */
    public LocalDate getEndDate() {
        return (LocalDate) get(4);
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
     * Create a detached SprintsRecord
     */
    public SprintsRecord() {
        super(Sprints.SPRINTS);
    }

    /**
     * Create a detached, initialised SprintsRecord
     */
    public SprintsRecord(Integer sprintId, String sprintName, Integer projectId, LocalDate beginDate, LocalDate endDate) {
        super(Sprints.SPRINTS);

        setSprintId(sprintId);
        setSprintName(sprintName);
        setProjectId(projectId);
        setBeginDate(beginDate);
        setEndDate(endDate);
        resetChangedOnNotNull();
    }
}
