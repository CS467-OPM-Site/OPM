package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.Columns.NewColumnDto;
import org.opm.busybeaver.dto.SmallJsonResponse;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.service.ColumnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ApiPrefixController
@RestController
@CrossOrigin
public final class ColumnsController implements GetUserFromBearerTokenInterface {
    private final ColumnService columnService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String COLUMNS_PATH = BusyBeavPaths.Constants.COLUMNS;

    @Autowired
    public ColumnsController(ColumnService columnService) { this.columnService = columnService; }

    @PostMapping(PROJECTS_PATH + "/{projectID}" + COLUMNS_PATH)
    public NewColumnDto addColumnToProject(
            HttpServletRequest request,
            @Valid @RequestBody NewColumnDto newColumnDto,
            @PathVariable int projectID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            HttpServletResponse response
    ) {
        NewColumnDto newColumn = columnService.addNewColumn(userDto, newColumnDto, projectID, request.getContextPath());
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), newColumn.getColumnLocation());
        response.setStatus(HttpStatus.CREATED.value());

        return newColumn;
    }

    @DeleteMapping(PROJECTS_PATH + "/{projectID}" + COLUMNS_PATH + "/{columnID}")
    public SmallJsonResponse deleteColumnFromProject(
            @PathVariable int projectID,
            @PathVariable int columnID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        columnService.deleteColumn(userDto, projectID, columnID);

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.COLUMN_DELETED.getValue()
        );
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
