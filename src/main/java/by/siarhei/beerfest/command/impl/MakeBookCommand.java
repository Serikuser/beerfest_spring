package by.siarhei.beerfest.command.impl;

import by.siarhei.beerfest.command.LocaleType;
import by.siarhei.beerfest.command.Router;
import by.siarhei.beerfest.command.api.ActionCommand;
import by.siarhei.beerfest.config.ConfigurationManager;
import by.siarhei.beerfest.config.MessageManager;
import by.siarhei.beerfest.entity.RoleType;
import by.siarhei.beerfest.entity.impl.Bar;
import by.siarhei.beerfest.exception.ServiceException;
import by.siarhei.beerfest.service.api.BarService;
import by.siarhei.beerfest.service.api.BookService;
import by.siarhei.beerfest.service.api.LanguageService;
import by.siarhei.beerfest.servlet.SessionRequestContent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Realization of {@code ActionCommand} interface.
 * Command is processing login user logic.
 *
 * using {@code LanguageService}.
 * using {@code BookService}
 * using {@code BarService}
 */
public class MakeBookCommand implements ActionCommand {
    private static final Logger logger = LogManager.getLogger();

    private static final String JSP_MAIN = "path.page.main";
    private static final String JSP_PARTICIPANTS = "path.page.participants";
    private static final String ATTRIBUTE_USER_LOGIN = "userLogin";
    private static final String ATTRIBUTE_USER_ROLE = "userRole";
    private static final String ATTRIBUTE_MESSAGE = "bookErrorMessage";
    private static final String ATTRIBUTE_INDEX_MESSAGE = "errorMessage";
    private static final String ATTRIBUTE_ACCOUNT_ID = "accountId";
    private static final String ATTRIBUTE_PARTICIPANTS = "participants";
    private static final String ATTRIBUTE_BOOK_ERROR_MESSAGE = "bookErrorMessage";
    private static final String ERROR_JOKE = "message.signup.error.joke";
    private static final String ERROR_UPDATE_MESSAGE = "message.update.error";
    private static final String MAKE_BOOK_ERROR = "message.submit.book.error";
    private static final String MAKE_BOOK_ERROR_INVALID_DATE = "message.submit.book.error.date";
    private static final String MAKE_BOOK_ERROR_FULL = "message.submit.book.full";
    private static final String MAKE_BOOK_SUCCESS = "message.submit.book.success";
    private static final String PARAMETER_BAR_ID = "barId";
    private static final String PARAMETER_BOOK_PLACES = "bookPlaces";
    private static final String PARAMETER_BOOK_DATE = "bookDate";

    /**
     * {@code LanguageService} used to display messages based on user's locale.
     */
    private LanguageService languageService;

    /**
     * {@code BookService} used to define booking logic.
     */
    private BookService bookService;

    /**
     * {@code BarService} used to define displaying bar data logic for {@code RoleType} Guest.
     */
    private BarService barService;

    public MakeBookCommand(LanguageService languageService,BookService bookService, BarService barService) {
        this.languageService = languageService;
        this.bookService = bookService;
        this.barService = barService;
    }

    /**
     * Call method checking user's book on max value = 2, if limit is not exhausted
     * makes book for user based on user id and bar id. Otherwise setting to user error message
     * forwards user to {@code participants.jsp}
     *
     * @param content object that contain request, response and session information.
     * @return {@code Router} with forward routing type.
     */
    @Override
    public Router execute(SessionRequestContent content) {
        String uri = ConfigurationManager.getProperty(JSP_MAIN);
        LocaleType localeType = languageService.defineLocale(content);
        String login = (String) content.getSessionAttribute(ATTRIBUTE_USER_LOGIN);
        if (isEnterDataExist(content) && content.getSessionAttribute(ATTRIBUTE_USER_ROLE) == RoleType.GUEST) {
            uri = ConfigurationManager.getProperty(JSP_PARTICIPANTS);
            try {
                if (bookService.checkUserBook(login)) {
                    long accountId = (long) content.getSessionAttribute(ATTRIBUTE_ACCOUNT_ID);
                    long barId = Long.parseLong(content.getParameter(PARAMETER_BAR_ID));
                    int places = Integer.parseInt(content.getParameter(PARAMETER_BOOK_PLACES));
                    String inputDate = content.getParameter(PARAMETER_BOOK_DATE);
                    if (!inputDate.isEmpty()) {
                        Date date = Date.valueOf(inputDate);
                        bookService.makeBook(accountId, barId, places, date);
                        content.setAttribute(ATTRIBUTE_MESSAGE, MessageManager.getProperty(MAKE_BOOK_SUCCESS, localeType));
                    } else {
                        content.setAttribute(ATTRIBUTE_MESSAGE, MessageManager.getProperty(MAKE_BOOK_ERROR_INVALID_DATE, localeType));
                    }
                } else {
                    content.setAttribute(ATTRIBUTE_MESSAGE, MessageManager.getProperty(MAKE_BOOK_ERROR_FULL, localeType));
                }
            } catch (ServiceException e) {
                content.setAttribute(ATTRIBUTE_MESSAGE, MessageManager.getProperty(MAKE_BOOK_ERROR, localeType));
            }
            fillParticipantList(content, localeType);
        } else {
            content.setAttribute(ATTRIBUTE_INDEX_MESSAGE, MessageManager.getProperty(ERROR_JOKE, localeType));
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
        return content.getParameter(PARAMETER_BAR_ID) != null
                && content.getParameter(PARAMETER_BOOK_PLACES) != null
                && content.getParameter(PARAMETER_BOOK_DATE) != null;
    }

    /**
     * Call method filling bar date to display it to user after making book
     *
     * @param content object that contain request, response and session information..
     */
    private void fillParticipantList(SessionRequestContent content, LocaleType localeType) {
        List<Bar> list = new ArrayList<>();
        try {
            list = barService.updateParticipants();
        } catch (ServiceException e) {
            logger.error(String.format("Cant update beer/food list throws exception: %s", e));
            content.setAttribute(ATTRIBUTE_BOOK_ERROR_MESSAGE, MessageManager.getProperty(ERROR_UPDATE_MESSAGE, localeType));
        }
        content.setAttribute(ATTRIBUTE_PARTICIPANTS, list);
    }
}
