package levelup42.trivia.infraestructure.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Emergency database fixer to handle schema mismatches that Flyway might skip
 * if the schema history is inconsistent.
 */
@Configuration
public class DatabaseFixer {

    private static final Logger log = LoggerFactory.getLogger(DatabaseFixer.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void fixDatabaseSchema() {
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        log.info("!!! STARTING CRITICAL DATABASE SCHEMA REPAIR (REPLACE STRATEGY) !!!");
        log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        try {
            // 1. Check if we need to fix session_type
            // We check if it exists as smallint (int2 in postgres)
            Integer isSmallInt = jdbcTemplate.queryForObject(
                "SELECT count(*) FROM information_schema.columns WHERE table_name = 'gamesession' AND column_name = 'session_type' AND data_type IN ('smallint', 'integer', 'int2', 'int4')",
                Integer.class
            );

            if (isSmallInt != null && isSmallInt > 0) {
                log.info("Detected session_type as numeric. Starting replacement strategy...");
                
                // Rename old column
                log.info("Step 1: Renaming session_type to session_type_old...");
                jdbcTemplate.execute("ALTER TABLE gamesession RENAME COLUMN session_type TO session_type_old");
                
                // Create new column
                log.info("Step 2: Adding new session_type column as varchar(20)...");
                jdbcTemplate.execute("ALTER TABLE gamesession ADD COLUMN session_type varchar(20) DEFAULT 'NORMAL' NOT NULL");
                
                // Migrate data
                log.info("Step 3: Migrating data from old column to new column...");
                jdbcTemplate.execute("UPDATE gamesession SET session_type = CASE " +
                                     "WHEN session_type_old::text = '1' THEN 'REVIEW' " +
                                     "ELSE 'NORMAL' END");
                
                // Drop old column
                log.info("Step 4: Dropping session_type_old column...");
                jdbcTemplate.execute("ALTER TABLE gamesession DROP COLUMN session_type_old");
                
                log.info("SUCCESS: session_type replacement completed.");
            } else {
                log.info("session_type already seems to be in the correct format (or missing). Skipping replacement.");
            }

            // 2. Fix timestamps just in case
            log.info("Ensuring timestamp columns are correct type...");
            jdbcTemplate.execute("ALTER TABLE gamesession ALTER COLUMN started_at TYPE timestamp USING started_at::timestamp");
            jdbcTemplate.execute("ALTER TABLE gamesession ALTER COLUMN finished_at TYPE timestamp USING finished_at::timestamp");

            // 3. Add review_question_count if missing
            log.info("Adding review_question_count column if missing...");
            jdbcTemplate.execute("ALTER TABLE gamesession ADD COLUMN IF NOT EXISTS review_question_count integer");

            log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
            log.info("!!! CRITICAL DATABASE SCHEMA REPAIR FINISHED SUCCESSFULLY !!!");
            log.info("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        } catch (Exception e) {
            log.error("CRITICAL ERROR during database repair: {}", e.getMessage(), e);
        }
    }
}

