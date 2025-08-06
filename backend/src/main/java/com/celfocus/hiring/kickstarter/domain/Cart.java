package com.celfocus.hiring.kickstarter.domain;

import java.util.Date;
import java.util.List;

public class Cart<T extends CartItem> {
    private String userId;
    private List<T> items;
    private Date lastModified;


    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }
}
