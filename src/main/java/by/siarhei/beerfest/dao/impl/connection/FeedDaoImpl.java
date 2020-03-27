package by.siarhei.beerfest.dao.impl.connection;

import by.siarhei.beerfest.dao.api.FeedDao;
import by.siarhei.beerfest.dao.transaction.DaoManager;
import by.siarhei.beerfest.datasource.CustomDataSourceBasedConnectionPool;
import by.siarhei.beerfest.entity.impl.Article;
import by.siarhei.beerfest.exception.DaoException;
import by.siarhei.beerfest.factory.impl.EntityBuilder;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedDaoImpl extends DaoManager implements FeedDao {
    private static final String INSERT_NEWS_SQL = "INSERT INTO feed (news_title, news_text, news_img_src) VALUES (?,?,?)";
    private static final String DELETE_NEW_BY_ID_SQL = "DELETE FROM feed WHERE feed.id = %s";
    private static final String SQL_SELECT_ALL_FEED =
            "SELECT id, news_title, news_text, news_img_src " +
                    "FROM feed";

    public FeedDaoImpl() {
    }

    public FeedDaoImpl(CustomDataSourceBasedConnectionPool connectionPool, EntityBuilder builder) {
        super(connectionPool, builder);
    }

    @Override
    public List<Article> findAll() throws DaoException {
        List<Article> list = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;
        EntityBuilder builder = getBuilder();
        try {
            connection = getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery(SQL_SELECT_ALL_FEED);
            while (resultSet.next()) {
                int index = 0;
                int id = resultSet.getInt(++index);
                String title = resultSet.getString(++index);
                String text = resultSet.getString(++index);
                String imgSrc = resultSet.getString(++index);
                list.add(builder.buildArticle(id, title, text, imgSrc));
            }
        } catch (SQLException e) {
            throw new DaoException("Cant update new list", e);
        } finally {
            if (!inTransaction) {
                close(connection);
            }
            close(statement);
            close(resultSet);
        }
        return list;
    }

    @Override
    public Article findEntity(Long id) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Article article) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) throws DaoException {
        Connection connection = null;
        Statement statement = null;
        try {
            connection = getConnection();
            statement = connection.createStatement();
            statement.execute(String.format(DELETE_NEW_BY_ID_SQL, id));
        } catch (SQLException e) {
            throw new DaoException("Cannot delete news", e);
        } finally {
            if (!inTransaction) {
                close(connection);
            }
            close(statement);
        }
    }

    @Override
    public long create(Article article) throws DaoException {
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            connection = getConnection();
            statement = connection.prepareStatement(INSERT_NEWS_SQL, Statement.RETURN_GENERATED_KEYS);
            int index = 0;
            statement.setString(++index, article.getTitle());
            statement.setString(++index, article.getText());
            statement.setString(++index, article.getImgSrc());
            statement.execute();
            logger.info(String.format("Created news: %s", article));
            resultSet = statement.getGeneratedKeys();
            resultSet.next();
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new DaoException(String.format("Cannot insert new news: %s", article), e);
        } finally {
            close(statement);
            close(resultSet);
            if (!inTransaction) {
                close(connection);
            }
        }
    }

    @Override
    public Article update(Article article) {
        throw new UnsupportedOperationException();
    }
}
