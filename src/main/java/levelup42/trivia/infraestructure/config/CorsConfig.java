package levelup42.trivia.infraestructure.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

        private static final List<String> ALLOWED_ORIGIN_PATTERNS = List.of(
            "https://triviahex.duckdns.org",
            "https://triviahex.vercel.app",
            "http://localhost:*",
            "http://127.0.0.1:*"
    );

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
            .allowedOriginPatterns(ALLOWED_ORIGIN_PATTERNS.toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH")
                .allowedHeaders("*")
                .allowCredentials(true);
    }
}
