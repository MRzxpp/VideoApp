package com.haishanda.android.videoapp.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.haishanda.android.videoapp.greendao.gen.BoatMessageDao;
import com.haishanda.android.videoapp.utils.DaoUtil;
import com.haishanda.android.videoapp.views.spinner.NiceSpinner;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.MonitorConfigBeanDao;
import com.haishanda.android.videoapp.greendao.gen.TimeBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
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

import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


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
    @BindView(R.id.loading_boat_gif)
    ImageView loadingGif;

    private static final String TAG = "船舶首页";
    private final String ADD_BOAT = "添加船舶";
    private int machineId;
    private String netModuleSerialNumber;
    private Map<String, Integer> boatInfos;
    private Map<String, String> boatNetModuleSerialNumbers;
    private SparseArray<String> supportArray;
    private String[] boatLists;
    LiveAdapter adapter;
    private BoatMessageDao boatMessageDao;
    private QueryCameras[] cameraList;
    private String[] cameraIconsPaths;

    private BoatFragment instance;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boat, container, false);
        boatMessageDao = VideoApplication.getApplication().getDaoSession().getBoatMessageDao();
        ButterKnife.bind(this, view);
        if (instance == null) {
            synchronized (BoatFragment.class) {
                if (instance == null) {
                    instance = this;
                }
            }
        }
        Glide.with(instance)
                .load(R.drawable.loading)
                .asGif()
                .into(loadingGif);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        for (View v : views) {
            v.setVisibility(View.GONE);
            v.setEnabled(false);
        }
        boatSpinner.setVisibility(View.INVISIBLE);
        boatSpinner.setEnabled(false);
        loadingGif.setVisibility(View.VISIBLE);
        handleSpinner();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        instance = null;
    }

    private void handleSpinner() {
        getBoatInfos();
    }

    private void initSpinner() {
        boatSpinner.attachDataSource(Arrays.asList(boatLists));
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
                        VideoApplication.getApplication().setCurrentBoatName(boatLists[position]);
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
                        VideoApplication.getApplication().setCurrentBoatName(boatLists[position]);
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
        if (boatLists != null) {
            if (boatLists.length > 1) {
                Intent intent = new Intent(getActivity(), BoatConfigActivity.class);
                intent.putExtra("machineId", machineId);
                intent.putExtra("boatName", VideoApplication.getApplication().getCurrentBoatName());
                intent.putExtra("netModuleSerialNumber", netModuleSerialNumber);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
            }
        }
    }

    private void getBoatInfos() {
        final Map<String, Integer> boatInfos = new HashMap<>();
        final Map<String, String> boatNetModuleSerialNumber = new HashMap<>();
        final SparseArray<String> array = new SparseArray<>();
        ApiManage.getInstence().getBoatApiService().queryMachines()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult<List<QueryMachines>>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "获取船舶信息结束   ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "获取船舶信息失败   " + e.toString());
                        if (e instanceof SocketTimeoutException) {
                            Toast.makeText(instance.getActivity(), "连接服务器超时，请重试！", Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);
                            return;
                        }
                        if (e instanceof ConnectException) {
                            Toast.makeText(instance.getActivity(), "连接服务器失败，请重试！", Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);
                            return;
                        }
                        if (e instanceof IOException) {
                            Toast.makeText(instance.getActivity(), "连接服务器失败，请重试！", Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onNext(SmartResult<List<QueryMachines>> listSmartResult) {
                        if (listSmartResult.getData() != null && listSmartResult.getCode() == 1) {
                            boatSpinner.setVisibility(View.VISIBLE);
                            boatSpinner.setEnabled(true);
                            for (QueryMachines queryMachines : listSmartResult.getData()
                                    ) {
                                //配置监控设置页面的数据
                                MonitorConfigBeanDao monitorConfigBeanDao = VideoApplication.getApplication().getDaoSession().getMonitorConfigBeanDao();
                                MonitorConfigBean monitorConfigBean = new MonitorConfigBean(queryMachines.getId(), queryMachines.getName(), queryMachines.isSwitchOn() ? 1 : 0);
                                monitorConfigBeanDao.insertOrReplace(monitorConfigBean);
                                //计算监控时间段
                                int begin = (Integer.valueOf(queryMachines.getBegin()));
                                int span = (Integer.valueOf(queryMachines.getSpan()));
                                setMonitorTime(begin, span, queryMachines);
                                boatInfos.put(queryMachines.getName(), queryMachines.getId());
                                boatNetModuleSerialNumber.put(queryMachines.getName(), queryMachines.getSerialNo() != null ? queryMachines.getSerialNo() : "获取失败");
                                array.put(queryMachines.getId(), queryMachines.getName());
                            }
                            instance.boatNetModuleSerialNumbers = boatNetModuleSerialNumber;
                            instance.supportArray = array;
                            instance.boatInfos = boatInfos;

                            String[] list = new String[boatInfos.size() + 1];
                            for (int i = 0; i < boatInfos.size(); i++) {
                                list[i] = boatInfos.keySet().toArray(new String[boatInfos.size()])[i];
                            }
//                            for (Map.Entry<String, Integer> entry : boatInfos.entrySet()
//                                    ) {
//                                list.add(entry.getKey());
//                            }
                            list[boatInfos.size()] = ADD_BOAT;
                            //设置当前船舶名称
                            if (VideoApplication.getApplication().getCurrentBoatName() == null) {
                                VideoApplication.getApplication().setCurrentBoatName(list[0]);
                            }
                            if (VideoApplication.getApplication().getCurrentMachineId() == 0) {
                                if (!list[0].equals(ADD_BOAT)) {
                                    VideoApplication.getApplication().setCurrentMachineId(boatInfos.get(list[0]));
                                } else {
                                    VideoApplication.getApplication().setCurrentMachineId(-1);
                                }
                            }

                            boatLists = list;

                            //如果船舶列表不只是包括“添加船舶”，则不显示无船舶的背景图
                            if (list.length <= 1) {
                                for (View view : views) {
                                    view.setVisibility(View.VISIBLE);
                                    view.setEnabled(true);
                                }
                            }
                            getActivity().runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    initSpinner();
                                }
                            });
                            if (supportArray != null) {
                                String localBoatName = VideoApplication.getApplication().getCurrentBoatName();
                                String remoteBoatName = supportArray.get(VideoApplication.getApplication().getCurrentMachineId());
                                if (!localBoatName.equals(remoteBoatName) && remoteBoatName != null) {
                                    if (!localBoatName.equals(ADD_BOAT)) {
                                        DaoUtil.renameBoat(remoteBoatName, localBoatName, VideoApplication.getApplication().getCurrentMachineId());
                                        Toast.makeText(getContext(), "服务器数据发生变化，数据已更新", Toast.LENGTH_LONG).show();
                                        VideoApplication.getApplication().setCurrentBoatName(remoteBoatName);
                                    }
                                }
                            }
                            try {
                                machineId = boatInfos.get(boatSpinner.getText().toString());
                                netModuleSerialNumber = boatNetModuleSerialNumbers.get(boatSpinner.getText().toString());
                            } catch (NullPointerException e) {
                                Log.d(TAG, e.toString());
                            }
                            handleLiveAdapter();
                        } else {
                            Toast.makeText(instance.getActivity(), listSmartResult.getMsg() != null ? listSmartResult.getMsg() : "连接错误", Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);
                        }
                    }
                });
    }

    private void setMonitorTime(int begin, int span, QueryMachines queryMachines) {
        TimeBeanDao timeBeanDao = VideoApplication.getApplication().getDaoSession().getTimeBeanDao();
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
        TimeBean timeBean = new TimeBean(beginHour, beginMinute, endHour, endMinute, queryMachines.getId());
        timeBeanDao.insertOrReplace(timeBean);
    }

    private void getCameraList() {

        ApiManage.getInstence().getBoatApiService().queryCameras(machineId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult<List<QueryCameras>>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "获取摄像头失败   " + e.toString());
                        if (e instanceof SocketTimeoutException) {
                            Toast.makeText(instance.getActivity(), "连接服务器超时，请重试！", Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);
                            return;
                        }
                        if (e instanceof ConnectException) {
                            Toast.makeText(instance.getActivity(), "连接服务器失败，请重试！", Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);
                            return;
                        }
                        if (e instanceof IOException) {
                            Toast.makeText(instance.getActivity(), "连接服务器失败，请重试！", Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onNext(SmartResult<List<QueryCameras>> listSmartResult) {
                        if (listSmartResult.getData() != null && listSmartResult.getCode() == 1) {
                            loadingGif.setVisibility(View.GONE);
                            cameraList = new QueryCameras[listSmartResult.getData().size()];
                            for (int i = 0; i < listSmartResult.getData().size(); i++
                                    ) {
                                if (listSmartResult.getData().get(i) != null) {
                                    instance.cameraList[i] = listSmartResult.getData().get(i);
                                }
                            }

                            BoatMessage boatMessage;
                            SimpleDateFormat formatter = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss", Locale.CHINA);
                            Date curDate = new Date(System.currentTimeMillis());//获取当前时间
                            String str = formatter.format(curDate);
                            QueryBuilder<BoatMessage> builder = boatMessageDao.queryBuilder();
                            List<BoatMessage> boats = builder.where(BoatMessageDao.Properties.MachineId.eq(machineId)).list();
                            if (boats.size() == 0) {
                                for (int i = 0; i < instance.cameraList.length; i++) {
                                    boatMessage = new BoatMessage(machineId, boatSpinner.getText().toString(), instance.cameraList[i].getId(), "default", str, null);
                                    boatMessageDao.insertOrReplace(boatMessage);
                                }
                            }
                            if (boats.size() != 0 && (boats.size() < instance.cameraList.length)) {
                                for (int i = boats.size(); i < instance.cameraList.length; i++) {
                                    boatMessage = new BoatMessage(machineId, boatSpinner.getText().toString(), instance.cameraList[i].getId(), "default", str, null);
                                    boatMessageDao.insertOrReplace(boatMessage);
                                }
                            }
                        } else {
                            Toast.makeText(instance.getActivity(), listSmartResult.getMsg() != null ? listSmartResult.getMsg() : "连接错误", Toast.LENGTH_LONG).show();
                            loadingGif.setVisibility(View.INVISIBLE);
                        }

                        getCameraImagePaths();
                        adapter = new LiveAdapter(getContext(), cameraIconsPaths, cameraList, VideoApplication.getApplication().getCurrentBoatName());
                        adapter.notifyDataSetInvalidated();
                        adapter.notifyDataSetChanged();
                        liveAdapterFields.setAdapter(adapter);

                    }
                });


    }

    private void handleLiveAdapter() {
        getCameraList();
    }

    private void getCameraImagePaths() {
        QueryBuilder<BoatMessage> queryBuilder = boatMessageDao.queryBuilder();
        List<BoatMessage> cameraDetails = queryBuilder.where(BoatMessageDao.Properties.MachineId.eq(machineId)).list();
        List<String> cameraImagePathsCopy = new ArrayList<>();
        for (int i = 0; i < cameraDetails.size(); i++) {
            cameraImagePathsCopy.add(cameraDetails.get(i).getCameraImagePath() != null ? cameraDetails.get(i).getCameraImagePath() : "default");
        }
        cameraIconsPaths = cameraImagePathsCopy.toArray(new String[cameraImagePathsCopy.size()]);
    }

}
