package org.opm.busybeaver.enums;

import org.springframework.util.ObjectUtils;

import java.util.Map;

public enum TaskFields {
    title, description, dueDate, assignedTo, priority, sprintID, customFields;

    public enum priorityFields {
        None, Low, High, Medium;

        public static boolean isPriorityFieldValid(String priority) {
            return ObjectUtils.containsConstant(priorityFields.values(), priority, true);
        }
    }

    public static boolean areTaskFieldsValid(Map<String, Object> fieldsToCheck) {
        for (String key : fieldsToCheck.keySet()) {
            boolean containsValidField = ObjectUtils.containsConstant(TaskFields.values(), key, true);

            if (!containsValidField) return false;
        }
        return true;
    }
}
