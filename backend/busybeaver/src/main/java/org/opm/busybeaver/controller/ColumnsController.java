package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.Columns.NewColumnDto;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.service.ColumnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ApiPrefixController
@RestController
@CrossOrigin
public final class ColumnsController implements GetUserFromBearerTokenInterface {
    private final ColumnsService columnsService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String COLUMNS_PATH = BusyBeavPaths.Constants.COLUMNS;

    @Autowired
    public ColumnsController(ColumnsService columnsService) { this.columnsService = columnsService; }

    @PostMapping(PROJECTS_PATH + "/{projectID}" + COLUMNS_PATH)
    public NewColumnDto makeNewProject(
            HttpServletRequest request,
            @Valid @RequestBody NewColumnDto newColumnDto,
            @PathVariable int projectID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        NewColumnDto newColumn = columnsService.addNewColumn(userDto, newColumnDto, projectID, request.getContextPath());
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), newColumn.getColumnLocation());
        response.setStatus(HttpStatus.CREATED.value());

        return newColumn;
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
