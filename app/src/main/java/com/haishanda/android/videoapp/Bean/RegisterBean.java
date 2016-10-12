package com.haishanda.android.videoapp.Bean;

/**
 * Created by Zhongsz on 2016/10/10.
 */

public class RegisterBean {
    private String mobileNo;
    private String password;
    private String code;

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String fetchCode) {
        this.code = fetchCode;
    }
}
