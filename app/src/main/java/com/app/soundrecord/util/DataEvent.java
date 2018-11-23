package com.app.soundrecord.util;

public class DataEvent {
    private String msg;
    private String size;

    public String getSize() {
        return size;
    }

    public DataEvent(String msg) {
        this.msg = msg;
    }
    public DataEvent(String msg,String size) {
        this.msg = msg;
        this.size=size;
    }
    public String getMsg(){
        return this.msg;
    }
}
