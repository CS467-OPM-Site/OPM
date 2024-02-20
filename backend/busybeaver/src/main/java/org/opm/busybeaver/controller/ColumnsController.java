package org.opm.busybeaver.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.controller.ControllerInterfaces.GetUserFromBearerTokenInterface;
import org.opm.busybeaver.dto.Columns.NewColumnDto;
import org.opm.busybeaver.dto.Columns.NewColumnIndexDto;
import org.opm.busybeaver.dto.Columns.NewColumnTitleDto;
import org.opm.busybeaver.dto.SmallJsonResponse;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.BusyBeavPaths;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.service.ColumnsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@ApiPrefixController
@RestController
@CrossOrigin
@Slf4j
public final class ColumnsController implements GetUserFromBearerTokenInterface {
    private final ColumnsService columnsService;
    private static final String PROJECTS_PATH = BusyBeavPaths.Constants.PROJECTS;
    private static final String COLUMNS_PATH = BusyBeavPaths.Constants.COLUMNS;
    private static final String ORDER_PATH = BusyBeavPaths.Constants.ORDER;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public ColumnsController(ColumnsService columnsService) { this.columnsService = columnsService; }

    @PostMapping(PROJECTS_PATH + "/{projectID}" + COLUMNS_PATH)
    public @NotNull NewColumnDto addColumnToProject(
            @NotNull HttpServletRequest request,
            @Valid @RequestBody NewColumnDto newColumnDto,
            @PathVariable int projectID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto,
            @NotNull HttpServletResponse response
    ) {
        NewColumnDto newColumn = columnsService.addNewColumn(userDto, newColumnDto, projectID, request.getContextPath());
        response.setHeader(BusyBeavConstants.LOCATION.getValue(), newColumn.getColumnLocation());
        response.setStatus(HttpStatus.CREATED.value());

        log.info("New column added to project. | RID: {}", request.getAttribute(RID));

        return newColumn;
    }

    @Contract("_, _, _, _ -> new")
    @DeleteMapping(PROJECTS_PATH + "/{projectID}" + COLUMNS_PATH + "/{columnID}")
    public @NotNull SmallJsonResponse deleteColumnFromProject(
            @NotNull HttpServletRequest request,
            @PathVariable int projectID,
            @PathVariable int columnID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        columnsService.deleteColumn(userDto, projectID, columnID);
        log.info("Column removed from project. | RID: {}", request.getAttribute(RID));

        return new SmallJsonResponse(
                HttpStatus.OK.value(),
                SuccessMessageConstants.COLUMN_DELETED.getValue()
        );
    }

    @PutMapping(PROJECTS_PATH + "/{projectID}" + COLUMNS_PATH + "/{columnID}" + ORDER_PATH)
    public NewColumnDto moveColumnInProject(
            @NotNull HttpServletRequest request,
            @Valid @RequestBody @NotNull NewColumnIndexDto newColumnIndexDto,
            @PathVariable int projectID,
            @PathVariable int columnID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        NewColumnDto newColumnDto = columnsService.moveColumn(
                userDto,
                projectID,
                columnID,
                newColumnIndexDto.columnIndex(),
                request.getContextPath()
        );

        log.info("Column moved within project. | RID: {}", request.getAttribute(RID));

        return newColumnDto;
    }

    @PutMapping(PROJECTS_PATH + "/{projectID}" + COLUMNS_PATH + "/{columnID}")
    public NewColumnDto changeColumnTitle(
            @NotNull HttpServletRequest request,
            @Valid @RequestBody NewColumnTitleDto newColumnTitleDto,
            @PathVariable int projectID,
            @PathVariable int columnID,
            @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL) UserDto userDto
    ) {
        NewColumnDto newColumnDto = columnsService.changeColumnTitle(
                userDto,
                projectID,
                columnID,
                newColumnTitleDto,
                request.getContextPath()
        );

        log.info("Modified column title to {}. | RID: {}", newColumnTitleDto.columnTitle(), request.getAttribute(RID));

        return newColumnDto;
    }

    @ModelAttribute(BusyBeavConstants.Constants.USER_KEY_VAL)
    @Override
    public UserDto getUserFromToken(@NotNull HttpServletRequest request) {
        return (UserDto) request.getAttribute(BusyBeavConstants.USER_KEY_VAL.getValue());
    }
}
