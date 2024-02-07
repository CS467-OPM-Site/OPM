package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.AuthenticatedUser;
import org.opm.busybeaver.dto.UserDto;
import org.opm.busybeaver.dto.UserRegisterDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.service.UserAlreadyExistsException;
import org.opm.busybeaver.exceptions.service.UserDoesNotExistException;
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

    public AuthenticatedUser getUserByEmailAndId(UserDto userDto) throws UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = userRepository.getUserByEmailAndId(userDto);

        if (beaverusersRecord == null) {
            throw new UserDoesNotExistException(ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue());
        }

        return new AuthenticatedUser(
                beaverusersRecord.getUsername(),
                BusyBeavConstants.SUCCESS.getValue()
        );
    }

    public void registerUser(UserRegisterDto userRegisterDto) throws UserAlreadyExistsException {
        boolean validNewUser = userRepository.registerUser(userRegisterDto);
        if (!validNewUser) {
            throw new UserAlreadyExistsException(ErrorMessageConstants.USER_ALREADY_EXISTS.getValue());
        }

    }
}
