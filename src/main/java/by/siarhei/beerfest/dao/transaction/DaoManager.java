package by.siarhei.beerfest.dao.transaction;

import by.siarhei.beerfest.config.SpringAppContext;
import by.siarhei.beerfest.factory.impl.EntityBuilder;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;

public abstract class DaoManager {
    private Connection connection;
    protected boolean inTransaction;
    private DataSource connectionPool;
    private EntityBuilder builder;

    public DaoManager() {
        this.connectionPool = (DataSource) SpringAppContext.getBean("connectionPool");
        this.builder = (EntityBuilder)SpringAppContext.getBean("entityBuilder");
    }

    public DaoManager(DataSource connectionPool, EntityBuilder builder) {
        this.connectionPool = connectionPool;
        this.builder = builder;
    }

    public void setInTransaction() {
        this.inTransaction = true;
    }

    protected Connection getConnection() throws SQLException {
        if (connection == null) {
            return connectionPool.getConnection();
        } else {
            return connection;
        }
    }

    protected EntityBuilder getBuilder() {
        return this.builder;
    }

    void setConnection(Connection connection) {
        this.connection = connection;
    }
}
