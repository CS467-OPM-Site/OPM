package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Comments.CommentInTaskDto;
import org.opm.busybeaver.dto.Comments.NewCommentBodyDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Comments.CommentsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.CommentsRecord;
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
    private final ProjectsRepository projectsRepository;

    @Autowired
    public CommentsService(
            UsersRepository usersRepository,
            CommentsRepository commentsRepository,
            TasksRepository tasksRepository,
            ProjectUsersRepository projectUsersRepository,
            ProjectsRepository projectsRepository
    ) {
        this.usersRepository = usersRepository;
        this.commentsRepository = commentsRepository;
        this.tasksRepository = tasksRepository;
        this.projectUsersRepository = projectUsersRepository;
        this.projectsRepository = projectsRepository;
    }

    public CommentInTaskDto addCommentToTask(UserDto userDto, int projectID, int taskID, NewCommentBodyDto newCommentBodyDto, String contextPath) {
        BeaverusersRecord commenter = validateUserValidAndInsideValidProject(userDto, projectID);

        tasksRepository.doesTaskExistInProject(taskID, projectID);
        CommentInTaskDto commentInTaskDto = commentsRepository.addComment(taskID, newCommentBodyDto, commenter);
        commentInTaskDto.setCommentLocation(contextPath, projectID, taskID);

        projectsRepository.updateLastUpdatedForProject(projectID);

        return commentInTaskDto;
    }

    public void modifyCommentOnTask(
            UserDto userDto,
            int projectID,
            int taskID,
            int commentID,
            NewCommentBodyDto newCommentBodyDto) throws CommentsExceptions.CommentBodyIdenticalNotModified {
        BeaverusersRecord commenter = validateUserValidAndInsideValidProject(userDto, projectID);

        tasksRepository.doesTaskExistInProject(taskID, projectID);

        // Ensure this user commented, and that this comment exists on the specified task
        CommentsRecord comment = commentsRepository.doesCommentExistOnTask(taskID, commentID, commenter.getUserId());

        // Check if comment bodies are identical
        if (comment.getCommentBody().equals(newCommentBodyDto.commentBody())) {
            throw new CommentsExceptions.CommentBodyIdenticalNotModified(
                    ErrorMessageConstants.COMMENT_EQUIVALENT_NOT_MODIFIED.getValue());
        }

        commentsRepository.modifyCommentOnTask(taskID, commentID, newCommentBodyDto);

        projectsRepository.updateLastUpdatedForProject(projectID);
    }

    public void removeCommentFromTask(UserDto userDto, int projectID, int taskID, int commentID) {
        BeaverusersRecord commenter = validateUserValidAndInsideValidProject(userDto, projectID);

        tasksRepository.doesTaskExistInProject(taskID, projectID);

        // Ensure this user commented, and that this comment exists on the specified task
        commentsRepository.doesCommentExistOnTask(taskID, commentID, commenter.getUserId());

        commentsRepository.deleteComment(taskID, commentID, commenter.getUserId());

        projectsRepository.updateLastUpdatedForProject(projectID);
    }

    @Override
    public BeaverusersRecord validateUserValidAndInsideValidProject(UserDto userDto, int projectID) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        // Validate user in project and project exists
        projectUsersRepository.isUserInProjectAndDoesProjectExist(beaverusersRecord.getUserId(), projectID);

        return beaverusersRecord;
    }
}
