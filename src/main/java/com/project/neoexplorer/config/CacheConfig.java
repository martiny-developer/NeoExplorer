package com.project.neoexplorer.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    public CacheManager cacheManager() {
        // Un caché por tipo de dato: apod, asteroides, epic y traducciones
        return new ConcurrentMapCacheManager("apod", "asteroides", "epic", "traducciones");
    }
}