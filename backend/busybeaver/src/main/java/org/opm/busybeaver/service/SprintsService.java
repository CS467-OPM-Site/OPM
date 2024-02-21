package org.opm.busybeaver.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.dto.Sprints.*;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
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
@Slf4j
public final class SprintsService implements ValidateUserAndProjectInterface {
    private final UsersRepository usersRepository;
    private final SprintsRepository sprintsRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private final ProjectsRepository projectsRepository;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

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

    public @NotNull SprintSummaryDto addSprint(
            UserDto userDto,
            int projectID,
            @NotNull NewSprintDto newSprintDto,
            HttpServletRequest request) {
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        sprintsRepository.isSprintNameInProject(projectID, newSprintDto.sprintName(), request);

        SprintSummaryDto newSprint = sprintsRepository.addSprintToProject(projectID, newSprintDto);

        newSprint.setSprintLocation(request.getContextPath(), projectID);

        projectsRepository.updateLastUpdatedForProject(projectID);

        return newSprint;
    }

    public void removeSprintFromProject(UserDto userDto, int projectID, int sprintID, HttpServletRequest request) {
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        sprintsRepository.doesSprintExistInProject(sprintID, projectID, request);

        sprintsRepository.removeSprintFromProject(sprintID, projectID);

        projectsRepository.updateLastUpdatedForProject(projectID);
    }

    public @NotNull SprintsInProjectDto getAllSprintsForProject(UserDto userDto, int projectID, HttpServletRequest request) {
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        SprintsInProjectDto sprintsInProjectDto = sprintsRepository.getAllSprintsForProject(projectID);
        sprintsInProjectDto.setLocations(request.getContextPath());

        return sprintsInProjectDto;
    }

    public @NotNull TasksInSprintDto getAllTasksInSprint(UserDto userDto, int projectID, int sprintID, HttpServletRequest request) {
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        sprintsRepository.doesSprintExistInProject(sprintID, projectID, request);

        TasksInSprintDto tasksInSprintDto = sprintsRepository.getAllTasksInSprint(projectID, sprintID);
        tasksInSprintDto.setSprintLocation(request.getContextPath(), projectID);
        return tasksInSprintDto;
    }

    public boolean modifySprint(
            UserDto userDto,
            int projectID,
            int sprintID,
            @NotNull EditSprintDto editSprintDto,
            HttpServletRequest request) {
        validateUserValidAndInsideValidProject(userDto, projectID, request);

        SprintsRecord sprintToEdit = sprintsRepository.doesSprintExistInProject(sprintID, projectID, request);

        modifySprintName(sprintToEdit, projectID, editSprintDto, request);
        if (editSprintDto.getStartDate() != null && editSprintDto.getEndDate() != null) {
            // Compare the new possible dates against each other
            modifySprintStartAndEndDate(sprintToEdit, editSprintDto, request);
        } else {
            // Compare the new possible dates against their older partner dates
            modifySprintStartDate(sprintToEdit, editSprintDto, request);
            modifySprintEndDate(sprintToEdit, editSprintDto, request);
        }

        boolean isSprintUpdated = sprintToEdit.changed();
        if (isSprintUpdated) {
            sprintToEdit.update();
            projectsRepository.updateLastUpdatedForProject(projectID);
        }

        return isSprintUpdated;
    }

    private void modifySprintName(
            SprintsRecord sprintToEdit,
            int projectID,
            @NotNull EditSprintDto editSprintDto,
            HttpServletRequest request) {
        if (editSprintDto.getSprintName() != null &&
                !editSprintDto.getSprintName().equals(sprintToEdit.getSprintName())) {

            sprintsRepository.isSprintNameInProject(projectID, editSprintDto.getSprintName(), request);

            sprintToEdit.setSprintName(editSprintDto.getSprintName());
            log.info("Sprint name modified. | RID: {}", request.getAttribute(RID));
        } else {
            log.info("Sprint name not modified. | RID: {}", request.getAttribute(RID));
        }

    }

