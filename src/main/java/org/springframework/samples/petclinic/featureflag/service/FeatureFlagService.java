package org.springframework.samples.petclinic.featureflag.service;


import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.samples.petclinic.featureflag.entity.FeatureFlag;
import org.springframework.samples.petclinic.featureflag.repository.FeatureFlagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import tools.jackson.databind.ObjectMapper;

@Service
public class FeatureFlagService {

 private static final Logger log = LoggerFactory.getLogger(FeatureFlagService.class);

 private final FeatureFlagRepository repository;
 private final CacheManager cacheManager;
 private final ObjectMapper objectMapper;

 public FeatureFlagService(FeatureFlagRepository repository, CacheManager cacheManager, ObjectMapper objectMapper) {
     this.repository = repository;
     this.cacheManager = cacheManager;
     this.objectMapper = objectMapper;
 }

 @Cacheable(value = "featureFlags", key = "#name", unless = "#result == null")
 public Optional<FeatureFlag> findByName(String name) {
     return repository.findByName(name);
 }

 public List<FeatureFlag> findAll() {
     return repository.findAll();
 }

 @Transactional
 public FeatureFlag save(FeatureFlag flag) throws JsonProcessingException {
     // Validate configuration based on type
     validateFlag(flag);

     // Save (will INSERT if new, UPDATE if ID exists)
     FeatureFlag saved = repository.save(flag);

     // Clear cache for this flag so next read gets fresh value
     evictCache(saved.getName());

     log.info("Saved feature flag: {} (type={}, config={})", 
              saved.getName(), saved.getType(), saved.getConfiguration());

     return saved;
 }
 @Transactional
 @CacheEvict(value = "featureFlags", key = "#name")
 public void deleteByName(String name) {
     repository.deleteByName(name);
     log.info("Deleted feature flag: {}", name);
 }

 private void evictCache(String name) {
     cacheManager.getCache("featureFlags").evictIfPresent(name);
 }

 private void validateFlag(FeatureFlag flag) throws JsonProcessingException {
     if (flag.getName() == null || flag.getName().isBlank()) {
         throw new IllegalArgumentException("Flag name is required");
     }
     try {
         switch (flag.getType()) {
             case BOOLEAN:
                 Boolean.parseBoolean(flag.getConfiguration());
                 break;
             case PERCENTAGE:
                 int pct = Integer.parseInt(flag.getConfiguration());
                 if (pct < 0 || pct > 100) {
                     throw new IllegalArgumentException("Percentage must be between 0 and 100");
                 }
                 break;
             case WHITELIST:
             case BLACKLIST:
                 objectMapper.readTree(flag.getConfiguration());
                 break;
             default:
                 throw new IllegalArgumentException("Invalid flag type");
         }
     } catch (NumberFormatException e) {
         throw new IllegalArgumentException("Invalid configuration for flag type: " + e.getMessage());
     }
 }
}
