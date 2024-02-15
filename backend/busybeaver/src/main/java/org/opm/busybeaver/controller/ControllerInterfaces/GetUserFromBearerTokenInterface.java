package org.opm.busybeaver.controller.ControllerInterfaces;

import jakarta.servlet.http.HttpServletRequest;
import org.opm.busybeaver.dto.Users.UserDto;

public interface GetUserFromBearerTokenInterface {
    public UserDto getUserFromToken(HttpServletRequest request);
}
