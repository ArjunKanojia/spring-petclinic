package org.springframework.samples.petclinic.featureflag.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.samples.petclinic.featureflag.entity.FeatureFlag;

import java.util.Optional;

public interface FeatureFlagRepository extends JpaRepository<FeatureFlag, Long> {
    Optional<FeatureFlag> findByName(String name);

    void deleteByName(String name);
}
