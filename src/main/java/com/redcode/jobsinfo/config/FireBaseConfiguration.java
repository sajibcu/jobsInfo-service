package com.redcode.jobsinfo.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FireBaseConfiguration {

    @Bean
    public FirebaseAuth firebaseAuth() throws IOException {

        InputStream serviceAccount = this.getClass().getClassLoader().getResourceAsStream("firebase-serviceAccountKey.json");
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .setDatabaseUrl("https://jobsinfo-d61a8.firebaseio.com").build();

        FirebaseApp.initializeApp(options);

        return FirebaseAuth.getInstance();
    }
}
