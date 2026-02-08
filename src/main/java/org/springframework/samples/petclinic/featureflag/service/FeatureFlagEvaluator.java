package org.springframework.samples.petclinic.featureflag.service;


import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.samples.petclinic.featureflag.dto.EvaluationContext;
import org.springframework.samples.petclinic.featureflag.entity.FeatureFlag;
import org.springframework.stereotype.Service;

import tools.jackson.databind.ObjectMapper;

@Service
public class FeatureFlagEvaluator {

 private static final Logger log = LoggerFactory.getLogger(FeatureFlagEvaluator.class);

 private final FeatureFlagService service;
 private final ObjectMapper objectMapper;

 public FeatureFlagEvaluator(FeatureFlagService service, ObjectMapper objectMapper) {
     this.service = service;
     this.objectMapper = objectMapper;
 }

 public boolean isEnabled(String flagName, EvaluationContext context) {
     try {
         FeatureFlag flag = service.findByName(flagName)
                 .orElseGet(() -> {
                     log.warn("Flag not found: {}, using default enabled", flagName);
                     FeatureFlag defaultFlag = new FeatureFlag();
                     defaultFlag.setEnabledByDefault(true);
                     return defaultFlag;
                 });

         String config = flag.getConfiguration();
         if (config == null) {
             return flag.isEnabledByDefault();
         }

         return switch (flag.getType()) {
             case BOOLEAN -> Boolean.parseBoolean(config);
             case PERCENTAGE -> evaluatePercentage(config, context);
             case WHITELIST -> evaluateList(config, context, true);
             case BLACKLIST -> evaluateList(config, context, false);
         };
     } catch (Exception e) {
         log.error("Error evaluating flag {}: {}", flagName, e.getMessage());
         return true;  // Fallback to enabled on error
     }
 }

 private boolean evaluatePercentage(String config, EvaluationContext context) {
     int percent = Integer.parseInt(config);
     if (percent <= 0) return false;
     if (percent >= 100) return true;

     // Deterministic based on userId if provided, else UUID for session
     String seed = (context != null && context.userId() != null) ? context.userId() : UUID.randomUUID().toString();
     int hash = Math.abs(seed.hashCode() % 100);
     return hash < percent;
 }

 private boolean evaluateList(String config, EvaluationContext context, boolean isWhitelist) {
     if (context == null || context.userId() == null) {
         log.warn("No context for list flag, defaulting to {}", !isWhitelist);
         return !isWhitelist;
     }

     String[] array = objectMapper.readValue(config, String[].class);
	 Set<String> listSet = new HashSet<>();
	 for (String item : array) {
	     listSet.add(item);
	 }
	 boolean contains = listSet.contains(context.userId());
	 return isWhitelist ? contains : !contains;
 }
}