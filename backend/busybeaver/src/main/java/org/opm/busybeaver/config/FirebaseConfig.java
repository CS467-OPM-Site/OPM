package org.opm.busybeaver.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import jakarta.annotation.PostConstruct;
import org.opm.busybeaver.enums.BusyBeavConstants;
import org.opm.busybeaver.enums.EnvVariables;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    @PostConstruct
    public void initialize() throws FileNotFoundException, SecurityException {
        FileInputStream configFile = new FileInputStream(
                BusyBeavConstants.RESOURCES_DIR.getValue() +
                        System.getProperty(EnvVariables.FIREBASE_ADMIN_SDK_KEY.getValue())
        );

        try {
            GoogleCredentials credentials = GoogleCredentials.fromStream(configFile);
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();
            FirebaseApp.initializeApp(options);
        } catch (IOException e) {
            System.out.print("ERROR: Unable to authenticate AdminSDK with Firebase.");
        }

    }
}
