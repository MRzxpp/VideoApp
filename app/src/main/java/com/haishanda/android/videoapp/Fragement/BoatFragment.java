package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.util.TimeUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Activity.AddBoatActivity;
import com.haishanda.android.videoapp.Activity.BoatConfigActivity;
import com.haishanda.android.videoapp.Activity.PlayVideoActivity;
import com.haishanda.android.videoapp.Adapter.LiveAdapter;
import com.haishanda.android.videoapp.Adapter.PhotosAdapter;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.BoatMessage;
import com.haishanda.android.videoapp.Bean.QueryCameras;
import com.haishanda.android.videoapp.Bean.QueryMachines;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.niceSpinner.NiceSpinner;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.BoatMessageDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.IOException;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Set;

import butterknife.BindColor;
import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Response;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;


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
    private Map<String, Integer> boatInfos;
    private List<Integer> cameraIds;
    private List<String> boatLists;
    LiveAdapter adapter;
    private BoatMessageDao boatMessageDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_boat, container, false);
        boatMessageDao = VideoApplication.getApplication().getDaoSession().getBoatMessageDao();
        ButterKnife.bind(this, view);
        dealSpinner();
        getCameraImagePaths();
        setLiveAdapter();
        return view;
    }

    private void dealSpinner() {
        boatInfos = getBoatInfos();
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String, Integer> entry : boatInfos.entrySet()
                ) {
            list.add(entry.getKey());
        }
        list.add(list.size(), "添加船舶");
        this.boatLists = list;
        if (list.size() > 1) {
            for (View view : views) {
                view.setVisibility(View.INVISIBLE);
                view.setEnabled(false);
            }
        }

        mySpinner.attachDataSource(list);
        mySpinner.setBackgroundColor(titleBlue);
        mySpinner.setTextColor(Color.WHITE);
        mySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                machineId = boatInfos.get(mySpinner.getText().toString());
                adapter.notifyDataSetInvalidated();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        try {
            machineId = boatInfos.get(mySpinner.getText().toString());
        } catch (NullPointerException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "获取船舶列表失败，请检查", Toast.LENGTH_LONG).show();
        }
    }

    @OnClick({R.id.add_boat_btn, R.id.add_boat_btn_big})
    public void addBoat() {
        Intent intent = new Intent(getActivity(), AddBoatActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.boat_config_btn)
    public void skipToBoatConfigActivity() {
        Intent intent = new Intent(getActivity(), BoatConfigActivity.class);
        intent.putExtra("machineId", machineId);
        startActivity(intent);
    }

    private Map<String, Integer> getBoatInfos() {
        final Map<String, Integer> boatInfos = new HashMap<String, Integer>();
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
            Response<SmartResult<List<QueryMachines>>> response = call.execute();
            for (QueryMachines queryMachines : response.body().getData()
                    ) {
                boatInfos.put(queryMachines.getName(), queryMachines.getId());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        ApiManage.getInstence().getBoatApiService().queryMachines()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Observer<SmartResult<List<QueryMachines>>>() {
//                    @Override
//                    public void onCompleted() {
//                        Log.i(Tag, "get boatinfos successfully");
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                        Log.i(Tag, "get boatinfos error");
//                        e.printStackTrace();
//                    }
//
//                    @Override
//                    public void onNext(SmartResult<List<QueryMachines>> listSmartResult) {
//                        for (QueryMachines queryMachine :
//                                listSmartResult.getData()) {
//                            boatInfos.put(queryMachine.getName(), queryMachine.getId());
//                        }
//                    }
//                });
        return boatInfos;
    }

    public List<Integer> getCamenraList(int machineId) {
        final List<Integer> cameraIds = new ArrayList<Integer>();
        machineId = this.machineId;
        ApiManage.getInstence().getBoatApiService().queryCameras(machineId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult<List<QueryCameras>>>() {
                    @Override
                    public void onCompleted() {
                        Log.i(Tag, "get cameralist successfully");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.i(Tag, "get camera error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult<List<QueryCameras>> listSmartResult) {
                        for (QueryCameras queryCamera :
                                listSmartResult.getData()) {
                            cameraIds.add(queryCamera.getId());
                        }
                    }
                });
        this.cameraIds = cameraIds;
        BoatMessage boatMessage;
        for (int i = 0; i < cameraIds.size(); i++) {
            boatMessage = new BoatMessage(machineId, mySpinner.getText().toString(), cameraIds.get(i), null, android.text.format.Time.getCurrentTimezone(), null);
            boatMessageDao.insertOrReplace(boatMessage);
        }

        return cameraIds;
    }

    public void setLiveAdapter() {
        adapter = new LiveAdapter(getContext(), getCameraImagePaths(), getCamenraList(this.machineId), mySpinner.getText().toString());
        adapter.notifyDataSetInvalidated();
        adapter.notifyDataSetChanged();
        liveAdapterFields.setAdapter(adapter);
    }

    private String[] getCameraImagePaths() {
        QueryBuilder<BoatMessage> queryBuilder = boatMessageDao.queryBuilder();
        List<BoatMessage> cameraDetails = queryBuilder.where(BoatMessageDao.Properties.Name.eq(mySpinner.getText().toString())).list();
        List<String> cameraImagePathsCopy = new ArrayList<String>();
        for (int i = 0; i < cameraDetails.size(); i++) {
            cameraImagePathsCopy.add(cameraDetails.get(i).getCameraImagePath());
        }
        String[] cameraImagePaths = cameraImagePathsCopy.toArray(new String[cameraImagePathsCopy.size()]);
        return cameraImagePaths;
    }

}
