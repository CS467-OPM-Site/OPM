package org.opm.busybeaver.exceptions.Comments;

public final class CommentsExceptions {

    public static class CommentDoesNotExistOnTask extends RuntimeException {
        public CommentDoesNotExistOnTask(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class UserDidNotLeaveThisComment extends RuntimeException {
        public UserDidNotLeaveThisComment(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class CommentBodyIdenticalNotModified extends RuntimeException {
        public CommentBodyIdenticalNotModified(String errorMessage) {
            super(errorMessage);
        }
    }
}
