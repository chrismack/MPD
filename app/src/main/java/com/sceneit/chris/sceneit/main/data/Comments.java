package com.sceneit.chris.sceneit.main.data;

import java.util.Date;

/**
 * Created by Chris on 09/03/2018.
 */

public class Comments {

    private String uid;
    private String value;
    private Date timestamp;

    public Comments() {
    }

    public Comments(String uid, String value, Date timestamp) {
        this.uid = uid;
        this.value = value;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
