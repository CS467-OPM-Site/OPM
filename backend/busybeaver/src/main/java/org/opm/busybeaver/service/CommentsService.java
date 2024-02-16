package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Comments.NewCommentBodyDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Projects.ProjectsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentsService implements ValidateUserAndProjectInterface {

    private final ProjectsRepository projectsRepository;
    private final UsersRepository usersRepository;
    private final ColumnsRepository columnsRepository;
    private final TasksRepository tasksRepository;
    private final ProjectUsersRepository projectUsersRepository;

    @Autowired
    public CommentsService(
            ProjectsRepository projectsRepository,
            UsersRepository usersRepository,
            ColumnsRepository columnsRepository,
            TasksRepository tasksRepository,
            ProjectUsersRepository projectUsersRepository
    ) {
        this.projectsRepository = projectsRepository;
        this.usersRepository = usersRepository;
        this.columnsRepository = columnsRepository;
        this.tasksRepository = tasksRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public void addCommentToTask(UserDto userDto, int projectID, int taskID, NewCommentBodyDto newCommentBodyDto) {
        validateUserValidAndInsideValidProject(userDto, projectID);

    }
    @Override
    public void validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);
    }
}
