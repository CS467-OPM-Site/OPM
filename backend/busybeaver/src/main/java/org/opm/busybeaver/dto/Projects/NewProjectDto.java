package org.opm.busybeaver.dto.Projects;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import org.opm.busybeaver.enums.BusyBeavPaths;


public final class NewProjectDto {

        @NotBlank(message = "Missing 'projectName' attribute to generate a new project")
        @Size(min = 3, max = 50, message = "Project name must be between 3 and 50 characters")
        private String projectName;

        @NotBlank(message = "Missing 'teamName' attribute to generate a new project")
        private String teamName;

        // Bean validation cannot seem to handle confirming a positive whole integer number above 0
        @NotBlank
        @Pattern(regexp = "^[0-9]*[1-9]+[0-9]*$", message = "teamID must be a positive integer")
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        private String teamID;

        @JsonProperty("teamID")
        private int teamIDInt;
        private int projectID;
        private String projectLocation;

        public String getTeamName() {
                return teamName;
        }

        public void setTeamName(String teamName) {
                this.teamName = teamName;
        }

        public String getProjectLocation() {
                return projectLocation;
        }

        public void setProjectLocation(String contextPath) {
                final String PATH = contextPath + BusyBeavPaths.V1.getValue();

                this.projectLocation = PATH +
                        BusyBeavPaths.PROJECTS.getValue() +
                        "/" + getProjectID();
        }

        public String getProjectName() {
                return projectName;
        }

        public int getProjectID() {
                return projectID;
        }

        public void setProjectID(int projectID) {
                this.projectID = projectID;
        }

        public int getTeamIDInt() {
                return teamIDInt;
        }

        public void setTeamIDInt(int teamIDInt) {
                this.teamIDInt = teamIDInt;
                resetTeamId();
        }

        private void resetTeamId() {
                this.teamID = String.valueOf(teamIDInt);
        }

        public String getTeamID() {
                return teamID;
        }
}
