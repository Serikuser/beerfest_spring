package by.siarhei.beerfest.factory.impl;

import by.siarhei.beerfest.entity.RoleType;
import by.siarhei.beerfest.entity.StatusType;
import by.siarhei.beerfest.entity.impl.*;
import by.siarhei.beerfest.factory.api.EntityFactory;

import java.sql.Date;
import java.sql.Timestamp;

public class EntityBuilder {

    private static final String ENTITY_TYPE_ARTICLE = "article";
    private static final String ENTITY_TYPE_BAR = "bar";
    private static final String ENTITY_TYPE_USER = "user";
    private static final String ENTITY_TYPE_BOOK = "book";
    private static final String ENTITY_TYPE_REGISTRATION = "registration";
    private EntityFactory factory;

    private EntityBuilder(EntityFactory factory) {
        this.factory = factory;
    }

    public Article buildArticle(long id, String title, String text, String imgSrc) {
        Article article = (Article) factory.produce(ENTITY_TYPE_ARTICLE);
        article.setId(id);
        article.setTitle(title);
        article.setText(text);
        article.setImgSrc(imgSrc);
        return article;
    }

    public Article buildArticle(String title, String text, String imgSrc) {
        Article article = (Article) factory.produce(ENTITY_TYPE_ARTICLE);
        article.setTitle(title);
        article.setText(text);
        article.setImgSrc(imgSrc);
        return article;
    }

    public Book buildBook(long bookId, String barName, int reservedPlaces, Date date) {
        Book book = (Book) factory.produce(ENTITY_TYPE_BOOK);
        book.setId(bookId);
        book.setBarName(barName);
        book.setPlaces(reservedPlaces);
        book.setDate(date);
        return book;
    }

    public Book buildBar(String userName, Date date, int reservedPlaces) {
        Book book = (Book) factory.produce(ENTITY_TYPE_BOOK);
        book.setUserName(userName);
        book.setDate(date);
        book.setPlaces(reservedPlaces);
        return book;
    }

    public Bar buildBar(long barId, long accountId, String name, String description, long foodId, String foodName, long beerId, String beerName, int places) {
        Bar bar = (Bar) factory.produce(ENTITY_TYPE_BAR);
        bar.setId(barId);
        bar.setAccountId(accountId);
        bar.setName(name);
        bar.setDescription(description);
        bar.setFoodId(foodId);
        bar.setFoodName(foodName);
        bar.setBeerId(beerId);
        bar.setBeerName(beerName);
        bar.setPlaces(places);
        return bar;
    }

    public Registration buildRegistration() {
        return (Registration) factory.produce(ENTITY_TYPE_REGISTRATION);
    }

    public Registration buildRegistration(long id, Timestamp date) {
        Registration registration = (Registration) factory.produce(ENTITY_TYPE_REGISTRATION);
        registration.setId(id);
        registration.setDate(date);
        return registration;
    }

    public Registration buildRegistration(long id, long accountId, String token, boolean isExpired) {
        Registration registration = (Registration) factory.produce(ENTITY_TYPE_REGISTRATION);
        registration.setId(id);
        registration.setAccountId(accountId);
        registration.setToken(token);
        registration.setExpired(isExpired);
        return registration;
    }

    public User buildUser() {
        return (User) factory.produce(ENTITY_TYPE_USER);
    }

    public User buildUser(long id, String login, String eMail, String role, String status) {
        User user = (User) factory.produce(ENTITY_TYPE_USER);
        user.setId(id);
        user.setLogin(login);
        user.setEmail(eMail);
        user.setRole(RoleType.valueOf(role.toUpperCase()));
        user.setStatus(StatusType.valueOf(status.toUpperCase()));
        return user;
    }

    public User buildUser(String login, String password, String email, String avatarUrl, RoleType role, StatusType status) {
        User user = (User) factory.produce(ENTITY_TYPE_USER);
        user.setLogin(login);
        user.setPassword(password);
        user.setEmail(email);
        user.setAvatarUrl(avatarUrl);
        user.setRole(role);
        user.setStatus(status);
        return user;
    }

    public User buildUser(long id, String userLogin, String password, String eMail, String avatarUrl, String role, String status) {
        User user = (User) factory.produce(ENTITY_TYPE_USER);
        user.setId(id);
        user.setLogin(userLogin);
        user.setPassword(password);
        user.setEmail(eMail);
        user.setAvatarUrl(avatarUrl);
        user.setRole(RoleType.valueOf(role.toUpperCase()));
        user.setStatus(StatusType.valueOf(status.toUpperCase()));
        return user;
    }
}
