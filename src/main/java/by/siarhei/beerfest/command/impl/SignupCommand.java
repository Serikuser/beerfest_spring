package by.siarhei.beerfest.command.impl;


import by.siarhei.beerfest.command.LocaleType;
import by.siarhei.beerfest.command.Router;
import by.siarhei.beerfest.command.api.ActionCommand;
import by.siarhei.beerfest.config.ConfigurationManager;
import by.siarhei.beerfest.config.MessageManager;
import by.siarhei.beerfest.entity.RoleType;
import by.siarhei.beerfest.entity.StatusType;
import by.siarhei.beerfest.exception.ServiceException;
import by.siarhei.beerfest.service.api.AccountService;
import by.siarhei.beerfest.service.api.LanguageService;
import by.siarhei.beerfest.service.api.RegistrationService;
import by.siarhei.beerfest.servlet.SessionRequestContent;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * Realization of {@code ActionCommand} interface.
 * Command is processing signup new user logic.
 * <p>
 * using {@code LanguageService}.
 * using {@code AccountService}
 * using {@code RegistrationService}
 */
public class SignupCommand implements ActionCommand, ApplicationContextAware {

    public static final String PARAMETER_USERNAME = "username";
    public static final String PARAMETER_EMAIL = "email";
    public static final String PARAMETER_PASSWORD = "password";
    public static final String PARAMETER_ROLE = "role";
    private static final String JSP_MAIN = "path.page.main";
    private static final String ATTRIBUTE_MESSAGE = "errorMessage";
    private static final String SIGNUP_SUCCESS = "message.signup.success";
    private static final String SIGNUP_ERROR = "message.signup.error";
    private static final String SIGNUP_SERVER_ERROR = "message.signup.error.server";
    private static final String SIGNUP_ERROR_JOKE = "message.signup.error.joke";

    private ApplicationContext applicationContext;

    /**
     * {@code LanguageService} used to display messages based on user's locale.
     */
    private LanguageService languageService;

    /**
     * {@code AccountService} used to define signup logic.
     */
    private AccountService accountService;

    /**
     * {@code RegistrationService} used to define registration logic by sending on user's eMail unique generated token.
     */
    private RegistrationService registrationService;

    public SignupCommand(LanguageService languageService, AccountService accountService, RegistrationService registrationService) {
        this.languageService = languageService;
        this.accountService = accountService;
        this.registrationService = registrationService;
    }

    /**
     * Call gets user's signup data to process registration logic with validation on {@code AccountService}
     * and registration on {@code RegistrationService}
     * forwards user to {@code main.jsp}
     *
     * @param content object that contain request, response and session information.
     * @return {@code Router} with forward routing type.
     */
    @Override
    public Router execute(SessionRequestContent content) {
        String uri = ConfigurationManager.getProperty(JSP_MAIN);
        LocaleType localeType = languageService.defineLocale(content);
        if (isEnterDataExist(content)) {
            String login = content.getParameter(PARAMETER_USERNAME);
            String eMail = content.getParameter(PARAMETER_EMAIL);
            try {
                if (accountService.checkUserByLoginEmail(login, eMail)) {
                    String password = content.getParameter(PARAMETER_PASSWORD);
                    RoleType role = RoleType.valueOf(content.getParameter(PARAMETER_ROLE).toUpperCase());
                    long id = accountService.signupUser(login, eMail, password, role, StatusType.INACTIVE);
                    registrationService.addRegistrationToken(id, eMail);
                    content.setAttribute(ATTRIBUTE_MESSAGE, MessageManager.getProperty(SIGNUP_SUCCESS, localeType));
                } else {
                    content.setAttribute(ATTRIBUTE_MESSAGE, MessageManager.getProperty(SIGNUP_ERROR, localeType));
                }
            } catch (ServiceException e) {
                content.setAttribute(ATTRIBUTE_MESSAGE, MessageManager.getProperty(SIGNUP_SERVER_ERROR, localeType));
            }
        } else {
            content.setAttribute(ATTRIBUTE_MESSAGE, MessageManager.getProperty(SIGNUP_ERROR_JOKE, localeType));
        }
        return new Router(uri);
    }

    /**
     * Call method check entered data for existence
     *
     * @param content object that contain request, response and session information.
     * @return boolean value is entered data exists.
     */
    private boolean isEnterDataExist(SessionRequestContent content) {
        return content.getParameter(PARAMETER_USERNAME) != null
                && content.getParameter(PARAMETER_EMAIL) != null
                && content.getParameter(PARAMETER_PASSWORD) != null
                && content.getParameter(PARAMETER_ROLE) != null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}

