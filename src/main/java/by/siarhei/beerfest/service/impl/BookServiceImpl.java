package by.siarhei.beerfest.service.impl;

import by.siarhei.beerfest.dao.BookDao;
import by.siarhei.beerfest.entity.impl.Book;
import by.siarhei.beerfest.exception.DaoException;
import by.siarhei.beerfest.exception.ServiceException;
import by.siarhei.beerfest.service.BookService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Date;
import java.util.List;

public class BookServiceImpl implements BookService {
    private static final Logger logger = LogManager.getLogger();

    private BookDao bookDao;

    private BookServiceImpl(BookDao bookDao) {
        this.bookDao = bookDao;
    }

    @Override
    public boolean checkUserBook(String login) throws ServiceException {
        try {
            return bookDao.isUsersBookingFull(login);
        } catch (DaoException e) {
            logger.error("Cannot check user: %s books throws exception: %s", e);
            throw new ServiceException(e);
        }
    }

    @Override
    public void makeBook(long accountId, long barId, int places, Date date) throws ServiceException {
        Book book = new Book();
        book.setUserId(accountId);
        book.setBarId(barId);
        book.setPlaces(places);
        book.setDate(date);
        try {
            bookDao.create(book);
        } catch (DaoException e) {
            logger.error("Cannot make book", e);
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Book> findUserBook(Long id) throws ServiceException {
        try {
            return bookDao.findUserBook(id);
        } catch (DaoException e) {
            logger.error("Cannot find book ", e);
            throw new ServiceException(e);
        }
    }

    @Override
    public List<Book> finBarBook(Long id) throws ServiceException {
        try {
            return bookDao.findBarBook(id);
        } catch (DaoException e) {
            logger.error("Cannot find book", e);
            throw new ServiceException(e);
        }
    }

    @Override
    public void deleteBook(long bookId) throws ServiceException {
        try {
            bookDao.delete(bookId);
        }
        catch (DaoException e){
            logger.error("Cannot delete book ", e);
            throw new ServiceException(e);
        }
    }
}
