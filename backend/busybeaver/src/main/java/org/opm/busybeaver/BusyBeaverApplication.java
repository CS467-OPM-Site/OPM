package org.opm.busybeaver;

import io.github.cdimascio.dotenv.Dotenv;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BusyBeaverApplication {

    public static void main(String[] args) {
        Dotenv.configure()
            .directory(BusyBeavConstants.RESOURCES_DIR.getValue())
            .systemProperties()
            .load();

        SpringApplication.run(BusyBeaverApplication.class, args);
    }

}
