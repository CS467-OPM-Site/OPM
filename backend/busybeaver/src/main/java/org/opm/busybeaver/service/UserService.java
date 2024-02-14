package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Users.AuthenticatedUser;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public AuthenticatedUser getUserByEmailAndId(UserDto userDto) throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.getUserByEmailAndId(userDto);

        if (beaverusersRecord == null) {
            throw new UsersExceptions.UserDoesNotExistException(ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue());
        }

        return new AuthenticatedUser(
                beaverusersRecord.getUsername(),
                SuccessMessageConstants.SUCCESS.getValue()
        );
    }

    public AuthenticatedUser registerUser(UserDto userDto) throws UsersExceptions.UserAlreadyExistsException {
        String newUsername = userRepository.registerUser(userDto);

        return new AuthenticatedUser(
                newUsername,
                SuccessMessageConstants.SUCCESS.getValue()
        );
    }
}
