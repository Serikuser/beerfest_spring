package by.siarhei.beerfest.dao.impl.template;

import by.siarhei.beerfest.dao.api.RegistrationDao;
import by.siarhei.beerfest.entity.impl.Registration;
import by.siarhei.beerfest.exception.DaoException;
import by.siarhei.beerfest.factory.impl.EntityBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.List;

public class RegistrationDaoJdbcTemplateBased implements RegistrationDao {
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

    private JdbcTemplate template;
    private EntityBuilder builder;

    private RegistrationDaoJdbcTemplateBased(JdbcTemplate template, EntityBuilder builder) {
        this.template = template;
        this.builder = builder;
    }

    @Override
    public void updateExpiredByToken(long id, boolean status) throws DaoException {
        try {
            template.update(UPDATE_EXPIRED_BY_ID_SQL, status, id);
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot change status id: %s", id), e);
        }
    }

    @Override
    public Registration findRegistrationByToken(String token) throws DaoException {
        try {
            return template.queryForObject(UPDATE_EXPIRED_BY_ID_SQL, new Object[]{token},
                    (resultSet, rowNumber) -> {
                        int columnIndex = 0;
                        long id = resultSet.getLong(++columnIndex);
                        long accountId = resultSet.getLong(++columnIndex);
                        ++columnIndex;
                        boolean isExpired = resultSet.getBoolean(++columnIndex);
                        return builder.buildRegistration(id,accountId,token,isExpired);
                    });
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot find registration by token: %s", token), e);
        }
    }

    @Override
    public List<Registration> findAllNotExpiredTokens() throws DaoException {
        try {
            return template.query(SELECT_NOT_EXPIRED_TOKENS_ID_SQL,
                    (resultSet, rowNumber) -> {
                        int columnIndex = 0;
                        long id = resultSet.getLong(++columnIndex);
                        Timestamp date = resultSet.getTimestamp(++columnIndex);
                        return builder.buildRegistration(id,date);
                    });
        } catch (DataAccessException e) {
            throw new DaoException("Cannot select expired tokens throws excpetion", e);
        }
    }

    @Override
    public void deleteExpired() throws DaoException {
        try {
            template.update(DELETE_EXPIRED_REGISTRATIONS_SQL);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot delete registration", e);
        }
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
    public boolean delete(Registration registration) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long create(Registration registration) throws DaoException {
        try {
            PreparedStatementCreator preparedStatementCreator = connection -> {
                PreparedStatement statement = connection.prepareStatement(INSERT_REGISTRATION_SQL, new String[]{"id"});
                int columnIndex = 0;
                statement.setLong(++columnIndex, registration.getAccountId());
                statement.setString(++columnIndex, registration.getToken());
                statement.setBoolean(++columnIndex, registration.isExpired());
                statement.setTimestamp(++columnIndex, registration.getDate());
                return statement;
            };
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(preparedStatementCreator, keyHolder);
            return (int) keyHolder.getKey();
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot insert new registration: %s", registration), e);
        }
    }

    @Override
    public Registration update(Registration registration) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
