package com.haishanda.android.videoapp.Bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Unique;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by Zhongsz on 2016/12/3.
 */
@Entity
public class AlarmNum {
    @Unique
    int alarmNum;

    @Generated(hash = 1822350878)
    public AlarmNum(int alarmNum) {
        this.alarmNum = alarmNum;
    }

    @Generated(hash = 730345117)
    public AlarmNum() {
    }

    public int getAlarmNum() {
        return this.alarmNum;
    }

    public void setAlarmNum(int alarmNum) {
        this.alarmNum = alarmNum;
    }
}
