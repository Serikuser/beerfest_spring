package by.siarhei.beerfest.dao.impl;

import by.siarhei.beerfest.config.SpringAppContext;
import by.siarhei.beerfest.dao.BarDao;
import by.siarhei.beerfest.entity.impl.Bar;
import by.siarhei.beerfest.exception.DaoException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BarDaoJdbcTemplateBased implements BarDao {
    private static final String INSERT_BAR_SQL = "INSERT INTO bar (account_id,name,description,food_id,beer_id ,places) VALUES (?,?,?,?,?,?)";
    private static final String INSERT_BEER_SQL = "INSERT INTO beer (name) VALUES (?)";
    private static final String INSERT_FOOD_SQL = "INSERT INTO food (name) VALUES (?)";
    private static final String SELECT_BEER_ID_BY_NAME = "SELECT id FROM beer WHERE name=?";
    private static final String SELECT_FOOD_ID_BY_NAME = "SELECT id FROM food WHERE name=?";
    private static final String SELECT_ALL_FOOD = "SELECT food.id,food.name FROM food ";
    private static final String SELECT_ALL_BEER = "SELECT beer.id,beer.name FROM beer ";
    private static final String CHECK_BAR_BY_USER_LOGIN =
            "SELECT count(*) " +
                    "FROM bar " +
                    "INNER JOIN account " +
                    "ON bar.account_id = account.id " +
                    "WHERE account.login= ? ";
    private static final String SELECT_ALL_BAR_SQL =
            "SELECT bar.id,bar.account_id,bar.name,bar.description,bar.food_id,food.name,bar.beer_id,beer.name,bar.places " +
                    "FROM bar " +
                    "INNER JOIN beer " +
                    "ON bar.beer_id = beer.id " +
                    "INNER JOIN food " +
                    "ON bar.food_id = food.id ";
    private static final String SELECT_BAR_ID_BY_USER_ID_SQL =
            "SELECT bar.id " +
                    "FROM bar " +
                    "INNER JOIN account " +
                    "ON account.id = bar.account_id " +
                    "WHERE account.id = ?";


    private JdbcTemplate template;

    private BarDaoJdbcTemplateBased(JdbcTemplate template) {
        this.template = template;
    }

    @Override
    public long findBeerIdByName(String name) throws DaoException {
        try {
            return template.queryForObject(SELECT_BEER_ID_BY_NAME, new Object[]{name}, Integer.class);
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot find beer id by name: %s", name), e);
        }
    }

    @Override
    public long findFoodIdByName(String name) throws DaoException {
        try {
            return template.queryForObject(SELECT_FOOD_ID_BY_NAME, new Object[]{name}, Integer.class);
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot find food id by name: %s", name), e);
        }
    }

    @Override
    public boolean isUserSubmittedBar(String login) throws DaoException {
        try {
            return template.queryForObject(CHECK_BAR_BY_USER_LOGIN, new Object[]{login}, Integer.class) != 0;
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot check is user : %s submission exists", login), e);
        }
    }

    @Override
    public Map<Long, String> findAllFoodType() throws DaoException {
        Map<Long, String> foodMap;
        try {
            ResultSetExtractor<HashMap<Long, String>> resultSetExtractor = resultSet -> {
                HashMap<Long, String> result = new HashMap<>();
                while (resultSet.next()) {
                    int columnIndex = 0;
                    long foodId = resultSet.getLong(++columnIndex);
                    String foodName = resultSet.getString(++columnIndex);
                    result.put(foodId, foodName);
                }
                return result;
            };
            foodMap = template.query(SELECT_ALL_FOOD, resultSetExtractor);
        } catch (DataAccessException e) {
            throw new DaoException("Cant find food map ", e);
        }
        return foodMap;
    }

    @Override
    public Map<Long, String> findAllBeerType() throws DaoException {
        Map<Long, String> beerMap;
        try {
            ResultSetExtractor<HashMap<Long, String>> resultSetExtractor = resultSet -> {
                HashMap<Long, String> result = new HashMap<>();
                while (resultSet.next()) {
                    int columnIndex = 0;
                    long beerId = resultSet.getLong(++columnIndex);
                    String beerName = resultSet.getString(++columnIndex);
                    result.put(beerId, beerName);
                }
                return result;
            };
            beerMap = template.query(SELECT_ALL_BEER, resultSetExtractor);
        } catch (DataAccessException e) {
            throw new DaoException("Cant find beer map ", e);
        }
        return beerMap;
    }

    @Override
    public void submitBeer(String beerName) throws DaoException {
        try {
            template.update(INSERT_BEER_SQL, beerName);
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot insert new beer: %s", beerName), e);
        }
    }

    @Override
    public void submitFood(String foodName) throws DaoException {
        try {
            template.update(INSERT_FOOD_SQL, foodName);
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot insert new food: %s", foodName), e);
        }
    }

    @Override
    public long findBarByUserId(long userId) throws DaoException {
        try {
            return template.queryForObject(SELECT_BAR_ID_BY_USER_ID_SQL, new Object[]{userId}, Long.class);
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cant find bar id by user id %s", userId), e);
        }
    }

    @Override
    public List<Bar> findAll() throws DaoException {
        try {
            return template.query(SELECT_ALL_BAR_SQL,
                    (resultSet, rowNumber) -> {
                        Bar bar = (Bar) SpringAppContext.getBean("bar");
                        int index = 0;
                        long barId = resultSet.getLong(++index);
                        bar.setId(barId);
                        long accountId = resultSet.getLong(++index);
                        bar.setAccountId(accountId);
                        String name = resultSet.getString(++index);
                        bar.setName(name);
                        String description = resultSet.getString(++index);
                        bar.setDescription(description);
                        long foodId = resultSet.getLong(++index);
                        bar.setFoodId(foodId);
                        String foodName = resultSet.getString(++index);
                        bar.setFoodName(foodName);
                        long beerId = resultSet.getLong(++index);
                        bar.setBeerId(beerId);
                        String beerName = resultSet.getString(++index);
                        bar.setBeerName(beerName);
                        int places = resultSet.getInt(++index);
                        bar.setPlaces(places);
                        return bar;
                    });
        } catch (DataAccessException e) {
            throw new DaoException("Bar list cant be updated", e);
        }
    }

    @Override
    public Bar findEntity(Long id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean delete(Bar bar) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete(Long id) throws DaoException {
        throw new UnsupportedOperationException();
    }

    @Override
    public long create(Bar bar) throws DaoException {
        try {
            PreparedStatementCreator preparedStatementCreator = connection -> {
                PreparedStatement statement = connection.prepareStatement(INSERT_BAR_SQL, new String[]{"id"});
                int columnIndex = 0;
                statement.setLong(++columnIndex, bar.getAccountId());
                statement.setString(++columnIndex, bar.getName());
                statement.setString(++columnIndex, bar.getDescription());
                statement.setLong(++columnIndex, bar.getFoodId());
                statement.setLong(++columnIndex, bar.getBeerId());
                statement.setLong(++columnIndex, bar.getPlaces());
                return statement;
            };
            KeyHolder keyHolder = new GeneratedKeyHolder();
            template.update(preparedStatementCreator, keyHolder);
            return (int) keyHolder.getKey();
        } catch (DataAccessException e) {
            throw new DaoException(String.format("Cannot insert new bar: %s", bar), e);
        }
    }

    @Override
    public Bar update(Bar bar) throws DaoException {
        throw new UnsupportedOperationException();
    }
}
