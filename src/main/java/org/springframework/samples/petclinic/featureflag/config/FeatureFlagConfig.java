package org.springframework.samples.petclinic.featureflag.config;



import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

import tools.jackson.databind.ObjectMapper;

@Configuration
@EnableAspectJAutoProxy
public class FeatureFlagConfig {

 @Bean
 public ObjectMapper objectMapper() {
     return new ObjectMapper();
 }
}