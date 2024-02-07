package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.HomePageProjectDto;
import org.opm.busybeaver.dto.HomePageProjectsDto;
import org.opm.busybeaver.dto.UserDto;
import org.opm.busybeaver.exceptions.service.UserDoesNotExistException;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.ProjectRepository;
import org.opm.busybeaver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.opm.busybeaver.utils.Utils.verifyUserExistsAndReturn;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final UserRepository userRepository;

    @Autowired
    public ProjectService(
            ProjectRepository projectRepository,
            UserRepository userRepository
    ) {
        this.projectRepository = projectRepository;
        this.userRepository = userRepository;
    }

    public HomePageProjectsDto getUserHomePageProjects(UserDto userDto, String contextPath) throws UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = verifyUserExistsAndReturn(userDto, userRepository);

        List<HomePageProjectDto> projects =
                projectRepository.getUserHomePageProjects(beaverusersRecord.getUserId());

        projects.forEach( project -> project.setProjectAndTeamLocation(contextPath));

        return new HomePageProjectsDto(projects);
    }
}
