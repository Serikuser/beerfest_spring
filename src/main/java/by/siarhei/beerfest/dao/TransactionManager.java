package by.siarhei.beerfest.dao;

import by.siarhei.beerfest.config.SpringAppContext;
import by.siarhei.beerfest.datasource.CustomDataSourceBasedConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.SQLException;

public class TransactionManager {
    private static final Logger logger = LogManager.getLogger();
    private static final String BEAN_TOKEN_CONNECTION_POOL = "connectionPool";

    private Connection connection;
    private CustomDataSourceBasedConnectionPool connectionPool;

    public TransactionManager() {
        connectionPool = (CustomDataSourceBasedConnectionPool) SpringAppContext.getApplicationContext().getBean(BEAN_TOKEN_CONNECTION_POOL);

    }

    public void openTransaction(DaoTransaction dao, DaoTransaction... daos) {
        if (connection == null) {
            connection = connectionPool.getConnection();
        }
        try {
            connection.setAutoCommit(false);
        } catch (SQLException e) {
            logger.error(String.format("Auto commit cannot be turned off throws exception: %s", e));
        }
        dao.setConnection(connection);
        dao.setInTransaction();
        for (DaoTransaction daoElement : daos) {
            daoElement.setConnection(connection);
            daoElement.setInTransaction();
        }
    }

    public void closeTransaction() {
        if (connection != null) {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                logger.error(String.format("Auto commit cannot be turned on throws exception: %s", e));
            }
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error(String.format("Connection cant be closed throws exception: %s", e));
            }
            connection = null;
        }
    }

    public void commit() {
        try {
            connection.commit();
        } catch (SQLException e) {
            logger.error(String.format("Cannot commit throws exception: %s", e));
        }
    }

    public void rollback() {
        try {
            connection.rollback();
        } catch (SQLException e) {
            logger.error(String.format("Cannot rollback throws exception: %s", e));
        }
    }
}
