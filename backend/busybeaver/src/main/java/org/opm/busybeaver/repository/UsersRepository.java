package org.opm.busybeaver.repository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.opm.busybeaver.dto.Users.UserDto;
import org.opm.busybeaver.enums.BusyBeavConstants;
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
@Slf4j
public class UsersRepository {

    private final DSLContext create;
    private static final String RID = BusyBeavConstants.REQUEST_ID.getValue();

    @Autowired
    public UsersRepository(DSLContext dslContext) {
        this.create = dslContext;
    }

    public BeaverusersRecord getUserByEmailAndId(@NotNull UserDto userDto, HttpServletRequest request)
            throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverUser = create
                .selectFrom(BEAVERUSERS)
                .where(BEAVERUSERS.EMAIL.eq(userDto.getEmail()))
                .and(BEAVERUSERS.FIREBASE_ID.eq(userDto.getFirebase_id()))
                .fetchOne();

        if (beaverUser == null) {
            UsersExceptions.UserDoesNotExistException userDoesNotExistException =
                    new UsersExceptions.UserDoesNotExistException(ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    userDoesNotExistException);

            throw userDoesNotExistException;
        }

        return beaverUser;
    }

    public BeaverusersRecord getUserByUsername(String username, HttpServletRequest request)
            throws UsersExceptions.UserDoesNotExistException {
        BeaverusersRecord beaverUser = create
                .selectFrom(BEAVERUSERS)
                .where(BEAVERUSERS.USERNAME.eq(username))
                .fetchOne();

        if (beaverUser == null) {
            UsersExceptions.UserDoesNotExistException userDoesNotExistException =
                    new UsersExceptions.UserDoesNotExistException(ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.USER_DOES_NOT_EXIST.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    userDoesNotExistException);

            throw userDoesNotExistException;
        }

        return beaverUser;
    }

    public String registerUser(@NotNull UserDto userDto, HttpServletRequest request)
            throws UsersExceptions.UserAlreadyExistsException {
        try {
            return create.insertInto(BEAVERUSERS, BEAVERUSERS.EMAIL, BEAVERUSERS.FIREBASE_ID, BEAVERUSERS.USERNAME)
                    .values(userDto.getEmail(), userDto.getFirebase_id(), userDto.getUsername())
                    .returningResult(BEAVERUSERS.USERNAME)
                    .fetchSingle().component1();

        } catch(DuplicateKeyException e) {
            // User with those details already exists
            UsersExceptions.UserAlreadyExistsException userAlreadyExistsException =
                    new UsersExceptions.UserAlreadyExistsException(ErrorMessageConstants.USER_ALREADY_EXISTS.getValue());

            log.error("{}. | RID: {} {}",
                    ErrorMessageConstants.USER_ALREADY_EXISTS.getValue(),
                    request.getAttribute(RID),
                    System.lineSeparator(),
                    userAlreadyExistsException);

            throw userAlreadyExistsException;
        }
    }
}
