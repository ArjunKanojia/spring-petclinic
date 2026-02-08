package org.springframework.samples.petclinic.featureflag.exception;



import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeatureDisabledException.class)
    public String handleFeatureDisabled(FeatureDisabledException ex, Model model) {
        model.addAttribute("message", ex.getMessage());  // e.g. "Feature owner_search is disabled"
        return "featureError";  // or "welcome" or "owners/findOwners"
    }
}
