package com.haishanda.android.videoapp.config;

import java.io.Serializable;

/**
 * Created by Zhongsz on 2016/10/10.
 */

public class SmartResult<T> implements Serializable {

    private static final long serialVersionUID = -7995819821085283079L;

    private int code;
    private String msg;
    private T data;

    public SmartResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public SmartResult(){}

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
