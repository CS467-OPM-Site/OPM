package org.opm.busybeaver.utils;

import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.service.UserDoesNotExistException;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.UserRepository;
import org.opm.busybeaver.service.FirebaseAuthenticationService;

public final class Utils {

    public static UserDto parseToken(FirebaseAuthenticationService firebaseAuthenticationService) {
        return new UserDto(
                firebaseAuthenticationService.getEmail(),
                firebaseAuthenticationService.getUid()
        );
    }

    public static BeaverusersRecord verifyUserExistsAndReturn(UserDto userDto, UserRepository userRepository) {
        BeaverusersRecord beaverusersRecord = userRepository.getUserByEmailAndId(userDto);
        if (beaverusersRecord == null) {
            throw new UserDoesNotExistException(ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue());
        }
        return beaverusersRecord;
    }

}
