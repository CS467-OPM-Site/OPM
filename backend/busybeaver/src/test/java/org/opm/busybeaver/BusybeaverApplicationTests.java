package org.opm.busybeaver;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.opm.busybeaver.config.FirebaseConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import javax.sql.DataSource;

@SpringBootTest
class BusybeaverApplicationTests {

    // Need to mock this due to System properties call in FirebaseConfig.java not loading
    @MockBean
    private FirebaseConfig firebaseConfig;

    @MockBean
    private DataSource dataSource;

    @Test
    void contextLoads() {
    }

}
