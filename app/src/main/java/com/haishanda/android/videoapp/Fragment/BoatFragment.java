package com.haishanda.android.videoapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.haishanda.android.videoapp.activity.AddBoatActivity;
import com.haishanda.android.videoapp.activity.BoatConfigActivity;
import com.haishanda.android.videoapp.adapter.LiveAdapter;
import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.bean.BoatMessage;
import com.haishanda.android.videoapp.bean.MonitorConfigBean;
import com.haishanda.android.videoapp.bean.QueryCameras;
import com.haishanda.android.videoapp.bean.QueryMachines;
import com.haishanda.android.videoapp.bean.TimeBean;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.utils.DaoUtil;
import com.haishanda.android.videoapp.views.spinner.NiceSpinner;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.BoatMessageDao;
import com.haishanda.android.videoapp.greendao.gen.MonitorConfigBeanDao;
import com.haishanda.android.videoapp.greendao.gen.TimeBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;


/**
 * 船舶首页
 * Created by Zhongsz on 2016/10/14.
 */

public class BoatFragment extends Fragment {
    @BindView(R.id.spinner_index_choices)
    NiceSpinner boatSpinner;
    @BindView(R.id.live_adapter_fields)
    GridView liveAdapterFields;
    @BindColor(R.color.titleBlue)
    int titleBlue;
    @BindColor(R.color.light_black)
    int black;
    @BindViews({R.id.boat_background, R.id.boat_not_add_text, R.id.add_boat_btn_big})
    View[] views;

