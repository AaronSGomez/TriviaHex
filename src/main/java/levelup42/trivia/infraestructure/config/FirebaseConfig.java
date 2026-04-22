package levelup42.trivia.infraestructure.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Initializes Firebase Admin SDK for token verification and other Firebase operations.
 * Requires GOOGLE_APPLICATION_CREDENTIALS environment variable pointing to service account JSON.
 */
@Configuration
public class FirebaseConfig {

    private static final Logger log = LoggerFactory.getLogger(FirebaseConfig.class);

    @Value("${firebase.credentials-path:}")
    private String credentialsPath;

    public FirebaseConfig() {
        try {
            // Check if Firebase is already initialized
            if (FirebaseApp.getApps().isEmpty()) {
                initializeFirebase();
            }
        } catch (Exception e) {
            log.warn("firebase_initialization_failed: {}", e.getMessage());
        }
    }

    private void initializeFirebase() throws IOException {
        String credPath = credentialsPath;

        // If not configured, try environment variable (Docker sets this)
        if (credPath == null || credPath.isBlank()) {
            credPath = System.getenv("GOOGLE_APPLICATION_CREDENTIALS");
        }

        if (credPath == null || credPath.isBlank()) {
            log.warn("Firebase credentials not configured. Set GOOGLE_APPLICATION_CREDENTIALS or firebase.credentials-path");
            return;
        }

        Path path = Path.of(credPath);
        if (Files.isDirectory(path)) {
            Path candidate = path.resolve("firebase.json");
            if (Files.isRegularFile(candidate)) {
                path = candidate;
                credPath = candidate.toString();
                log.warn("firebase_credentials_path_was_directory using_file={}", credPath);
            } else {
                log.warn("firebase_credentials_path_is_directory_without_file path={} expected_file={}", path, candidate);
                return;
            }
        }

        if (!Files.isRegularFile(path)) {
            log.warn("firebase_credentials_file_not_found path={}", path);
            return;
        }

        try (FileInputStream serviceAccount = new FileInputStream(credPath)) {
            GoogleCredentials credentials = GoogleCredentials.fromStream(serviceAccount);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .build();

            FirebaseApp.initializeApp(options);
            log.info("firebase_initialized_successfully credentials_path={}", credPath);
        }
    }
}
