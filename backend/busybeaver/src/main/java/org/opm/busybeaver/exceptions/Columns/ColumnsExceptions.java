package org.opm.busybeaver.exceptions.Columns;

public final class ColumnsExceptions {

    public static class ColumnDoesNotExistInProject extends RuntimeException {
        public ColumnDoesNotExistInProject(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class ColumnTitleAlreadyInProject extends RuntimeException {
        public ColumnTitleAlreadyInProject(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class ColumnStillContainsTasks extends RuntimeException {
        public ColumnStillContainsTasks(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class ColumnIndexIdentical extends RuntimeException {
        public ColumnIndexIdentical(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class ColumnIndexOutOfBounds extends RuntimeException {
        public ColumnIndexOutOfBounds(String errorMessage) {
            super(errorMessage);
        }
    }

    public static class ColumnTitleIdentical extends RuntimeException {
        public ColumnTitleIdentical(String errorMessage) {
            super(errorMessage);
        }
    }
}
