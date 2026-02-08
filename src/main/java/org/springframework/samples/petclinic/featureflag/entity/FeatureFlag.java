package org.springframework.samples.petclinic.featureflag.entity;


import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "feature_flags", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class FeatureFlag {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(nullable = false, unique = true)
 private String name;

 @Enumerated(EnumType.STRING)
 @Column(nullable = false)
 private FlagType type = FlagType.BOOLEAN;

 @Column(columnDefinition = "TEXT")
 private String configuration;  // "true/false", "50", or JSON array for lists

 private boolean enabledByDefault = true;  // fallback

 public enum FlagType {
     BOOLEAN,
     PERCENTAGE,
     WHITELIST,
     BLACKLIST
 }

 // Constructors
 public FeatureFlag() {}

 public FeatureFlag(String name, FlagType type, String configuration) {
     this.name = name;
     this.type = type;
     this.configuration = configuration;
 }

 // Getters and Setters
 public Long getId() {
     return id;
 }

 public void setId(Long id) {
     this.id = id;
 }

 public String getName() {
     return name;
 }

 public void setName(String name) {
     this.name = name;
 }

 public FlagType getType() {
     return type;
 }

 public void setType(FlagType type) {
     this.type = type;
 }

 public String getConfiguration() {
     return configuration;
 }

 public void setConfiguration(String configuration) {
     this.configuration = configuration;
 }

 public boolean isEnabledByDefault() {
     return enabledByDefault;
 }

 public void setEnabledByDefault(boolean enabledByDefault) {
     this.enabledByDefault = enabledByDefault;
 }

 // Equals and HashCode
 @Override
 public boolean equals(Object o) {
     if (this == o) return true;
     if (o == null || getClass() != o.getClass()) return false;
     FeatureFlag that = (FeatureFlag) o;
     return Objects.equals(name, that.name);
 }

 @Override
 public int hashCode() {
     return Objects.hash(name);
 }
}