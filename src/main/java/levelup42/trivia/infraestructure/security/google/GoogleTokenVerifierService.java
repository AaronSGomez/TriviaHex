package levelup42.trivia.infraestructure.security.google;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
public class GoogleTokenVerifierService {

    private static final Logger log = LoggerFactory.getLogger(GoogleTokenVerifierService.class);

    @Value("${application.security.google.allowed-audiences:}")
    private String allowedAudiencesCsv;

    public Optional<GoogleProfile> verify(String idTokenString) {
        if (idTokenString == null || idTokenString.isBlank()) {
            return Optional.empty();
        }

        try {
            GoogleIdTokenVerifier.Builder builder = new GoogleIdTokenVerifier.Builder(
                    new NetHttpTransport(),
                    GsonFactory.getDefaultInstance()
            );

            List<String> audiences = parseAudiences(allowedAudiencesCsv);
            if (!audiences.isEmpty()) {
                builder.setAudience(audiences);
            }

            GoogleIdTokenVerifier verifier = builder.build();
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                return Optional.empty();
            }

            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            Object emailVerifiedRaw = payload.get("email_verified");
            boolean emailVerified = Boolean.TRUE.equals(emailVerifiedRaw)
                    || "true".equalsIgnoreCase(String.valueOf(emailVerifiedRaw));

            String name = payload.get("name") instanceof String ? (String) payload.get("name") : null;
            if (name == null || name.isBlank()) {
                Object givenName = payload.get("given_name");
                if (givenName instanceof String) {
                    name = (String) givenName;
                }
            }

            if (email == null || email.isBlank() || !emailVerified) {
                return Optional.empty();
            }

            return Optional.of(new GoogleProfile(email.trim(), normalizeName(name, email), emailVerified));
        } catch (GeneralSecurityException | IOException ex) {
            log.warn("google_token_verification_failed reason={}", ex.getMessage());
            return Optional.empty();
        }
    }

    private static List<String> parseAudiences(String csv) {
        if (csv == null || csv.isBlank()) {
            return List.of();
        }
        return Arrays.stream(csv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    private static String normalizeName(String name, String email) {
        if (name != null && !name.isBlank()) {
            return name.trim();
        }
        int atIndex = email.indexOf('@');
        if (atIndex > 0) {
            return email.substring(0, atIndex);
        }
        return "Jugador";
    }

    public record GoogleProfile(String email, String name, boolean emailVerified) {
    }
}
