package org.opm.busybeaver.dto.Projects;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NewProjectNameDto (
    @NotBlank(message = "Missing 'projectName' attribute to modify project name")
    @Size(min = 3, max = 50, message = "Project names must be 3 to 50 characters")
    String projectName
) { }
