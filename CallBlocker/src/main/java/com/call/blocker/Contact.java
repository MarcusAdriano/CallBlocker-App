package com.call.blocker;

/**
 * Created by Marcus on 22/07/2016.
 */
public class Contact {
    private int id;
    private String number;
    private String description;
    private boolean smsBlocker;
    private boolean callBlocker;

    public Contact () {

    }

    public Contact(String description, String number, boolean callBlocker, boolean smsBlocker) {
        this.description = description;
        this.number = number;
        this.smsBlocker = smsBlocker;
        this.callBlocker = callBlocker;
    }

    public boolean isSmsBlocker() {
        return smsBlocker;
    }

    public void setSmsBlocker(boolean smsBlocker) {
        this.smsBlocker = smsBlocker;
    }

    public boolean isCallBlocker() {
        return callBlocker;
    }

    public void setCallBlocker(boolean callBlocker) {
        this.callBlocker = callBlocker;
    }

    @Override
    public String toString() {
        return this.description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
