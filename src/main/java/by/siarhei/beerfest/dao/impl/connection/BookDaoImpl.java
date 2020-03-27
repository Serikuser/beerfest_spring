package by.siarhei.beerfest.dao.impl.connection;

import by.siarhei.beerfest.dao.api.BookDao;
import by.siarhei.beerfest.dao.transaction.DaoManager;
import by.siarhei.beerfest.datasource.CustomDataSourceBasedConnectionPool;
import by.siarhei.beerfest.entity.impl.Book;
import by.siarhei.beerfest.exception.DaoException;
import by.siarhei.beerfest.factory.impl.EntityBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookDaoImpl extends DaoManager implements BookDao {
    private static final String INSERT_BOOK_SQL = "INSERT INTO book (guest_id,bar_id,reserved_places,reservation_date) VALUES (?,?,?,?)";
    private static final String COINCIDENCES_RESULT_INDEX = "coincidences";
    private static final String CHECK_BOOK_BY_LOGIN_SQL = String.format(
            "SELECT count(*) as %s " +
                    "FROM book " +
                    "INNER JOIN account " +
                    "ON book.guest_id = account.id " +
                    "WHERE account.login= ?", COINCIDENCES_RESULT_INDEX);
    private static final String SELECT_BOOK_BY_USER_ID_SQL =
            "SELECT book.id,bar.name, book.reserved_places, book.reservation_date " +
                    "FROM book " +
                    "INNER JOIN account " +
                    "ON book.guest_id = account.id " +
                    "INNER JOIN bar " +
                    "ON book.bar_id = bar.id " +
                    "where guest_id = ? ";
    private static final String SELECT_BOOK_BY_BAR_ID_SQL =
            "SELECT account.login,book.reservation_date,book.reserved_places " +
                    "FROM book " +
                    "INNER JOIN account " +
                    "ON book.guest_id = account.id " +
                    "INNER JOIN bar " +
                    "ON book.bar_id = bar.id " +
                    "WHERE bar.id = ? ";
    private static final String DELETE_BOOK_BY_ID_SQL = "DELETE FROM book WHERE book.id = %s";

    public BookDaoImpl() {
    }

    public BookDaoImpl(CustomDataSourceBasedConnectionPool connectionPool, EntityBuilder builder) {
        super(connectionPool,builder);
    }

    @Override
    public List<Book> findAll() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Book findEntity(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Book book) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) throws DaoException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.execute(String.format(DELETE_BOOK_BY_ID_SQL, id));
        } catch (SQLException e) {
            throw new DaoException("Cannot delete book", e);
        } finally {
            if (!inTransaction) {
                close(connection);
            }
            close(statement);
        }
    }

    @Override
    public List<Book> findUserBook(Long id) throws DaoException {
        List<Book> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_BOOK_BY_USER_ID_SQL);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            EntityBuilder builder = getBuilder();
            while (resultSet.next()) {
                int columnIndex = 0;
                long bookId = resultSet.getLong(++columnIndex);
                String barName = resultSet.getString(++columnIndex);
                int reservedPlaces = resultSet.getInt(++columnIndex);
                Date date = resultSet.getDate(++columnIndex);
                list.add(builder.buildBook(bookId,barName,reservedPlaces,date));
            }
        } catch (SQLException e) {
            throw new DaoException("Cannot find user book ", e);
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
    public List<Book> findBarBook(Long id) throws DaoException {
        List<Book> list = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        EntityBuilder builder = getBuilder();
        try {
            connection = getConnection();
            statement = connection.prepareStatement(SELECT_BOOK_BY_BAR_ID_SQL);
            statement.setLong(1, id);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                int columnIndex = 0;
                String userName = resultSet.getString(++columnIndex);
                Date date = resultSet.getDate(++columnIndex);
                int reservedPlaces = resultSet.getInt(++columnIndex);
                list.add(builder.buildBar(userName,date,reservedPlaces));
            }
        } catch (SQLException e) {
            throw new DaoException("Cannot find bar book ", e);
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
    public long create(Book book) throws DaoException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(INSERT_BOOK_SQL,Statement.RETURN_GENERATED_KEYS);
            int parameterIndex = 0;
            statement.setLong(++parameterIndex, book.getUserId());
            statement.setLong(++parameterIndex, book.getBarId());
            statement.setInt(++parameterIndex, book.getPlaces());
            statement.setDate(++parameterIndex, book.getDate());
            statement.executeUpdate();
            logger.info(String.format("Created book: %s", book));
            resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DaoException(String.format("Cannot insert new book: %s", book), e);
        } finally {
            close(statement);
            close(resultSet);
            if (!inTransaction) {
                close(connection);
            }
        }
    }

    @Override
    public Book update(Book book) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isUsersBookingFull(String login) throws DaoException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean flag = false;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(CHECK_BOOK_BY_LOGIN_SQL);
            statement.setString(1, login);
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                flag = resultSet.getInt(COINCIDENCES_RESULT_INDEX) < 2;
            }
        } catch (SQLException e) {
            throw new DaoException(String.format("Cannot check users book exists: %s", login), e);
        } finally {
            close(statement);
            if (!inTransaction) {
                close(connection);
            }
            close(resultSet);
        }
        return flag;
    }
}
