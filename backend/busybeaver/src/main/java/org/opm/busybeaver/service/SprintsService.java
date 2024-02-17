package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Sprints.NewSprintDto;
import org.opm.busybeaver.dto.Sprints.SprintSummaryDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public final class SprintsService implements ValidateUserAndProjectInterface {
    private final UsersRepository usersRepository;
    private final SprintsRepository sprintsRepository;
    private final ProjectUsersRepository projectUsersRepository;

    @Autowired
    public SprintsService(
            UsersRepository usersRepository,
            ProjectUsersRepository projectUsersRepository,
            SprintsRepository sprintsRepository
    ) {
        this.usersRepository = usersRepository;
        this.projectUsersRepository = projectUsersRepository;
        this.sprintsRepository = sprintsRepository;
    }

    public SprintSummaryDto addSprint(UserDto userDto, int projectID, NewSprintDto newSprintDto, String contextPath) {
        validateUserValidAndInsideValidProject(userDto, projectID);

        sprintsRepository.isSprintNameInProject(projectID, newSprintDto.sprintName());

        SprintSummaryDto newSprint = sprintsRepository.addSprintToProject(projectID, newSprintDto);

        newSprint.setSprintLocation(contextPath, projectID);

        return newSprint;
    }

    @Override
    public BeaverusersRecord validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);

        return beaverusersRecord;
    }
}
