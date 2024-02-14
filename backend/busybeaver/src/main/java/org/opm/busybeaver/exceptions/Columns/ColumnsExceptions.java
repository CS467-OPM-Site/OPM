package org.opm.busybeaver.exceptions.Columns;

public final class ColumnsExceptions {

    public static class ColumnDoesNotExistInProject extends RuntimeException {
        public ColumnDoesNotExistInProject(String errorMessage) {
            super(errorMessage);
        }
    }
}
