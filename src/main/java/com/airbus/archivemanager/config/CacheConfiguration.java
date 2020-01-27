package com.airbus.archivemanager.config;

import io.github.jhipster.config.JHipsterProperties;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@EnableCaching
public class CacheConfiguration {

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache = jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(CacheConfigurationBuilder
            .newCacheConfigurationBuilder(Object.class, Object.class,
                ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
            .withExpiry(
                ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
            .build());
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            createCache(cm, com.airbus.archivemanager.config.Constants.USERS_BY_LOGIN_CACHE);
            createCache(cm, com.airbus.archivemanager.config.Constants.USERS_BY_EMAIL_CACHE);
            createCache(cm, com.airbus.archivemanager.domain.User.class.getName());
            createCache(cm, com.airbus.archivemanager.domain.Authority.class.getName());
            createCache(cm, com.airbus.archivemanager.domain.User.class.getName() + ".authorities");
            createCache(cm, com.airbus.archivemanager.domain.ScenarioFile.class.getName());
            createCache(cm, com.airbus.archivemanager.domain.ScenarioFile.class.getName() + ".scenarios");
            createCache(cm, com.airbus.archivemanager.domain.OutputFile.class.getName());
            createCache(cm, com.airbus.archivemanager.domain.Scenario.class.getName());
            createCache(cm, com.airbus.archivemanager.domain.Scenario.class.getName() + ".runs");
            createCache(cm, com.airbus.archivemanager.domain.Scenario.class.getName() + ".scenarioFiles");
            createCache(cm, com.airbus.archivemanager.domain.Run.class.getName());
            createCache(cm, com.airbus.archivemanager.domain.Run.class.getName() + ".outputFiles");
            createCache(cm, com.airbus.archivemanager.domain.Run.class.getName() + ".toolVersions");
            createCache(cm, com.airbus.archivemanager.domain.ToolVersion.class.getName());
            createCache(cm, com.airbus.archivemanager.domain.TransferArchive.class.getName());
            createCache(cm, com.airbus.archivemanager.domain.TransferArchive.class.getName() + ".outputFiles");
            createCache(cm, com.airbus.archivemanager.domain.TransferArchive.class.getName() + ".configFiles");
            createCache(cm, com.airbus.archivemanager.domain.DataSet.class.getName());
            createCache(cm, com.airbus.archivemanager.domain.DataSet.class.getName() + ".inputFiles");
            createCache(cm, com.airbus.archivemanager.domain.ConfigDataSet.class.getName());
            createCache(cm, com.airbus.archivemanager.domain.ConfigDataSet.class.getName() + ".configFiles");
            createCache(cm, com.airbus.archivemanager.domain.UserPreferences.class.getName());
            // jhipster-needle-ehcache-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cm.destroyCache(cacheName);
        }
        cm.createCache(cacheName, jcacheConfiguration);
    }
}
