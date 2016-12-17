package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Activity.AddBoatActivity;
import com.haishanda.android.videoapp.Activity.BoatConfigActivity;
import com.haishanda.android.videoapp.Activity.LoginActivity;
import com.haishanda.android.videoapp.Adapter.LiveAdapter;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.BoatMessage;
import com.haishanda.android.videoapp.Bean.MonitorConfigBean;
import com.haishanda.android.videoapp.Bean.QueryCameras;
import com.haishanda.android.videoapp.Bean.QueryMachines;
import com.haishanda.android.videoapp.Bean.TimeBean;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.niceSpinner.NiceSpinner;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.BoatMessageDao;
import com.haishanda.android.videoapp.greendao.gen.MonitorConfigBeanDao;
import com.haishanda.android.videoapp.greendao.gen.TimeBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;


/**
 * Created by Zhongsz on 2016/10/14.
 */

public class BoatFragment extends Fragment {
    @BindView(R.id.spinner_index_choices)
    NiceSpinner mySpinner;
    @BindView(R.id.live_adapter_fields)
    GridView liveAdapterFields;
    @BindColor(R.color.titleBlue)
    int titleBlue;
    @BindColor(R.color.light_black)
    int black;
    @BindViews({R.id.boat_background, R.id.boat_not_add_text, R.id.add_boat_btn_big})
    View[] views;


