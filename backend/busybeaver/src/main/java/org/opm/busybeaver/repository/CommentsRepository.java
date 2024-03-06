package org.opm.busybeaver.repository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.Record3;
import org.opm.busybeaver.dto.Comments.CommentInTaskDto;
import org.opm.busybeaver.dto.Comments.NewCommentBodyDto;
import org.opm.busybeaver.dto.ProjectUsers.ProjectUserShortDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Comments.CommentsExceptions;
import org.opm.busybeaver.jooq.tables.records.CommentsRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

import static org.opm.busybeaver.jooq.Tables.*;

@Repository
@Component
@Slf4j
public class CommentsRepository {

    private final DSLContext create;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public CommentsRepository(DSLContext dslContext) {
        this.create = dslContext;
    }

    public CommentInTaskDto addComment(
            int taskID,
            @NotNull NewCommentBodyDto newCommentBodyDto,
            ProjectUserShortDto commenter) {
        Record3<Integer, String, OffsetDateTime> newComment = create.insertInto(COMMENTS, COMMENTS.USER_ID, COMMENTS.TASK_ID, COMMENTS.COMMENT_BODY)
                .values(commenter.userProjectID(), taskID, newCommentBodyDto.commentBody())
                .returningResult(COMMENTS.COMMENT_ID, COMMENTS.COMMENT_BODY, COMMENTS.COMMENT_CREATED)
                .fetchSingle();

        return new CommentInTaskDto(
                newComment.getValue(COMMENTS.COMMENT_ID),
                newComment.getValue(COMMENTS.COMMENT_BODY),
                commenter.username(),
                commenter.userProjectID(),
                newComment.getValue(COMMENTS.COMMENT_CREATED));
    }

    public CommentsRecord doesCommentExistOnTask(int taskID, int commentID, int userProjectID, HttpServletRequest request)
            throws CommentsExceptions.CommentDoesNotExistOnTask,
            CommentsExceptions.UserDidNotLeaveThisComment {
        //  SELECT Comments.comment_id
        //  FROM Comments
        //  WHERE Comments.comment_id = commentID
        //  AND Comments.task_id = taskID
        //  AND Comments.user_id = userProjectID;

        CommentsRecord commentOnTask =
                create.selectFrom(COMMENTS)
                        .where(COMMENTS.COMMENT_ID.eq(commentID))
                        .and(COMMENTS.TASK_ID.eq(taskID))
                        .fetchOne();

        if (commentOnTask == null) {
            CommentsExceptions.CommentDoesNotExistOnTask commentDoesNotExistOnTask =
                    new CommentsExceptions.CommentDoesNotExistOnTask(
                            ErrorMessageConstants.COMMENT_NOT_FOUND_ON_TASK.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.COMMENT_NOT_FOUND_ON_TASK.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    commentDoesNotExistOnTask);

            throw commentDoesNotExistOnTask;
        }

        if (commentOnTask.getUserId() != userProjectID) {
            CommentsExceptions.UserDidNotLeaveThisComment userDidNotLeaveThisComment =
                    new CommentsExceptions.UserDidNotLeaveThisComment(
                            ErrorMessageConstants.USER_DID_NOT_LEAVE_THIS_COMMENT.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.USER_DID_NOT_LEAVE_THIS_COMMENT.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    userDidNotLeaveThisComment);

            throw userDidNotLeaveThisComment;
        }
        return commentOnTask;
    }

    public void deleteComment(int taskID, int commentID, int userProjectID) {
        // DELETE FROM Comments
        // WHERE Comments.task_id = taskID
        // AND Comments.comment_id = commentID
        // AND Comments.user_id = userProjectID;
        create.deleteFrom(COMMENTS)
                .where(COMMENTS.TASK_ID.eq(taskID))
                .and(COMMENTS.COMMENT_ID.eq(commentID))
                .and(COMMENTS.USER_ID.eq(userProjectID))
                .execute();
    }

    public void modifyCommentOnTask(int taskID, int commentID, @NotNull NewCommentBodyDto newCommentBodyDto) {
        // UPDATE Comments SET Comments.comment_body = newCommentBodyDto.CommentBody()
        // WHERE Comments.task_id = taskID
        // AND Comments.comment_id = commentID;
        create.update(COMMENTS)
                .set(COMMENTS.COMMENT_BODY, newCommentBodyDto.commentBody())
                .where(COMMENTS.COMMENT_ID.eq(commentID))
                .and(COMMENTS.TASK_ID.eq(taskID))
                .execute();
    }

    public List<CommentInTaskDto> getCommentsOnTask(int taskID) {
        // SELECT Comments.comment_id, Comments.comment_body, BeaverUsers.username,
        //          Comments.user_id, Comments.commented_created
        // FROM Comments
        // JOIN ProjectUsers
        // ON Comments.user_id = ProjectUsers.user_project_id
        // JOIN BeaverUsers
        // ON ProjectUsers.user_id = BeaverUsers.user_id
        // WHERE Comments.task_id = taskID;
        return create.select(
                        COMMENTS.COMMENT_ID,
                        COMMENTS.COMMENT_BODY,
                        BEAVERUSERS.USERNAME,
                        COMMENTS.USER_ID,
                        COMMENTS.COMMENT_CREATED)
                .from(COMMENTS)
                .join(PROJECTUSERS)
                .on(COMMENTS.USER_ID.eq(PROJECTUSERS.USER_PROJECT_ID))
                .join(BEAVERUSERS)
                .on(PROJECTUSERS.USER_ID.eq(BEAVERUSERS.USER_ID))
                .where(COMMENTS.TASK_ID.eq(taskID))
                .orderBy(COMMENTS.COMMENT_CREATED.asc())
                .fetchInto(CommentInTaskDto.class);
    }
}
