package org.springframework.samples.petclinic.featureflag.controller;



import org.springframework.http.HttpStatus;
import org.springframework.samples.petclinic.featureflag.entity.FeatureFlag;
import org.springframework.samples.petclinic.featureflag.service.FeatureFlagService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import com.fasterxml.jackson.core.JsonProcessingException;

import java.util.List;

@RestController
@RequestMapping("/api/feature-flags")
public class FeatureFlagController {

 private final FeatureFlagService service;

 public FeatureFlagController(FeatureFlagService service) {
     this.service = service;
 }

 @GetMapping
 public List<FeatureFlag> getAll() {
     return service.findAll();
 }

 @GetMapping("/{name}")
 public FeatureFlag getByName(@PathVariable String name) {
     return service.findByName(name)
             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Flag not found: " + name));
 }

 @PostMapping
 @ResponseStatus(HttpStatus.CREATED)
 public FeatureFlag create(@RequestBody FeatureFlag flag) throws JsonProcessingException {
     return service.save(flag);
 }

 @PutMapping("/{name}")
 public FeatureFlag update(@PathVariable String name, @RequestBody FeatureFlag updateRequest) throws JsonProcessingException {
     
     // Step 1: Load existing flag (must exist)
     FeatureFlag existing = service.findByName(name)
         .orElseThrow(() -> new ResponseStatusException(
             HttpStatus.NOT_FOUND, 
             "Feature flag not found with name: " + name
         ));

     // Step 2: Update only allowed fields (never trust client to send ID or name)
     existing.setType(updateRequest.getType());
     existing.setConfiguration(updateRequest.getConfiguration());
     existing.setEnabledByDefault(updateRequest.isEnabledByDefault());  // if you allow changing default

     // Step 3: Save the existing entity → this will UPDATE, not INSERT
     return service.save(existing);
 }

 @DeleteMapping("/{name}")
 @ResponseStatus(HttpStatus.NO_CONTENT)
 public void delete(@PathVariable String name) {
     service.deleteByName(name);
 }
}