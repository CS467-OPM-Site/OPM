package org.opm.busybeaver.service;

import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.dto.Sprints.*;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Sprints.SprintsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.SprintsRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public final class SprintsService implements ValidateUserAndProjectInterface {
    private final UsersRepository usersRepository;
    private final SprintsRepository sprintsRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private final ProjectsRepository projectsRepository;

    @Autowired
    public SprintsService(
            UsersRepository usersRepository,
            ProjectUsersRepository projectUsersRepository,
            SprintsRepository sprintsRepository,
            ProjectsRepository projectsRepository
    ) {
        this.usersRepository = usersRepository;
        this.projectUsersRepository = projectUsersRepository;
        this.sprintsRepository = sprintsRepository;
        this.projectsRepository = projectsRepository;
    }

    public SprintSummaryDto addSprint(UserDto userDto, int projectID, NewSprintDto newSprintDto, String contextPath) {
        validateUserValidAndInsideValidProject(userDto, projectID);

        sprintsRepository.isSprintNameInProject(projectID, newSprintDto.sprintName());

        SprintSummaryDto newSprint = sprintsRepository.addSprintToProject(projectID, newSprintDto);

        newSprint.setSprintLocation(contextPath, projectID);

        projectsRepository.updateLastUpdatedForProject(projectID);

        return newSprint;
    }

    public void removeSprintFromProject(UserDto userDto, int projectID, int sprintID) {
        validateUserValidAndInsideValidProject(userDto, projectID);

        sprintsRepository.doesSprintExistInProject(sprintID, projectID);

        sprintsRepository.removeSprintFromProject(sprintID, projectID);

        projectsRepository.updateLastUpdatedForProject(projectID);
    }

    public SprintsInProjectDto getAllSprintsForProject(UserDto userDto, int projectID, String contextPath) {
        validateUserValidAndInsideValidProject(userDto, projectID);

        SprintsInProjectDto sprintsInProjectDto = sprintsRepository.getAllSprintsForProject(projectID);
        sprintsInProjectDto.setLocations(contextPath);

        return sprintsInProjectDto;
    }

    public TasksInSprintDto getAllTasksInSprint(UserDto userDto, int projectID, int sprintID, String contextPath) {
        validateUserValidAndInsideValidProject(userDto, projectID);

        sprintsRepository.doesSprintExistInProject(sprintID, projectID);

        TasksInSprintDto tasksInSprintDto = sprintsRepository.getAllTasksInSprint(projectID, sprintID);
        tasksInSprintDto.setSprintLocation(contextPath, projectID);
        return tasksInSprintDto;
    }

    public boolean modifySprint(UserDto userDto, int projectID, int sprintID, @NotNull EditSprintDto editSprintDto) {
        validateUserValidAndInsideValidProject(userDto, projectID);

        SprintsRecord sprintToEdit = sprintsRepository.doesSprintExistInProject(sprintID, projectID);

        modifySprintName(sprintToEdit, projectID, editSprintDto);
        if (editSprintDto.getStartDate() != null && editSprintDto.getEndDate() != null) {
            // Compare the new possible dates against each other
            modifySprintStartAndEndDate(sprintToEdit, editSprintDto);
        } else {
            // Compare the new possible dates against their older partner dates
            modifySprintStartDate(sprintToEdit, editSprintDto);
            modifySprintEndDate(sprintToEdit, editSprintDto);
        }

        boolean isSprintUpdated = sprintToEdit.changed();
        if (isSprintUpdated) {
            sprintToEdit.update();
        }

        return isSprintUpdated;
    }

    private void modifySprintName(SprintsRecord sprintToEdit, int projectID, @NotNull EditSprintDto editSprintDto) {
        if (editSprintDto.getSprintName() != null &&
                !editSprintDto.getSprintName().equals(sprintToEdit.getSprintName())) {

            sprintsRepository.isSprintNameInProject(projectID, editSprintDto.getSprintName());

            sprintToEdit.setSprintName(editSprintDto.getSprintName());
        }
    }

    private void modifySprintStartAndEndDate(SprintsRecord sprintToEdit, @NotNull EditSprintDto editSprintDto)
            throws SprintsExceptions.SprintDatesInvalid {

        LocalDate newStartDate = editSprintDto.getStartDate();
        LocalDate newEndDate = editSprintDto.getEndDate();

        // Just getting rid of the IDE saying potential nullability ahead...
        if (newStartDate == null || newEndDate == null) return;

        if (!newStartDate.isBefore(newEndDate)) {
            throw new SprintsExceptions.SprintDatesInvalid(ErrorMessageConstants.SPRINT_DATES_INVALID.getValue());
        }

        if (!newStartDate.isEqual(sprintToEdit.getBeginDate())) {
            sprintToEdit.setBeginDate(newStartDate);
        }

        if (!newEndDate.isEqual(sprintToEdit.getEndDate())) {
            sprintToEdit.setEndDate(newEndDate);
        }
    }

    private void modifySprintStartDate(SprintsRecord sprintToEdit, @NotNull EditSprintDto editSprintDto)
        throws SprintsExceptions.SprintDatesInvalid {
        if (editSprintDto.getStartDate() != null) {
            LocalDate newStartDate = editSprintDto.getStartDate();
            LocalDate currentEndDate = sprintToEdit.getEndDate();

            if (!newStartDate.isBefore(currentEndDate)) {
                throw new SprintsExceptions.SprintDatesInvalid(ErrorMessageConstants.SPRINT_DATES_INVALID.getValue());
            }

            if (!newStartDate.isEqual(sprintToEdit.getBeginDate())) {
                sprintToEdit.setBeginDate(newStartDate);
            }
        }
    }

    private void modifySprintEndDate(SprintsRecord sprintToEdit, @NotNull EditSprintDto editSprintDto)
        throws SprintsExceptions.SprintDatesInvalid {

        if (editSprintDto.getEndDate() != null) {
            LocalDate newStartDate = editSprintDto.getStartDate();
            LocalDate newEndDate = editSprintDto.getEndDate();
            LocalDate currentStartDate = sprintToEdit.getBeginDate();

            if (!currentStartDate.isBefore(newEndDate)) {
                throw new SprintsExceptions.SprintDatesInvalid(ErrorMessageConstants.SPRINT_DATES_INVALID.getValue());
            }

            if (!newEndDate.isEqual(sprintToEdit.getEndDate())) {
                sprintToEdit.setEndDate(newEndDate);
            }
        }
    }

    @Override
    public BeaverusersRecord validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);

        return beaverusersRecord;
    }
}
