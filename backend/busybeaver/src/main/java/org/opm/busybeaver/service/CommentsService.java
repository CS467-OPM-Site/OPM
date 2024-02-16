package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Comments.CommentInTaskDto;
import org.opm.busybeaver.dto.Comments.NewCommentBodyDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.*;
import org.opm.busybeaver.service.ServiceInterfaces.ValidateUserAndProjectInterface;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CommentsService implements ValidateUserAndProjectInterface {
    private final UsersRepository usersRepository;
    private final CommentsRepository commentsRepository;
    private final TasksRepository tasksRepository;
    private final ProjectUsersRepository projectUsersRepository;

    @Autowired
    public CommentsService(
            UsersRepository usersRepository,
            CommentsRepository commentsRepository,
            TasksRepository tasksRepository,
            ProjectUsersRepository projectUsersRepository
    ) {
        this.usersRepository = usersRepository;
        this.commentsRepository = commentsRepository;
        this.tasksRepository = tasksRepository;
        this.projectUsersRepository = projectUsersRepository;
    }

    public CommentInTaskDto addCommentToTask(UserDto userDto, int projectID, int taskID, NewCommentBodyDto newCommentBodyDto, String contextPath) {
        BeaverusersRecord commenter = validateUserValidAndInsideValidProject(userDto, projectID);

        tasksRepository.doesTaskExistInProject(taskID, projectID);
        CommentInTaskDto commentInTaskDto = commentsRepository.addComment(taskID, newCommentBodyDto, commenter);
        commentInTaskDto.setCommentLocation(contextPath, projectID, taskID);
        return commentInTaskDto;
    }

    @Override
    public BeaverusersRecord validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);

        return beaverusersRecord;
    }
}
