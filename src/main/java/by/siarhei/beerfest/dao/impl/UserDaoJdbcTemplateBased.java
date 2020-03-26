package by.siarhei.beerfest.dao.impl;

import by.siarhei.beerfest.config.SpringAppContext;
import by.siarhei.beerfest.dao.UserDao;
import by.siarhei.beerfest.entity.RoleType;
import by.siarhei.beerfest.entity.StatusType;
import by.siarhei.beerfest.entity.impl.User;
import by.siarhei.beerfest.exception.DaoException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.List;

public class UserDaoJdbcTemplateBased implements UserDao {
    private static final String INSERT_LOGIN_SQL = "INSERT INTO account (login,password,email,avatar_url,role,status) VALUES (?,?,?,?,?,?)";
    private static final String COINCIDENCES_RESULT_INDEX = "coincidences";
    private static final String CHECK_USER_BY_LOGIN_EMAIL_SQL = "SELECT count(*) FROM account WHERE login= ? or email= ?";
    private static final String CHECK_USER_BY_LOGIN_PASSWORD_SQL = "SELECT count(*) FROM account WHERE login= ? and password= ?";
    private static final String SELECT_USER_BY_LOGIN_SQL =
            "SELECT account.id,login,password,email,avatar_url,role.name,status.name " +
                    "FROM account " +
                    "INNER JOIN role " +
                    "ON account.role = role.id " +
                    "INNER JOIN status " +
                    "ON account.status = status.id WHERE login=?";
    private static final String UPDATE_PASSWORD_BY_LOGIN_SQL =
            "UPDATE account " +
                    "SET password=? " +
                    "WHERE login=?";
    private static final String UPDATE_AVATAR_BY_LOGIN_SQL =
            "UPDATE account " +
                    "SET avatar_url=? " +
                    "WHERE login=?";
    private static final String UPDATE_STATUS_BY_LOGIN_SQL =
            "UPDATE account " +
                    "SET status=? " +
                    "WHERE login=?";
    private static final String UPDATE_STATUS_BY_ID_SQL =
            "UPDATE account " +
                    "SET status=? " +
                    "WHERE id=?";
    private static final String SELECT_USER_BY_ID_SQL =
            "SELECT account.id,login,password,email,avatar_url,role.name,status.name " +
                    "FROM account " +
                    "INNER JOIN role " +
                    "ON account.role = role.id " +
                    "INNER JOIN status " +
                    "ON account.status = status.id WHERE account.id=?";
    private static final String SELECT_ALL_USER_SQL =
            "SELECT account.id,login,email,role.name,status.name " +
                    "FROM account  " +
                    "INNER JOIN role " +
                    "ON account.role = role.id " +
                    "INNER JOIN status " +
                    "ON account.status = status.id " +
                    "ORDER BY role.id,account.login " +
                    "OFFSET ? " +
                    "LIMIT 5";
    private static final String COUNT_USER_SQL = "SELECT count(*) as count FROM account";

    private JdbcTemplate template;

    private UserDaoJdbcTemplateBased(JdbcTemplate jdbcTemplate) {

        this.template = jdbcTemplate;
    }

    @Override
    public User findUserByLogin(String login) throws DaoException {
        try {
            return template.queryForObject(SELECT_USER_BY_LOGIN_SQL, new Object[]{login},
                    (resultSet, rowNumber) -> {
                        User user = (User) SpringAppContext.getBean("user");
                        int columnIndex = 0;
                        int id = resultSet.getInt(++columnIndex);
                        user.setId(id);
                        String userLogin = resultSet.getString(++columnIndex);
                        user.setLogin(userLogin);
                        String password = resultSet.getString(++columnIndex);
                        user.setPassword(password);
                        String eMail = resultSet.getString(++columnIndex);
                        user.setEmail(eMail);
                        String avatarUrl = resultSet.getString(++columnIndex);
                        user.setAvatarUrl(avatarUrl);
                        String role = resultSet.getString(++columnIndex);
                        user.setRole(RoleType.valueOf(role.toUpperCase()));
                        String status = resultSet.getString(++columnIndex);
                        user.setStatus(StatusType.valueOf(status.toUpperCase()));
                        return user;
                    });
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot find user by login: %s", login), e);
        }
    }

    @Override
    public boolean isLoginPasswordMatch(String login, String password) throws DaoException {
        try {
            return template.queryForObject(CHECK_USER_BY_LOGIN_PASSWORD_SQL, new Object[]{login, password}, Integer.class) != 0;
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot check login/password exists: %s", login), e);
        }
    }

