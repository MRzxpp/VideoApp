package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.haishanda.android.videoapp.R;
import com.kyleduo.switchbutton.SwitchButton;


/**
 * Created by Zhongsz on 2016/12/8.
 */

public class MonitorConfigAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private String[] boatNames;
    private int[] machineIds;

    public MonitorConfigAdapter(Context context, String[] boatNames, int[] machineIds) {
        super(context, R.layout.adapter_monitor_config, boatNames);
        this.context = context;
        this.boatNames = boatNames;
        this.machineIds = machineIds;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_monitor_config, parent, false);
        }
        RelativeLayout monitorTime = (RelativeLayout) convertView.findViewById(R.id.monitor_time);
        SwitchButton monitorOpen = (SwitchButton) convertView.findViewById(R.id.is_monitor_open);
        RelativeLayout monitorVoice = (RelativeLayout) convertView.findViewById(R.id.monitor_voice);
        TextView monitorBoatname = (TextView) convertView.findViewById(R.id.monitor_boatname);
        monitorBoatname.setText(boatNames[position]);
        monitorTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });


        return convertView;
    }
}
