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
}