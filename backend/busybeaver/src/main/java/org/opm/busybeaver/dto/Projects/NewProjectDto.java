package org.opm.busybeaver.dto.Projects;

import jakarta.validation.constraints.*;
import org.opm.busybeaver.enums.BusyBeavPaths;


public final class NewProjectDto {

    @NotBlank(message = "Missing 'projectName' attribute to generate a new project")
    @Size(min = 3, max = 50, message = "Project name must be between 3 and 50 characters")
    private final String projectName;

    @NotBlank(message = "Missing 'teamName' attribute to generate a new project")
    private final String teamName;

    @NotNull(message = "Missing 'teamID' attribute to generate a new project")
    @Min(value = 1, message = "teamID must be a positive non-zero integer ID of the associated team")
    private final Integer teamID;

    private Integer projectID;
    private String projectLocation;

    public NewProjectDto(String projectName, String teamName, Integer teamID) {
            this.projectName = projectName;
            this.teamName = teamName;
            this.teamID = teamID;
    }

    public String getTeamName() {
                              return teamName;
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

    public int getTeamID() {
                         return teamID;
        }
}
