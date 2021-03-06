package by.siarhei.beerfest.dao.impl.connection;

import by.siarhei.beerfest.dao.api.RegistrationDao;
import by.siarhei.beerfest.dao.transaction.DaoManager;
import by.siarhei.beerfest.datasource.CustomDataSourceBasedConnectionPool;
import by.siarhei.beerfest.entity.impl.Registration;
import by.siarhei.beerfest.exception.DaoException;
import by.siarhei.beerfest.factory.impl.EntityBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RegistrationDaoImpl extends DaoManager implements RegistrationDao {
    private static final String INSERT_REGISTRATION_SQL = "INSERT INTO registration (account_id,token,expired,date) VALUES ('%s','%s','%s','%s')";
    private static final String DELETE_REGISTRATION_BY_ID_SQL = "DELETE FROM registration WHERE registration.id = %s";
    private static final String DELETE_EXPIRED_REGISTRATIONS_SQL = "DELETE FROM registration WHERE expired = true";
    private static final String SELECT_NOT_EXPIRED_TOKENS_ID_SQL = "SELECT registration.id,registration.date FROM registration WHERE expired = false";
    private static final String FIND_REGISTRATION_BY_TOKEN_SQL =
            "SELECT registration.id,registration.account_id,registration.token,registration.expired " +
                    "FROM registration " +
                    "WHERE token=?";
    private static final String UPDATE_EXPIRED_BY_ID_SQL =
            "UPDATE registration " +
                    "SET expired=? " +
                    "WHERE id=?";

    public RegistrationDaoImpl() {
    }

    public RegistrationDaoImpl(CustomDataSourceBasedConnectionPool connectionPool, EntityBuilder builder) {
        super(connectionPool,builder);
    }

    @Override
    public List<Registration> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Registration findEntity(Long id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void updateExpiredByToken(long id, boolean status) throws DaoException {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(UPDATE_EXPIRED_BY_ID_SQL);
            statement.setBoolean(1, status);
            statement.setLong(2, id);
            statement.execute();
        } catch (SQLException e) {
            throw new DaoException(String.format("Cannot change status id: %s", id), e);
        } finally {
            close(statement);
            if (!inTransaction) {
                close(connection);
            }
        }
    }

    @Override
    public Registration findRegistrationByToken(String token) throws DaoException {
        Registration registration = getBuilder().buildRegistration();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(FIND_REGISTRATION_BY_TOKEN_SQL);
            statement.setString(1, token);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int columnIndex = 0;
                long id = resultSet.getLong(++columnIndex);
                registration.setId(id);
                long accountId = resultSet.getLong(++columnIndex);
                registration.setAccountId(accountId);
                ++columnIndex;
                registration.setToken(token);
                boolean isExpired = resultSet.getBoolean(++columnIndex);
                registration.setExpired(isExpired);
            }
        } catch (SQLException e) {
            throw new DaoException(String.format("Cannot find registration by token: %s", token), e);
        } finally {
            close(statement);
            if (!inTransaction) {
                close(connection);
            }
            close(resultSet);
        }
        return registration;
    }

    @Override
    public List<Registration> findAllNotExpiredTokens() throws DaoException {
        List<Registration> list = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SELECT_NOT_EXPIRED_TOKENS_ID_SQL);
            EntityBuilder builder = getBuilder();
            while (resultSet.next()) {
                int columnIndex = 0;
                long id = resultSet.getLong(++columnIndex);
                Timestamp date = resultSet.getTimestamp(++columnIndex);
                list.add(builder.buildRegistration(id,date));
            }
        } catch (SQLException e) {
            throw new DaoException("Cannot select expired tokens throws excpetion", e);
        } finally {
            close(statement);
            if (!inTransaction) {
                close(connection);
            }
            close(resultSet);
        }
        return list;
    }

    @Override
    public boolean delete(Registration registration) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void deleteExpired() throws DaoException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.execute(DELETE_EXPIRED_REGISTRATIONS_SQL);
        } catch (SQLException e) {
            throw new DaoException("Cannot delete registration", e);
        } finally {
            if (!inTransaction) {
                close(connection);
            }
            close(statement);
        }
    }

    @Override
    public void delete(Long id) throws DaoException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.execute(String.format(DELETE_REGISTRATION_BY_ID_SQL, id));
            logger.error(String.format("Token with id: %s deleted", id));
        } catch (SQLException e) {
            throw new DaoException("Cannot delete registration", e);
        } finally {
            if (!inTransaction) {
                close(connection);
            }
            close(statement);
        }
    }

    @Override
    public long create(Registration registration) throws DaoException {
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            long accountId = registration.getAccountId();
            String token = registration.getToken();
            boolean expired = registration.isExpired();
            Timestamp date = registration.getDate();
            statement.execute(String.format(INSERT_REGISTRATION_SQL, accountId, token, expired, date), Statement.RETURN_GENERATED_KEYS);
            logger.info(String.format("Created registration:%s", registration));
            resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DaoException(String.format("Cannot insert new registration: %s", registration), e);
        } finally {
            close(statement);
            close(resultSet);
            if (!inTransaction) {
                close(connection);
            }
        }
    }

    @Override
    public Registration update(Registration registration) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
