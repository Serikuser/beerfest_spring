package by.siarhei.beerfest.datasource;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public abstract class DriverBasedDataSource extends AbstractSource {

    private String databaseUrl;
    private String userName;
    private String userPassword;
    private String driverName;

    public DriverBasedDataSource(String databaseUrl, String driverName) {
        this.databaseUrl = databaseUrl;
        this.driverName = driverName;
        setDriverClassName();
    }

    public DriverBasedDataSource(String userName, String userPassword, String databaseUrl, String driverName) {
        this.userName = userName;
        this.userPassword = userPassword;
        this.databaseUrl = databaseUrl;
        this.driverName = driverName;
        setDriverClassName();
    }

    private void setDriverClassName() {
        try {
            Class.forName(driverName);
        } catch (ClassNotFoundException e) {
            logger.fatal(String.format("Poll cant register drivers throws exception: %s", e));
            throw new ExceptionInInitializerError("Cant install database drivers");
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("getConnection with custom username/password");
    }

    protected Connection getDriverBasedConnection() throws SQLException {
        return new ProxyConnection(
                DriverManager.getConnection(databaseUrl, userName, userPassword));
    }
}
