package by.siarhei.beerfest.filter;

import by.siarhei.beerfest.command.LocaleType;
import by.siarhei.beerfest.config.MessageManager;
import by.siarhei.beerfest.config.SpringAppContext;
import by.siarhei.beerfest.service.api.LanguageService;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(urlPatterns = {"/controller"}, servletNames = {"Controller"})
public class ServletEmptyCommandFilter implements Filter {
    private static final String URL = "/";
    private static final String PARAMETER_COMMAND = "command";
    private static final String ATTRIBUTE_ERROR_MESSAGE = "errorMessage";
    private static final String ERROR_MESSAGE = "message.signup.error.joke";
    private static final String BEAN_TOKEN_LANGUAGE_SERVICE = "languageService";

    private LanguageService languageService;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        languageService = (LanguageService) SpringAppContext.getApplicationContext().getBean(BEAN_TOKEN_LANGUAGE_SERVICE);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        LocaleType localeType = languageService.defineLocale(request);
        String action = request.getParameter(PARAMETER_COMMAND);
        if (action == null || action.isBlank()) {
            request.setAttribute(ATTRIBUTE_ERROR_MESSAGE, MessageManager.getProperty(ERROR_MESSAGE, localeType));
            RequestDispatcher dispatcher = request.getServletContext()
                    .getRequestDispatcher(URL);
            dispatcher.forward(request, response);
            return;
        }
        filterChain.doFilter(request, response);
    }

    @Override
    public void destroy() {

    }
}
