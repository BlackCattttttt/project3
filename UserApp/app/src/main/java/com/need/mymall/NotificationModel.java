package com.need.mymall;

import java.util.Date;

class NotificationModel {
    private String title;
    private String body;
    private Date date;
    private boolean readed;

    public NotificationModel(String title, String body, Date date, boolean readed) {
        this.title = title;
        this.body = body;
        this.date = date;
        this.readed = readed;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isReaded() {
        return readed;
    }

    public void setReaded(boolean readed) {
        this.readed = readed;
    }
}