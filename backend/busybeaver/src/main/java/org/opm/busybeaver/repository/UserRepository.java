package org.opm.busybeaver.repository;

import org.jooq.DSLContext;
import org.opm.busybeaver.dto.UserDto;
import org.opm.busybeaver.dto.UserRegisterDto;
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

    public boolean registerUser(UserRegisterDto userRegisterDto) {
        try {
            create.insertInto(BEAVERUSERS)
                    .set(BEAVERUSERS.EMAIL, userRegisterDto.getEmail())
                    .set(BEAVERUSERS.FIREBASE_ID, userRegisterDto.getFirebase_id())
                    .set(BEAVERUSERS.USERNAME, userRegisterDto.getUsername())
                    .execute();
            return true;
        } catch(DuplicateKeyException e) {
            // User with those details already exists
            return false;
        }

    }


}
