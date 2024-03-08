package org.opm.busybeaver.service;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.dto.Comments.CommentInTaskDto;
import org.opm.busybeaver.dto.Comments.NewCommentBodyDto;
import org.opm.busybeaver.dto.ProjectUsers.ProjectUserShortDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Comments.CommentsExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.jooq.tables.records.CommentsRecord;
import org.opm.busybeaver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class CommentsService {
    private final UsersRepository usersRepository;
    private final CommentsRepository commentsRepository;
    private final TasksRepository tasksRepository;
    private final ProjectUsersRepository projectUsersRepository;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

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

    public CommentInTaskDto addCommentToTask(
            UserDto userDto,
            int projectID,
            int taskID,
            NewCommentBodyDto newCommentBodyDto,
            HttpServletRequest request) {
        ProjectUserShortDto commenter = validateProjectUserValidAndInsideValidProject(userDto, projectID, request);

        tasksRepository.doesTaskExistInProject(taskID, projectID, request);
        CommentInTaskDto commentInTaskDto = commentsRepository.addComment(taskID, newCommentBodyDto, commenter);
        commentInTaskDto.setCommentLocation(request.getContextPath(), projectID, taskID);

        return commentInTaskDto;
    }

    public void modifyCommentOnTask(
            UserDto userDto,
            int projectID,
            int taskID,
            int commentID,
            @NotNull NewCommentBodyDto newCommentBodyDto,
            HttpServletRequest request) throws CommentsExceptions.CommentBodyIdenticalNotModified {
        ProjectUserShortDto commenter = validateProjectUserValidAndInsideValidProject(userDto, projectID, request);

        tasksRepository.doesTaskExistInProject(taskID, projectID, request);

        // Ensure this user commented, and that this comment exists on the specified task
        CommentsRecord comment = commentsRepository.doesCommentExistOnTask(
                taskID,
                commentID,
                commenter.userProjectID(),
                request);

        // Check if comment bodies are identical
        if (comment.getCommentBody().equals(newCommentBodyDto.commentBody())) {
            CommentsExceptions.CommentBodyIdenticalNotModified commentBodyIdenticalNotModified =
                    new CommentsExceptions.CommentBodyIdenticalNotModified(
                            ErrorMessageConstants.COMMENT_EQUIVALENT_NOT_MODIFIED.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.COMMENT_EQUIVALENT_NOT_MODIFIED.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    commentBodyIdenticalNotModified);

            throw commentBodyIdenticalNotModified;
        }

        commentsRepository.modifyCommentOnTask(taskID, commentID, newCommentBodyDto);
    }

    public void removeCommentFromTask(
            UserDto userDto,
            int projectID,
            int taskID,
            int commentID,
            HttpServletRequest request) {
        ProjectUserShortDto commenter = validateProjectUserValidAndInsideValidProject(userDto, projectID, request);

        tasksRepository.doesTaskExistInProject(taskID, projectID, request);

        // Ensure this user commented, and that this comment exists on the specified task
        commentsRepository.doesCommentExistOnTask(taskID, commentID, commenter.userProjectID(), request);

        commentsRepository.deleteComment(taskID, commentID, commenter.userProjectID());
    }

    private ProjectUserShortDto validateProjectUserValidAndInsideValidProject(
            UserDto userDto,
            int projectID,
            HttpServletRequest request) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto, request);

        // Validate user in project and project exists
        return projectUsersRepository.getUserInProject(projectID, beaverusersRecord, request);
    }
}