    private final String Tag = "船舶首页";
    private int machineId;
    private String globalId;
    private Map<String, Integer> boatInfos;
    private Map<String, String> boatGlobalIds;
    private List<Long> cameraIds;
    private List<String> boatLists;
    LiveAdapter adapter;
    private BoatMessageDao boatMessageDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boat, container, false);
        boatMessageDao = VideoApplication.getApplication().getDaoSession().getBoatMessageDao();
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        dealSpinner();
        setLiveAdapter();
    }


    private void dealSpinner() {
        boatInfos = getBoatInfos();
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : boatInfos.entrySet()
                ) {
            list.add(entry.getKey());
        }
        list.add(list.size(), "添加船舶");
        if (VideoApplication.getApplication().getCurrentBoatName() == null) {
            VideoApplication.getApplication().setCurrentBoatName(list.get(0));
        }
        if (VideoApplication.getApplication().getCurrentMachineId() == 0) {
            if (list.get(0) != "添加船舶") {
                VideoApplication.getApplication().setCurrentMachineId(boatInfos.get(list.get(0)));
            } else {
                VideoApplication.getApplication().setCurrentMachineId(-1);
            }
        }

        this.boatLists = list;

        if (list.size() > 1) {
            for (View view : views) {
                view.setVisibility(View.INVISIBLE);
                view.setEnabled(false);
            }
        }

        mySpinner.attachDataSource(list);
        mySpinner.setSelectedIndex(VideoApplication.getApplication().getSelectedId());
        mySpinner.setBackgroundColor(titleBlue);
        mySpinner.setTextColor(Color.WHITE);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 1) {
                    if (!parent.getItemAtPosition(position - 1).toString().equals("添加船舶")) {
                        machineId = boatInfos.get(parent.getItemAtPosition(position - 1).toString());
                        globalId = boatGlobalIds.get(parent.getItemAtPosition(position - 1).toString());
                        VideoApplication.getApplication().setCurrentBoatName(boatLists.get(position));
                        VideoApplication.getApplication().setCurrentMachineId(machineId);
                        VideoApplication.getApplication().setSelectedId(position);
                        setLiveAdapter();
                    } else {
                        Intent intent = new Intent(getActivity(), AddBoatActivity.class);
                        startActivity(intent);
                    }
                } else {
                    if (!parent.getItemAtPosition(position).toString().equals("添加船舶")) {
                        machineId = boatInfos.get(parent.getItemAtPosition(position).toString());
                        globalId = boatGlobalIds.get(parent.getItemAtPosition(position).toString());
                        VideoApplication.getApplication().setCurrentBoatName(boatLists.get(position));
                        VideoApplication.getApplication().setCurrentMachineId(machineId);
                        VideoApplication.getApplication().setSelectedId(position);
                        setLiveAdapter();
                    } else {
                        Intent intent = new Intent(getActivity(), AddBoatActivity.class);
                        startActivity(intent);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        try {
            machineId = boatInfos.get(mySpinner.getText().toString());
            globalId = boatGlobalIds.get(mySpinner.getText().toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
//            Toast.makeText(getContext(), "未获取到船舶列表，请检查", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick({R.id.add_boat_btn, R.id.add_boat_btn_big})
    public void addBoat() {
        Intent intent = new Intent(getActivity(), AddBoatActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.boat_config_btn)
    public void skipToBoatConfigActivity() {
        Intent intent = new Intent(getActivity(), BoatConfigActivity.class);
        intent.putExtra("machineId", machineId);
        intent.putExtra("boatName", mySpinner.getText().toString());
        intent.putExtra("globalId", globalId);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    private Map<String, Integer> getBoatInfos() {
        Map<String, Integer> boatInfos = new HashMap<String, Integer>();
        Map<String, String> boatGlobalIds = new HashMap<>();
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectDiskWrites()
                .detectNetwork()
                .penaltyLog()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                .detectLeakedSqlLiteObjects()
                .detectLeakedClosableObjects()
                .penaltyLog()
                .penaltyDeath()
                .build());
        Call<SmartResult<List<QueryMachines>>> call = ApiManage.getInstence().getBoatApiService().queryMachinesCopy();
        try {
            TimeBeanDao timeBeanDao = VideoApplication.getApplication().getDaoSession().getTimeBeanDao();
            TimeBean timeBean;
            int beginHour;
            int beginMinute;
            int endHour;
            int endMinute;
            Response<SmartResult<List<QueryMachines>>> response = call.execute();
            if (response.body().getCode() == 1) {
                for (QueryMachines queryMachines : response.body().getData()
                        ) {
                    //配置监控设置页面的数据
                    MonitorConfigBeanDao monitorConfigBeanDao = VideoApplication.getApplication().getDaoSession().getMonitorConfigBeanDao();
                    MonitorConfigBean monitorConfigBean = new MonitorConfigBean(queryMachines.getId(), queryMachines.getName(), queryMachines.isSwitchOn());
                    monitorConfigBeanDao.insertOrReplace(monitorConfigBean);
                    //计算监控时间段
                    int begin = (Integer.valueOf(queryMachines.getBegin()));
                    int span = (Integer.valueOf(queryMachines.getSpan()));
                    beginHour = begin / 60;
                    beginMinute = begin - 60 * beginHour;
                    if (begin + span < 1440) {
                        endHour = (begin + span) / 60;
                        endMinute = begin + span - (60 * endHour);
                    } else {
                        endHour = (begin + span - 1440) / 60;
                        endMinute = (begin + span - 1440) - (60 * endHour);
                    }
                    timeBean = new TimeBean(beginHour, beginMinute, endHour, endMinute, queryMachines.getId());
                    timeBeanDao.insertOrReplace(timeBean);
                    boatInfos.put(queryMachines.getName(), queryMachines.getId());
                    boatGlobalIds.put(queryMachines.getName(), queryMachines.getGlobalId());
                }
                this.boatGlobalIds = boatGlobalIds;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "请重新登录", Toast.LENGTH_LONG).show();
            FragmentManager fragmentManager = getFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.remove(this);
            fragmentTransaction.commit();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        return boatInfos;
    }

    public List<Long> getCamenraList() {
        List<Long> cameraIds = new ArrayList<Long>();
        int machineId = this.machineId;
        Call<SmartResult<List<QueryCameras>>> call = ApiManage.getInstence().getBoatApiService().queryCamerasCopy(machineId);
        try {
            Response<SmartResult<List<QueryCameras>>> response = call.execute();
            for (QueryCameras queryCamera : response.body().getData()
                    ) {
                cameraIds.add(queryCamera.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "请重新登录", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
        }

        this.cameraIds = cameraIds;

        BoatMessage boatMessage;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        QueryBuilder<BoatMessage> builder = boatMessageDao.queryBuilder();
        List<BoatMessage> boats = builder.where(BoatMessageDao.Properties.MachineId.eq(machineId)).list();
        if (boats.size() == 0) {
            for (int i = 0; i < cameraIds.size(); i++) {
                boatMessage = new BoatMessage(machineId, mySpinner.getText().toString(), cameraIds.get(i), "default", str, null);
                boatMessageDao.insertOrReplace(boatMessage);
            }
        }
        if (boats.size() < cameraIds.size()) {
            for (int i = boats.size(); i < cameraIds.size(); i++) {
                boatMessage = new BoatMessage(machineId, mySpinner.getText().toString(), cameraIds.get(i), "default", str, null);
                boatMessageDao.insertOrReplace(boatMessage);
            }
        }

        return cameraIds;
    }

    public void setLiveAdapter() {
        List<Long> cameraList = getCamenraList();
        String[] cameraIconsPaths = getCameraImagePaths();
        adapter = new LiveAdapter(getContext(), cameraIconsPaths, cameraList, VideoApplication.getApplication().getCurrentBoatName());
        adapter.notifyDataSetInvalidated();
        adapter.notifyDataSetChanged();
        liveAdapterFields.setAdapter(adapter);
    }

    private String[] getCameraImagePaths() {
        QueryBuilder<BoatMessage> queryBuilder = boatMessageDao.queryBuilder();
        List<BoatMessage> cameraDetails = queryBuilder.where(BoatMessageDao.Properties.MachineId.eq(machineId)).list();
        List<String> cameraImagePathsCopy = new ArrayList<String>();
        for (int i = 0; i < cameraDetails.size(); i++) {
            cameraImagePathsCopy.add(cameraDetails.get(i).getCameraImagePath() != null ? cameraDetails.get(i).getCameraImagePath() : "default");
        }
        return cameraImagePathsCopy.toArray(new String[cameraImagePathsCopy.size()]);
    }

}
