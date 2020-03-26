package by.siarhei.beerfest.dao;

import by.siarhei.beerfest.datasource.CustomDataSourceBasedConnectionPool;

import java.sql.Connection;

public abstract class DaoTransaction {
    private Connection connection;
    protected boolean inTransaction;
    private CustomDataSourceBasedConnectionPool connectionPool;

    public DaoTransaction() {
    }

    public DaoTransaction(CustomDataSourceBasedConnectionPool connectionPool) {
        this.connectionPool = connectionPool;
    }

    public void setInTransaction() {
        this.inTransaction = true;
    }

    protected Connection getConnection() {
        if (connection == null) {
            return connectionPool.getConnection();
        } else {
            return connection;
        }
    }

    void setConnection(Connection connection) {
        this.connection = connection;
    }
}
