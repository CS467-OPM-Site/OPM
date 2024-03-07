package org.opm.busybeaver.dto.Comments;

import org.opm.busybeaver.enums.BusyBeavPaths;

import java.beans.ConstructorProperties;
import java.time.OffsetDateTime;

public final class CommentInTaskDto {
    private final int commentID;
    private final String commentBody;
    private final String commenterUsername;
    private final int commenterID;
    private final boolean isCommenter;
    private final OffsetDateTime commentedAt;
    private String commentLocation;

    @ConstructorProperties({"comment_id", "comment_body", "username", "user_id", "comment_created", "is_commenter"})
    public CommentInTaskDto(
            int commentID, String commentBody, String commenter,
            int commenterID, OffsetDateTime commentedAt, boolean isCommenter) {
        this.commentID = commentID;
        this.commentBody = commentBody;
        this.commenterUsername = commenter;
        this.commenterID  = commenterID;
        this.commentedAt = commentedAt;
        this.isCommenter = isCommenter;
    }

    public void setCommentLocation(String contextPath, int projectID, int taskID) {
        final String PATH = contextPath + BusyBeavPaths.V1.getValue();

        this.commentLocation = PATH +
                BusyBeavPaths.PROJECTS.getValue() +
                "/" + projectID + BusyBeavPaths.TASKS.getValue() + "/" + taskID +
                BusyBeavPaths.COMMENTS.getValue() + "/" + getCommentID();
    }

    public int getCommentID() {
        return commentID;
    }

    public String getCommentBody() {
        return commentBody;
    }

    public String getCommenterUsername() {
        return commenterUsername;
    }

    public int getCommenterID() {
        return commenterID;
    }

    public boolean getIsCommenter() {
        return isCommenter;
    }

    public OffsetDateTime getCommentedAt() {
        return commentedAt;
    }

    public String getCommentLocation() {
        return commentLocation;
    }
}