    private void modifySprintStartAndEndDate(
            SprintsRecord sprintToEdit,
            @NotNull EditSprintDto editSprintDto,
            HttpServletRequest request)
            throws SprintsExceptions.SprintDatesInvalid {

        LocalDate newStartDate = editSprintDto.getStartDate();
        LocalDate newEndDate = editSprintDto.getEndDate();

        // Just getting rid of the IDE saying potential nullability ahead...
        if (newStartDate == null || newEndDate == null) return;
        log.info("New start date: {} | New end date: {} | RID: {}", newStartDate, newEndDate, request.getAttribute(RID));

        if (!newStartDate.isBefore(newEndDate)) {
            SprintsExceptions.SprintDatesInvalid sprintDatesInvalid =
                    new SprintsExceptions.SprintDatesInvalid(ErrorMessageConstants.SPRINT_DATES_INVALID.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.SPRINT_DATES_INVALID,
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    sprintDatesInvalid);

            throw sprintDatesInvalid;
        }

        if (!newStartDate.isEqual(sprintToEdit.getBeginDate())) {
            sprintToEdit.setBeginDate(newStartDate);
            log.info("Set the new sprint start date. | RID: {}", request.getAttribute(RID));
        }

        if (!newEndDate.isEqual(sprintToEdit.getEndDate())) {
            sprintToEdit.setEndDate(newEndDate);
            log.info("Set the new sprint end date. | RID: {}", request.getAttribute(RID));
        }
    }

    private void modifySprintStartDate(
            SprintsRecord sprintToEdit,
            @NotNull EditSprintDto editSprintDto,
            HttpServletRequest request)
        throws SprintsExceptions.SprintDatesInvalid {
        if (editSprintDto.getStartDate() != null) {
            LocalDate newStartDate = editSprintDto.getStartDate();
            LocalDate currentEndDate = sprintToEdit.getEndDate();
            log.info("New start date: {} | Current end date: {} | RID: {}",
                    newStartDate,
                    currentEndDate,
                    request.getAttribute(RID));

            if (!newStartDate.isBefore(currentEndDate)) {
                SprintsExceptions.SprintDatesInvalid sprintDatesInvalid =
                        new SprintsExceptions.SprintDatesInvalid(ErrorMessageConstants.SPRINT_DATES_INVALID.getValue());

                log.error("{}. | RID: {} {}",
                        ErrorMessageConstants.SPRINT_DATES_INVALID.getValue(),
                        request.getAttribute(RID),
                        System.lineSeparator(),
                        sprintDatesInvalid);

                throw sprintDatesInvalid;
            }

            if (!newStartDate.isEqual(sprintToEdit.getBeginDate())) {
                sprintToEdit.setBeginDate(newStartDate);
                log.info("Set new sprint start date. | RID: {}", request.getAttribute(RID));
            }
        }
    }

    private void modifySprintEndDate(
            SprintsRecord sprintToEdit,
            @NotNull EditSprintDto editSprintDto,
            HttpServletRequest request)
        throws SprintsExceptions.SprintDatesInvalid {

        if (editSprintDto.getEndDate() != null) {
            LocalDate newEndDate = editSprintDto.getEndDate();
            LocalDate currentStartDate = sprintToEdit.getBeginDate();
            log.info("New end date: {} | Current start date: {} | RID: {}",
                    newEndDate,
                    currentStartDate,
                    request.getAttribute(RID));

            if (!currentStartDate.isBefore(newEndDate)) {
                SprintsExceptions.SprintDatesInvalid sprintDatesInvalid =
                        new SprintsExceptions.SprintDatesInvalid(ErrorMessageConstants.SPRINT_DATES_INVALID.getValue());

                log.error("{}. | RID: {} {}",
                        ErrorMessageConstants.SPRINT_DATES_INVALID.getValue(),
                        request.getAttribute(RID),
                        System.lineSeparator(),
                        sprintDatesInvalid);

                throw sprintDatesInvalid;
            }

            if (!newEndDate.isEqual(sprintToEdit.getEndDate())) {
                sprintToEdit.setEndDate(newEndDate);
                log.info("Set new sprint end date. | RID: {}", request.getAttribute(RID));
            }
        }
    }

    @Override
    public @NotNull BeaverusersRecord validateUserValidAndInsideValidProject(
            UserDto userDto,
            int projectID,
            HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID, request);

        return beaverusersRecord;
    }
}
