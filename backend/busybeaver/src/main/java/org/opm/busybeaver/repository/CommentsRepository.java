package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Comments.CommentInTaskDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

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