    private static final String Tag = "船舶首页";
    private final String ADD_BOAT = "添加船舶";
    private int machineId;
    private String netModuleSerialNumber;
    private Map<String, Integer> boatInfos;
    private Map<String, String> boatNetModuleSerialNumbers;
    private SparseArray<String> supportArray;
    private List<String> boatLists;
    LiveAdapter adapter;
    private BoatMessageDao boatMessageDao;
    private List<Long> cameraList;
    private String[] cameraIconsPaths;


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
        handleSpinner();
        handleLiveAdapter();
    }

    private void handleSpinner() {
        BoatThread boatThread = new BoatThread();
        boatThread.start();
        try {
            boatThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        List<String> list = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : boatInfos.entrySet()
                ) {
            list.add(entry.getKey());
        }
        list.add(list.size(), ADD_BOAT);
        //设置当前船舶名称
        if (VideoApplication.getApplication().getCurrentBoatName() == null) {
            VideoApplication.getApplication().setCurrentBoatName(list.get(0));
        }
        if (VideoApplication.getApplication().getCurrentMachineId() == 0) {
            if (!list.get(0).equals(ADD_BOAT)) {
                VideoApplication.getApplication().setCurrentMachineId(boatInfos.get(list.get(0)));
            } else {
                VideoApplication.getApplication().setCurrentMachineId(-1);
            }
        }

        boatLists = list;

        //如果船舶列表不只是包括“添加船舶”，则不显示无船舶的背景图
        if (list.size() > 1) {
            for (View view : views) {
                view.setVisibility(View.INVISIBLE);
                view.setEnabled(false);
            }
        }
        initSpinner();
        if (supportArray != null) {
            String localBoatName = VideoApplication.getApplication().getCurrentBoatName();
            String remoteBoatName = supportArray.get(VideoApplication.getApplication().getCurrentMachineId());
            if (!localBoatName.equals(remoteBoatName) && remoteBoatName != null) {
                if (!localBoatName.equals(ADD_BOAT)) {
                    Toast.makeText(getContext(), "服务器数据发生变化，数据更新中", Toast.LENGTH_LONG).show();
                    DaoUtil.renameBoat(remoteBoatName, localBoatName, VideoApplication.getApplication().getCurrentMachineId());
                    VideoApplication.getApplication().setCurrentBoatName(remoteBoatName);
                }
            }
        }
        try {
            machineId = boatInfos.get(boatSpinner.getText().toString());
            netModuleSerialNumber = boatNetModuleSerialNumbers.get(boatSpinner.getText().toString());
        } catch (NullPointerException e) {
            Log.d(Tag, e.toString());
        }
    }

    private void initSpinner() {
        boatSpinner.attachDataSource(boatLists);
        boatSpinner.setSelectedIndex(VideoApplication.getApplication().getSelectedId());
        boatSpinner.setBackgroundColor(titleBlue);
        boatSpinner.setTextColor(Color.WHITE);
        boatSpinner.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        boatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 1) {
                    if (!parent.getItemAtPosition(position - 1).toString().equals(ADD_BOAT)) {
                        machineId = boatInfos.get(parent.getItemAtPosition(position - 1).toString());
                        netModuleSerialNumber = boatNetModuleSerialNumbers.get(parent.getItemAtPosition(position - 1).toString());
                        VideoApplication.getApplication().setCurrentBoatName(boatLists.get(position));
                        VideoApplication.getApplication().setCurrentMachineId(machineId);
                        VideoApplication.getApplication().setSelectedId(position);
                        handleLiveAdapter();
                    } else {
                        Intent intent = new Intent(getActivity(), AddBoatActivity.class);
                        startActivity(intent);
                    }
                } else {
                    if (!parent.getItemAtPosition(position).toString().equals(ADD_BOAT)) {
                        machineId = boatInfos.get(parent.getItemAtPosition(position).toString());
                        netModuleSerialNumber = boatNetModuleSerialNumbers.get(parent.getItemAtPosition(position).toString());
                        VideoApplication.getApplication().setCurrentBoatName(boatLists.get(position));
                        VideoApplication.getApplication().setCurrentMachineId(machineId);
                        VideoApplication.getApplication().setSelectedId(position);
                        handleLiveAdapter();
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
    }

    @OnClick({R.id.add_boat_btn, R.id.add_boat_btn_big})
    public void addBoat() {
        Intent intent = new Intent(getActivity(), AddBoatActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @OnClick(R.id.boat_config_btn)
    public void skipToBoatConfigActivity() {
        if (boatLists.size() > 1) {
            Intent intent = new Intent(getActivity(), BoatConfigActivity.class);
            intent.putExtra("machineId", machineId);
            intent.putExtra("boatName", VideoApplication.getApplication().getCurrentBoatName());
            intent.putExtra("netModuleSerialNumber", netModuleSerialNumber);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }
    }

    private Map<String, Integer> getBoatInfos() {
        Map<String, Integer> boatInfos = new HashMap<>();
        Map<String, String> boatNetModuleSerialNumber = new HashMap<>();
        SparseArray<String> array = new SparseArray<>();
        Call<SmartResult<List<QueryMachines>>> call = ApiManage.getInstence().getBoatApiService().queryMachinesCopy();
        try {
            Response<SmartResult<List<QueryMachines>>> response = call.execute();
            if (response.body().getData() != null && response.body().getCode() == 1) {
                for (QueryMachines queryMachines : response.body().getData()
                        ) {
                    //配置监控设置页面的数据
                    MonitorConfigBeanDao monitorConfigBeanDao = VideoApplication.getApplication().getDaoSession().getMonitorConfigBeanDao();
                    MonitorConfigBean monitorConfigBean;
                    if (queryMachines.isSwitchOn()) {
                        monitorConfigBean = new MonitorConfigBean(queryMachines.getId(), queryMachines.getName(), 1);
                    } else {
                        monitorConfigBean = new MonitorConfigBean(queryMachines.getId(), queryMachines.getName(), 0);
                    }
                    monitorConfigBeanDao.insertOrReplace(monitorConfigBean);
                    //计算监控时间段
                    int begin = (Integer.valueOf(queryMachines.getBegin()));
                    int span = (Integer.valueOf(queryMachines.getSpan()));
                    setMonitorTime(begin, span, queryMachines);
                    boatInfos.put(queryMachines.getName(), queryMachines.getId());
                    boatNetModuleSerialNumber.put(queryMachines.getName(), queryMachines.getSerialNo() != null ? queryMachines.getSerialNo() : "获取失败");
                    array.put(queryMachines.getId(), queryMachines.getName());
                }
                this.boatNetModuleSerialNumbers = boatNetModuleSerialNumber;
                this.supportArray = array;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return boatInfos;
    }

    private void setMonitorTime(int begin, int span, QueryMachines queryMachines) {
        TimeBeanDao timeBeanDao = VideoApplication.getApplication().getDaoSession().getTimeBeanDao();
        TimeBean timeBean;
        int beginHour;
        int beginMinute;
        int endHour;
        int endMinute;
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
    }

    private List<Long> getCameraList() {
        List<Long> cameraIds = new ArrayList<>();
        int machineId = this.machineId;
        Call<SmartResult<List<QueryCameras>>> call = ApiManage.getInstence().getBoatApiService().queryCamerasCopy(machineId);
        try {
            Response<SmartResult<List<QueryCameras>>> response = call.execute();
            if (response.body().getData() != null) {
                for (QueryCameras queryCamera : response.body().getData()
                        ) {
                    if (queryCamera != null) {
                        cameraIds.add(queryCamera.getId());
                    }
                }
            }
        } catch (IOException e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                Log.d(Tag, "网络连接中断");
            }
            e.printStackTrace();
        }

        BoatMessage boatMessage;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss", Locale.CHINA);
        Date curDate = new Date(System.currentTimeMillis());//获取当前时间
        String str = formatter.format(curDate);
        QueryBuilder<BoatMessage> builder = boatMessageDao.queryBuilder();
        List<BoatMessage> boats = builder.where(BoatMessageDao.Properties.MachineId.eq(machineId)).list();
        if (boats.size() == 0) {
            for (int i = 0; i < cameraIds.size(); i++) {
                boatMessage = new BoatMessage(machineId, boatSpinner.getText().toString(), cameraIds.get(i), "default", str, null);
                boatMessageDao.insertOrReplace(boatMessage);
            }
        }
        if (boats.size() < cameraIds.size()) {
            for (int i = boats.size(); i < cameraIds.size(); i++) {
                boatMessage = new BoatMessage(machineId, boatSpinner.getText().toString(), cameraIds.get(i), "default", str, null);
                boatMessageDao.insertOrReplace(boatMessage);
            }
        }

        return cameraIds;
    }

    private void handleLiveAdapter() {
        AdapterThread adapterThread = new AdapterThread();
        adapterThread.start();
        try {
            adapterThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        adapter = new LiveAdapter(getContext(), cameraIconsPaths, cameraList, VideoApplication.getApplication().getCurrentBoatName());
        adapter.notifyDataSetInvalidated();
        adapter.notifyDataSetChanged();
        liveAdapterFields.setAdapter(adapter);
    }

    private String[] getCameraImagePaths() {
        QueryBuilder<BoatMessage> queryBuilder = boatMessageDao.queryBuilder();
        List<BoatMessage> cameraDetails = queryBuilder.where(BoatMessageDao.Properties.MachineId.eq(machineId)).list();
        List<String> cameraImagePathsCopy = new ArrayList<>();
        for (int i = 0; i < cameraDetails.size(); i++) {
            cameraImagePathsCopy.add(cameraDetails.get(i).getCameraImagePath() != null ? cameraDetails.get(i).getCameraImagePath() : "default");
        }
        return cameraImagePathsCopy.toArray(new String[cameraImagePathsCopy.size()]);
    }

    class AdapterThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            cameraList = getCameraList();
            cameraIconsPaths = getCameraImagePaths();
        }
    }

    class BoatThread extends Thread {
        @Override
        public void run() {
            Looper.prepare();
            boatInfos = getBoatInfos();
        }
    }

}
