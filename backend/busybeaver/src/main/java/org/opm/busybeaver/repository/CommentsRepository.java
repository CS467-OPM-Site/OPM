package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.jooq.Record3;
import org.jooq.Result;
import org.opm.busybeaver.dto.Comments.CommentInTaskDto;
import org.opm.busybeaver.dto.Comments.NewCommentBodyDto;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

import static org.opm.busybeaver.jooq.Tables.*;

@Repository
@Component
public class CommentsRepository {

    private final DSLContext create;

    @Autowired
    public CommentsRepository(DSLContext dslContext) {
        this.create = dslContext;
    }

    public CommentInTaskDto addComment(int taskID, NewCommentBodyDto newCommentBodyDto, BeaverusersRecord commenter) {
        Record3<Integer, String, LocalDateTime> newComment = create.insertInto(COMMENTS, COMMENTS.USER_ID, COMMENTS.TASK_ID, COMMENTS.COMMENT_BODY)
                .values(commenter.getUserId(), taskID, newCommentBodyDto.commentBody())
                .returningResult(COMMENTS.COMMENT_ID, COMMENTS.COMMENT_BODY, COMMENTS.COMMENT_CREATED)
                .fetchSingle();

        return new CommentInTaskDto(
                newComment.getValue(COMMENTS.COMMENT_ID),
                newComment.getValue(COMMENTS.COMMENT_BODY),
                commenter.getUsername(),
                commenter.getUserId(),
                newComment.getValue(COMMENTS.COMMENT_CREATED));
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
