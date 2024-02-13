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
}
