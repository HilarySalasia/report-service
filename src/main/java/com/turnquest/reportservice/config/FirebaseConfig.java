package com.turnquest.reportservice.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

@Configuration
public class FirebaseConfig {

    /**
     * Initializes the FirebaseApp instance using service account credentials.
     *
     * @return A FirebaseApp instance initialized with the specified credentials and storage bucket.
     * @throws IOException If there is an error reading the service account key file.
     */
    @Bean
    public FirebaseApp initializeFirebaseApp() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/serviceAccountKey.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setStorageBucket("first-file-firebase.appspot.com")
                .build();

        return FirebaseApp.initializeApp(options);
    }

    /**
     * Configures and provides a Storage instance for interacting with Firebase Storage.
     *
     * @return A Storage instance configured with the specified credentials.
     * @throws IOException If there is an error reading the service account key file.
     */
    @Bean
    public Storage storage() throws IOException {
        FileInputStream serviceAccount =
                new FileInputStream("src/main/resources/serviceAccountKey.json");
        StorageOptions storageOptions = StorageOptions.newBuilder()
                .setCredentials(ServiceAccountCredentials.fromStream(serviceAccount))
                .build();
        return storageOptions.getService();
    }
}