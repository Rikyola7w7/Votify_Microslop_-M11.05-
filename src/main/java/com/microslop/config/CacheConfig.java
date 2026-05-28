package com.microslop.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.github.benmanes.caffeine.cache.Caffeine;
import java.util.concurrent.TimeUnit;

@Configuration
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager(
            "categories",
            "categoriesAll",
            "categoriesByCompetition",
            "competitions",
            "competitionsAll",
            "projects",
            "projectsAll",
            "projectsByCompetition",
            "projectsByCompetitionAndCategory",
            "rankings",
            "votes",
            "certificateTypes",
            "rankingTypes"
        );
        cacheManager.setCaffeine(Caffeine.newBuilder()
            .maximumSize(500)
            .expireAfterWrite(10, TimeUnit.MINUTES)
            .recordStats());
        return cacheManager;
    }
}