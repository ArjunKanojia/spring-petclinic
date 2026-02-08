package org.springframework.samples.petclinic.featureflag.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.samples.petclinic.featureflag.annotation.FeatureToggle;
import org.springframework.samples.petclinic.featureflag.dto.EvaluationContext;
import org.springframework.samples.petclinic.featureflag.exception.FeatureDisabledException;
import org.springframework.samples.petclinic.featureflag.service.FeatureFlagEvaluator;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class FeatureToggleAspect {

 private final FeatureFlagEvaluator evaluator;

 public FeatureToggleAspect(FeatureFlagEvaluator evaluator) {
     this.evaluator = evaluator;
 }

 @Around("@annotation(featureToggle)")
 public Object checkFeature(ProceedingJoinPoint joinPoint, FeatureToggle featureToggle) throws Throwable {
     // Extract context from arguments - assuming first arg is ownerId or similar
     EvaluationContext context = null;
     Object[] args = joinPoint.getArgs();
     if (args.length > 0 && args[0] instanceof Integer ownerId) {
         context = new EvaluationContext("owner-" + ownerId);
     }

     if (evaluator.isEnabled(featureToggle.value(), context)) {
         return joinPoint.proceed();
     } else {
         if (featureToggle.fallbackEnabled()) {
             return joinPoint.proceed();  // or custom fallback
         } else {
             throw new FeatureDisabledException("Feature " + featureToggle.value() + " is disabled");
         }
     }
 }
}