    @Override
    public boolean isExist(String login, String eMail) throws DaoException {
        try {
            return template.queryForObject(CHECK_USER_BY_LOGIN_EMAIL_SQL, new Object[]{login, eMail}, Integer.class) != 0;
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot check login/eMail exists exists: %s", login), e);
        }
    }

    @Override
    public void updatePassword(String login, String newPassword) throws DaoException {
        try {
            template.update(UPDATE_PASSWORD_BY_LOGIN_SQL, newPassword, login);
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot change password: %s", login), e);
        }
    }

    @Override
    public void updateAvatar(String login, String uploadedFilePath) throws DaoException {
        try {
            template.update(UPDATE_AVATAR_BY_LOGIN_SQL, uploadedFilePath, login);
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot change avatar: %s", login), e);
        }
    }

    @Override
    public void updateStatus(String login, int status) throws DaoException {
        try {
            template.update(UPDATE_STATUS_BY_LOGIN_SQL, status, login);
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot change status login: %s", login), e);
        }
    }

    @Override
    public void updateStatusById(long id, int status) throws DaoException {
        try {
            template.update(UPDATE_STATUS_BY_ID_SQL, status, id);
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot change status id: %s", id), e);
        }
    }

    @Override
    public int countUsers() throws DaoException {
        try {
            return template.queryForObject(COUNT_USER_SQL, Integer.class);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot count users", e);
        }
    }

    @Override
    public List<User> findAll(long offset) throws DaoException {
        try {
            return template.query(SELECT_ALL_USER_SQL, new Object[]{offset},
                    (resultSet, i) -> {
                        User user = (User) SpringAppContext.getBean("user");
                        int columnIndex = 0;
                        long id = resultSet.getLong(++columnIndex);
                        user.setId(id);
                        String login = resultSet.getString(++columnIndex);
                        user.setLogin(login);
                        String eMail = resultSet.getString(++columnIndex);
                        user.setEmail(eMail);
                        String role = resultSet.getString(++columnIndex);
                        user.setRole(RoleType.valueOf(role.toUpperCase()));
                        String status = resultSet.getString(++columnIndex);
                        user.setStatus(StatusType.valueOf(status.toUpperCase()));
                        return user;
                    });
        } catch (DataAccessException e) {
            throw new DaoException("Cannot find users", e);
        }
    }

    @Override
    public List<User> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public User findEntity(Long id) throws DaoException {
        try {
            return template.queryForObject(SELECT_USER_BY_ID_SQL, new Object[]{id},
                    (resultSet, i) -> {
                        User user = (User) SpringAppContext.getBean("user");
                        int columnIndex = 1;
                        user.setId(id);
                        String login = resultSet.getString(++columnIndex);
                        user.setLogin(login);
                        String password = resultSet.getString(++columnIndex);
                        user.setPassword(password);
                        String eMail = resultSet.getString(++columnIndex);
                        user.setEmail(eMail);
                        String avatarUrl = resultSet.getString(++columnIndex);
                        user.setAvatarUrl(avatarUrl);
                        String role = resultSet.getString(++columnIndex);
                        user.setRole(RoleType.valueOf(role.toUpperCase()));
                        String status = resultSet.getString(++columnIndex);
                        user.setStatus(StatusType.valueOf(status.toUpperCase()));
                        return user;
                    });
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot find user by id: %s", id), e);
        }
    }

    @Override
    public boolean delete(User user) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long create(User user) throws DaoException {
        try {
            PreparedStatementCreator preparedStatementCreator = connection -> {
                PreparedStatement statement = connection.prepareStatement(INSERT_LOGIN_SQL, new String[]{"id"});
                int columnIndex = 0;
                statement.setString(++columnIndex, user.getLogin());
                statement.setString(++columnIndex, user.getPassword());
                statement.setString(++columnIndex, user.getEmail());
                statement.setString(++columnIndex, user.getAvatarUrl());
                statement.setInt(++columnIndex, user.getRole().getValue());
                statement.setInt(++columnIndex, user.getStatus().getValue());
                return statement;
            };
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(preparedStatementCreator, keyHolder);
            return (int) keyHolder.getKey();
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot insert new user: %s", user), e);
        }
    }

    @Override
    public User update(User user) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
