package by.siarhei.beerfest.dao.impl.template;

import by.siarhei.beerfest.config.SpringAppContext;
import by.siarhei.beerfest.dao.api.BookDao;
import by.siarhei.beerfest.entity.impl.Book;
import by.siarhei.beerfest.exception.DaoException;
import by.siarhei.beerfest.factory.impl.EntityBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.util.List;

public class BookDaoJdbcTemplateBased implements BookDao {
    private static final String INSERT_BOOK_SQL = "INSERT INTO book (guest_id,bar_id,reserved_places,reservation_date) VALUES (?,?,?,?)";
    private static final String CHECK_BOOK_BY_LOGIN_SQL =
            "SELECT count(*) " +
                    "FROM book " +
                    "INNER JOIN account " +
                    "ON book.guest_id = account.id " +
                    "WHERE account.login= ?";
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
    private static final String DELETE_BOOK_BY_ID_SQL = "DELETE FROM book WHERE book.id = ?";


    private JdbcTemplate template;
    private EntityBuilder builder;

    private BookDaoJdbcTemplateBased(JdbcTemplate template, EntityBuilder builder) {
        this.template = template;
        this.builder = builder;
    }

    @Override
    public List<Book> findUserBook(Long id) throws DaoException {
        try {
            return template.query(SELECT_BOOK_BY_USER_ID_SQL, new Object[]{id},
                    ((resultSet, rowNumber) -> {
                        int columnIndex = 0;
                        long bookId = resultSet.getLong(++columnIndex);
                        String barName = resultSet.getString(++columnIndex);
                        int reservedPlaces = resultSet.getInt(++columnIndex);
                        Date date = resultSet.getDate(++columnIndex);
                        return builder.buildBook(bookId, barName, reservedPlaces, date);
                    }));
        } catch (DataAccessException e) {
            throw new DaoException("Cannot find user book ", e);
        }
    }

    @Override
    public List<Book> findBarBook(Long id) throws DaoException {
        try {
            return template.query(SELECT_BOOK_BY_BAR_ID_SQL, new Object[]{id},
                    ((resultSet, rowNumber) -> {
                        Book book = (Book) SpringAppContext.getBean("book");
                        int columnIndex = 0;
                        long bookId = resultSet.getLong(++columnIndex);
                        book.setId(bookId);
                        String barName = resultSet.getString(++columnIndex);
                        book.setBarName(barName);
                        int reservedPlaces = resultSet.getInt(++columnIndex);
                        book.setPlaces(reservedPlaces);
                        Date date = resultSet.getDate(++columnIndex);
                        book.setDate(date);
                        return builder.buildBook(bookId, barName, reservedPlaces, date);
                    }));
        } catch (DataAccessException e) {
            throw new DaoException("Cannot find bar book ", e);
        }
    }

    @Override
    public boolean isUsersBookingFull(String login) throws DaoException {
        try {
            return template.queryForObject(CHECK_BOOK_BY_LOGIN_SQL, new Object[]{login}, Integer.class) < 2;
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot check users book exists: %s", login), e);
        }
    }

    @Override
    public List<Book> findAll() throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public Book findEntity(Long id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Book book) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) throws DaoException {
        try {
            template.update(DELETE_BOOK_BY_ID_SQL, id);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot delete book", e);

        }
    }

    @Override
    public long create(Book book) throws DaoException {
        try {
            PreparedStatementCreator preparedStatementCreator = connection -> {
                PreparedStatement statement = connection.prepareStatement(INSERT_BOOK_SQL, new String[]{"id"});
                int columnIndex = 0;
                statement.setLong(++columnIndex, book.getUserId());
                statement.setLong(++columnIndex, book.getBarId());
                statement.setInt(++columnIndex, book.getPlaces());
                statement.setDate(++columnIndex, book.getDate());
                return statement;
            };
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(preparedStatementCreator, keyHolder);
            int id = (int) keyHolder.getKey();
            logger.info(String.format("Created book: %s with id: %s", book, id));
            return id;
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot insert new book: %s", book), e);
        }
    }

    @Override
    public Book update(Book book) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
