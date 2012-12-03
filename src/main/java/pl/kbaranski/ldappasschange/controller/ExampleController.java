package pl.kbaranski.ldappasschange.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.support.SessionStatus;

import pl.kbaranski.ldappasschange.UserPasswordContainer;
import pl.kbaranski.ldappasschange.UserPasswordContainerValidator;
import pl.kbaranski.ldappasschange.utils.LdapUtil;
import pl.kbaranski.ldappasschange.utils.ldaputil.LdapConnectException;
import pl.kbaranski.ldappasschange.utils.ldaputil.LdapException;
import pl.kbaranski.ldappasschange.utils.ldaputil.LdapNonUniqueUidException;

@Controller
@RequestMapping("/example")
public class ExampleController {

    UserPasswordContainerValidator userPasswordContainerValidator;
    LdapUtil ldapUtil;

    @Autowired
    public ExampleController(UserPasswordContainerValidator userPasswordContainerValidator, LdapUtil ldapUtil) {
        this.userPasswordContainerValidator = userPasswordContainerValidator;
        this.ldapUtil = ldapUtil;
    }

    @RequestMapping(method = RequestMethod.POST)
    public String processSubmit(@ModelAttribute("upc") UserPasswordContainer userPasswordContainer,
            BindingResult result, SessionStatus status) {
        userPasswordContainerValidator.validate(userPasswordContainer, result);

        if (!result.hasErrors()) {
            String dn = null;
            try {
                dn = ldapUtil.getDnByUid(userPasswordContainer.getUsername());
                if (dn != null) {
                    ldapUtil.updatePassword(dn, userPasswordContainer.getOldPassword(),
                            userPasswordContainer.getPassword());
                    status.setComplete();
                } else {
                    // FIXME Komunikaty pobierane z pliku *.properties. Dotyczy
                    // również innych wywołań konstruktora ObjectError w tej
                    // metodzie.
                    result.addError(new ObjectError("upc", String.format(
                            "Nie znaleziono użytkownika %s na serwerze LDAP", userPasswordContainer.getUsername())));
                }
            } catch (LdapConnectException e) {
                if (dn == null) { // blad logowania na usera administracyjnego
                    result.addError(new ObjectError("upc", "Błąd logowania na użytkownika administracyjnego"));
                } else { // blad logowania na wlasciwego usera
                    result.addError(new ObjectError("upc", String.format("Błąd logowania użytkownika %s",
                            userPasswordContainer.getUsername())));
                }
            } catch (LdapNonUniqueUidException e) {
                result.addError(new ObjectError("upc", String.format("Nazwa użytkownika %s jest niejednoznaczna",
                        userPasswordContainer.getUsername())));
            } catch (LdapException e) {
                result.addError(new ObjectError("upc", String.format("Błąd podczas operacji na serwerze LDAP: %s",
                        e.getMessage())));
            }
            // form success
        }
        return "example";
    }

    @RequestMapping(method = RequestMethod.GET)
    public String printWelcome(ModelMap model) {
        ldapUtil.testConfig();
        System.err.println(System.getProperty("config"));

        model.addAttribute("upc", new UserPasswordContainer());
        return "example";
    }

}
