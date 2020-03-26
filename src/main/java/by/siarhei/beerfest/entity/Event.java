package by.siarhei.beerfest.entity;

import java.text.DateFormat;
import java.util.Date;
import java.util.Random;

public class Event {
    private long id;
    private String msg;
    private Date date;
    private DateFormat dateFormat;

    public Event(Date date, DateFormat dateFormat) {
        this.dateFormat = dateFormat;
        this.date = date;
        generateId();
    }

    private void generateId() {
        this.id = new Random().nextLong();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return String.format("Event with id: %s , message: %s on date: %s", id, msg, dateFormat.format(date));
    }
}
