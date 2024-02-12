package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.ErrorMessageConstants;
import org.opm.busybeaver.exceptions.Users.UsersExceptions;
import org.opm.busybeaver.jooq.tables.records.BeaverusersRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import static org.opm.busybeaver.jooq.tables.Beaverusers.BEAVERUSERS;

@Repository
@Component
public class UserRepository {

    private final DSLContext create;

    @Autowired
    public UserRepository(DSLContext dslContext) {
        this.create = dslContext;
    }

    public BeaverusersRecord getUserByEmailAndId(UserDto userDto) {
        return create
                .selectFrom(BEAVERUSERS)
                .where(BEAVERUSERS.EMAIL.eq(userDto.getEmail()))
                .and(BEAVERUSERS.FIREBASE_ID.eq(userDto.getFirebase_id()))
                .fetchOne();
    }

    public BeaverusersRecord getUserByUsername(String username) {
        return create
                .selectFrom(BEAVERUSERS)
                .where(BEAVERUSERS.USERNAME.eq(username))
                .fetchOne();
    }

    public String registerUser(UserDto userDto) throws UsersExceptions.UserAlreadyExistsException {
        try {
            return create.insertInto(BEAVERUSERS, BEAVERUSERS.EMAIL, BEAVERUSERS.FIREBASE_ID, BEAVERUSERS.USERNAME)
                    .values(userDto.getEmail(), userDto.getFirebase_id(), userDto.getUsername())
                    .returningResult(BEAVERUSERS.USERNAME)
                    .fetchSingle().component1();

        } catch(DuplicateKeyException e) {
            // User with those details already exists
            throw new UsersExceptions.UserAlreadyExistsException(ErrorMessageConstants.USER_ALREADY_EXISTS.getValue());
        }
    }

    public BeaverusersRecord verifyUserExistsAndReturn(UserDto userDto) throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = getUserByEmailAndId(userDto);
        if (beaverusersRecord == null) {
            throw new UsersExceptions.UserDoesNotExistException(ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue());
        }
        return beaverusersRecord;
    }

    public BeaverusersRecord verifyUserExistsAndReturn(String username) throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverusersRecord = getUserByUsername(username);
        if (beaverusersRecord == null) {
            throw new UsersExceptions.UserDoesNotExistException(ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue());
        }
        return beaverusersRecord;
    }
}
