package by.siarhei.beerfest.config;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class SpringAppContext {

    private static final String SPRING_XML = "spring.xml";

    private SpringAppContext() {
    }

    private static ConfigurableApplicationContext applicationContext;

    public static void init() {
        applicationContext = new ClassPathXmlApplicationContext(SPRING_XML);
    }

    public static void destroy() {
        applicationContext.close();
    }

    public static ConfigurableApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public static Object getBean(String name) {
        return getApplicationContext().getBean(name);
    }
}
