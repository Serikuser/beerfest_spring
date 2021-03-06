package by.siarhei.beerfest.dao.impl.template;

import by.siarhei.beerfest.dao.api.FeedDao;
import by.siarhei.beerfest.entity.impl.Article;
import by.siarhei.beerfest.exception.DaoException;
import by.siarhei.beerfest.factory.impl.EntityBuilder;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.List;

public class FeedDaoJdbcTemplateBased implements FeedDao {
    private static final String INSERT_NEWS_SQL = "INSERT INTO feed (news_title, news_text, news_img_src) VALUES (?,?,?)";
    private static final String DELETE_NEWS_BY_ID_SQL = "DELETE FROM feed WHERE feed.id = ?";
    private static final String SQL_SELECT_ALL_FEED =
            "SELECT id, news_title, news_text, news_img_src " +
                    "FROM feed";

    private JdbcTemplate template;
    private EntityBuilder builder;

    private FeedDaoJdbcTemplateBased(JdbcTemplate template, EntityBuilder builder) {
        this.template = template;
        this.builder = builder;
    }

    @Override
    public List<Article> findAll() throws DaoException {
        try {
            return template.query(SQL_SELECT_ALL_FEED,
                    (resultSet, rowNumber) -> {
                        int columnIndex = 0;
                        int id = resultSet.getInt(++columnIndex);
                        String title = resultSet.getString(++columnIndex);
                        String text = resultSet.getString(++columnIndex);
                        String imgSrc = resultSet.getString(++columnIndex);
                        return builder.buildArticle(id, title, text, imgSrc);
                    });
        } catch (DataAccessException e) {
            throw new DaoException("Cant update new list", e);

        }
    }

    @Override
    public Article findEntity(Long id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Article article) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) throws DaoException {
        try {
            template.update(DELETE_NEWS_BY_ID_SQL, id);
        } catch (DataAccessException e) {
            throw new DaoException("Cannot delete news", e);

        }
    }

    @Override
    public long create(Article article) throws DaoException {
        try {
            PreparedStatementCreator preparedStatementCreator = connection -> {
                PreparedStatement statement = connection.prepareStatement(INSERT_NEWS_SQL, new String[]{"id"});
                int columnIndex = 0;
                statement.setString(++columnIndex, article.getTitle());
                statement.setString(++columnIndex, article.getText());
                statement.setString(++columnIndex, article.getImgSrc());
                return statement;
            };
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(preparedStatementCreator, keyHolder);
            return (int) keyHolder.getKey();
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot insert new news: %s", article), e);
        }
    }

    @Override
    public Article update(Article article) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
