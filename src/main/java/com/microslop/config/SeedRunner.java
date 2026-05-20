package com.microslop.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.DatabasePopulatorUtils;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
public class SeedRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(SeedRunner.class);
    private final DataSource dataSource;

    public SeedRunner(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) throws Exception {
        // First, reset all PostgreSQL sequences to prevent duplicate key errors
        resetPostgresSequences();
        
        ClassPathResource resource = new ClassPathResource("neon.session.sql");
        if (!resource.exists()) {
            log.warn("neon.session.sql not found on classpath — skipping seed population");
            return;
        }

        String content;
        try {
            content = resource.getContentAsString(StandardCharsets.UTF_8).trim();
        } catch (IOException e) {
            log.warn("Could not read neon.session.sql — skipping seed population");
            return;
        }

        if (content.isEmpty()) {
            log.info("neon.session.sql is empty — skipping seed population");
            return;
        }

        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(resource);
        DatabasePopulatorUtils.execute(populator, dataSource);
        log.info("Executed neon.session.sql database populator");
    }

    /**
     * Reset PostgreSQL sequences to be higher than any existing primary key values.
     * This prevents "duplicate key" errors when inserting new records with auto-increment IDs.
     * The sequence is set to MAX(id) + 1 to ensure the next insert gets a unique ID.
     */
    private void resetPostgresSequences() {
        String[] sequences = {
            "voter_id_seq",
            "users_id_seq",
            "competition_id_seq",
            "project_id_seq",
            "category_id_seq",
            "vote_id_seq",
            "judge_id_seq",
            "project_comment_id_seq",
            "checklist_item_id_seq",
            "checklist_vote_id_seq"
        };

        String[] tables = {
            "voter",
            "users",
            "competition",
            "project",
            "category",
            "vote",
            "judge",
            "project_comment",
            "checklist_item",
            "checklist_vote"
        };

        try (java.sql.Connection connection = dataSource.getConnection()) {
            for (int i = 0; i < sequences.length; i++) {
                String sequence = sequences[i];
                String table = tables[i];
                
                try {
                    String sql = String.format(
                        "SELECT setval('%s', (SELECT COALESCE(MAX(id), 0) + 1 FROM %s), false)",
                        sequence, table
                    );
                    
                    try (java.sql.Statement stmt = connection.createStatement()) {
                        stmt.execute(sql);
                        log.debug("Reset sequence: {} for table: {}", sequence, table);
                    }
                } catch (Exception e) {
                    log.warn("Could not reset sequence {} — table {} may not exist yet", sequence, table);
                }
            }
            log.info("PostgreSQL sequences reset successfully");
        } catch (Exception e) {
            log.error("Error resetting PostgreSQL sequences", e);
        }
    }
}