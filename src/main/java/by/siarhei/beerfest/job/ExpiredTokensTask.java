package by.siarhei.beerfest.job;

import by.siarhei.beerfest.dao.api.RegistrationDao;
import by.siarhei.beerfest.exception.DaoException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.TimerTask;

public class ExpiredTokensTask extends TimerTask {
    private static final Logger logger = LogManager.getLogger();

    private RegistrationDao registrationDao;

    private ExpiredTokensTask(RegistrationDao dao) {
        registrationDao = dao;
    }

    @Override
    public void run() {
        try {
            registrationDao.deleteExpired();
        } catch (DaoException e) {
            logger.error("Cannot delete expired tokens ", e);
        }
    }
}
