package org.opm.busybeaver;

import org.junit.jupiter.api.Test;
import org.opm.busybeaver.config.FirebaseConfig;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest
class BusybeaverApplicationTests {

    // Need to mock this due to System properties call in FirebaseConfig.java not loading
    @MockBean
    private FirebaseConfig firebaseConfig;

    @Test
    void contextLoads() {
    }

}
