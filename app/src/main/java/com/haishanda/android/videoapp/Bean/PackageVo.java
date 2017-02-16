package com.haishanda.android.videoapp.Bean;


import java.math.BigDecimal;

/**
 * 套餐信息
 * Created by Zhongsz on 2016/11/29.
 */
public class PackageVo {
    private int machineId;
    private String machineName;
    private String packageName;
    private BigDecimal balance;
    private int monthRent;

    public PackageVo(int machineId, String machineName, String packageName, BigDecimal balance, int monthRent) {
        this.machineId = machineId;
        this.machineName = machineName;
        this.packageName = packageName;
        this.balance = balance;
        this.monthRent = monthRent;
    }

    public int getMachineId() {
        return machineId;
    }

    public void setMachineId(int machineId) {
        this.machineId = machineId;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public int getMonthRent() {
        return monthRent;
    }

    public void setMonthRent(int monthRent) {
        this.monthRent = monthRent;
    }
}
