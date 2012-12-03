package pl.kbaranski.ldappasschange;

import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

public class UserPasswordContainerValidator implements Validator {

    public boolean supports(Class<?> clazz) {
        return UserPasswordContainer.class.isAssignableFrom(clazz);
    }

    public void validate(Object target, Errors errors) {
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "username", "required.username",
                "Field username is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "oldPassword", "required.oldPassword",
                "Field oldPassword is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "required.password",
                "Field password is required.");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "passwordConfirm", "required.passwordConfirm",
                "Field passwordConfirm is required.");

        UserPasswordContainer cust = (UserPasswordContainer) target;

        if (!(cust.getPassword().equals(cust.getPasswordConfirm()))) {
            errors.rejectValue("passwordConfirm", "notmatch");
        }
    }
}
