package levelup42.trivia.infraestructure.security.firebase;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Verifies Firebase ID tokens from client applications.
 * Firebase tokens are JWT tokens issued by Firebase Auth, different from Google ID tokens.
 */
@Service
public class FirebaseTokenVerifierService {

    private static final Logger log = LoggerFactory.getLogger(FirebaseTokenVerifierService.class);

    public Optional<FirebaseUserProfile> verify(String idTokenString) {
        if (idTokenString == null || idTokenString.isBlank()) {
            log.debug("Firebase token is empty or null");
            return Optional.empty();
        }

        try {
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idTokenString);

            String email = decodedToken.getEmail();
            String name = decodedToken.getName();
            String uid = decodedToken.getUid();

            if (email == null || email.isBlank()) {
                log.warn("Firebase token does not contain email");
                return Optional.empty();
            }

            // Use display name from token, fallback to email prefix if not available
            if (name == null || name.isBlank()) {
                int atIndex = email.indexOf('@');
                if (atIndex > 0) {
                    name = email.substring(0, atIndex);
                } else {
                    name = "Jugador";
                }
            }

            return Optional.of(new FirebaseUserProfile(email.trim(), name.trim(), uid));
        } catch (FirebaseAuthException ex) {
            log.warn("firebase_token_verification_failed reason={}", ex.getMessage());
            return Optional.empty();
        }
    }

    public record FirebaseUserProfile(String email, String name, String uid) {
    }
}
