package by.siarhei.beerfest.listener;

import by.siarhei.beerfest.config.SpringAppContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

@WebListener
public class InitializationListener implements ServletContextListener {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        SpringAppContext.init();

    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        SpringAppContext.destroy();
    }
}
