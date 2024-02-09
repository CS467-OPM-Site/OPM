package org.opm.busybeaver.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.jetbrains.annotations.NotNull;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.service.FirebaseAuthenticationService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
@CrossOrigin
public class FirebaseAuthenticationController extends OncePerRequestFilter {

    private static final String BEARER = "Bearer ";
    private static final String OPTIONS = "OPTIONS";
    private static final String AUTHORIZATION = "Authorization";

    private final ObjectMapper objectMapper;

    public FirebaseAuthenticationController(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(
            @NotNull HttpServletRequest request,
            @NotNull HttpServletResponse response,
            @NotNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Can skip OPTIONS requests
        if (Objects.equals(request.getMethod(), OPTIONS)) {
            filterChain.doFilter(request, response);
            return;
        };

        // Check to make sure "Authorization" : "Bearer ${TOKEN}" is in the Header
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null || !BEARER.equals(authorizationHeader.substring(0, BEARER.length()))) {// !authorizationHeader.contains(BEARER)) {
           sendJsonMissingAuthenticationHeader(response);
           return;
        }

        // Strip out "Bearer " to get the token itself
        authorizationHeader = authorizationHeader.substring(BEARER.length());

        FirebaseAuthenticationService authentication = new FirebaseAuthenticationService(authorizationHeader);
        if (authentication.isAuthenticated()) {
            // Store authenticated user details for user in Controllers
            request.setAttribute(BusyBeavConstants.USER_KEY_VAL.getValue(), parseToken(authentication));
            filterChain.doFilter(request, response);
            return;
        }

        sendJsonMissingAuthenticationHeader(response);
    }


    private void sendJsonMissingAuthenticationHeader(HttpServletResponse response) throws IOException {
        // Handles both missing Header, and invalid Firebase Authentication

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(BusyBeavConstants.JSON_CONTENT_TYPE.getValue());
        response.setCharacterEncoding(BusyBeavConstants.UTF_8_ENCODING.getValue());

        Map<String, String> responseBody = new HashMap<>();
        responseBody.put(
                ErrorMessageConstants.ERROR_KEY_VAL.getValue(),
                ErrorMessageConstants.MISSING_INVALID_HEADER_TOKEN.getValue()
        );

        String jsonString = objectMapper.writeValueAsString(responseBody);
        PrintWriter responseWriter = response.getWriter();
        responseWriter.print(jsonString);
        responseWriter.flush();
    }

    private static UserDto parseToken(FirebaseAuthenticationService firebaseAuthenticationService) {
        return new UserDto(
                firebaseAuthenticationService.getEmail(),
                firebaseAuthenticationService.getUid()
        );
    }
}
