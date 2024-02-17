package org.opm.busybeaver.service;

import org.opm.busybeaver.dto.Users.AuthenticatedUser;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.SuccessMessageConstants;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.opm.busybeaver.repository.UsersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UsersService {

    private final UsersRepository usersRepository;

    @Autowired
    public UsersService(UsersRepository usersRepository) {
        this.usersRepository = usersRepository;
    }

    public AuthenticatedUser getUserByEmailAndId(UserDto userDto) {
        BeaverusersRecord beaverusersRecord = usersRepository.getUserByEmailAndId(userDto);

        return new AuthenticatedUser(
                beaverusersRecord.getUsername(),
                SuccessMessageConstants.SUCCESS.getValue()
        );
    }

    public AuthenticatedUser registerUser(UserDto userDto) {
        String newUsername = usersRepository.registerUser(userDto);

        return new AuthenticatedUser(
                newUsername,
                SuccessMessageConstants.SUCCESS.getValue()
        );
    }
}
