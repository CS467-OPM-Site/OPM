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
public class UsersRepository {

    private final DSLContext create;

    @Autowired
    public UsersRepository(DSLContext dslContext) {
        this.create = dslContext;
    }

    public BeaverusersRecord getUserByEmailAndId(UserDto userDto) throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverUser = create
                .selectFrom(BEAVERUSERS)
                .where(BEAVERUSERS.EMAIL.eq(userDto.getEmail()))
                .and(BEAVERUSERS.FIREBASE_ID.eq(userDto.getFirebase_id()))
                .fetchOne();

        if (beaverUser == null) {
            throw new UsersExceptions.UserDoesNotExistException(ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue());
        }

        return beaverUser;
    }

    public BeaverusersRecord getUserByUsername(String username) throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverUser = create
                .selectFrom(BEAVERUSERS)
                .where(BEAVERUSERS.USERNAME.eq(username))
                .fetchOne();

        if (beaverUser == null) {
            throw new UsersExceptions.UserDoesNotExistException(ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue());
        }

        return beaverUser;
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
}
