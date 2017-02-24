package com.haishanda.android.videoapp.Fragement;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.Activity.MainActivity;
import com.haishanda.android.videoapp.Activity.MonitorConfigActivity;
import com.haishanda.android.videoapp.Activity.PlayMonitorPhotoActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.AlarmVoBean;
import com.haishanda.android.videoapp.Config.Constant;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Service.LoginService;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.AlarmVoBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.StringTokenizer;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * 监控界面
 * Created by Zhongsz on 2016/10/14.
 */

//Todo 报警信息过多时的分页处理

public class MonitorFragment extends Fragment {
    @BindView(R.id.monitor_messages)
    ListView monitorMessages;
    @BindView(R.id.edit_monitor_message)
    TextView editMonitorMessage;
    @BindView(R.id.edit_monitor_message_btns)
    RelativeLayout editBtns;

    private final String TAG = "获取报警信息";
    public List<AlarmVoBean> list = new ArrayList<>();
    private List<AlarmVoBean> chosenList = new ArrayList<>();
    private AlarmVoBeanDao alarmVoBeanDao;
    private SharedPreferences alarmPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_monitor, container, false);
        ButterKnife.bind(this, view);
        editBtns.setVisibility(View.INVISIBLE);
        editBtns.setEnabled(false);
        alarmPreferences = getActivity().getSharedPreferences(Constant.ALARM_MESSAGE, Context.MODE_PRIVATE);
        alarmVoBeanDao = VideoApplication.getApplication().getDaoSession().getAlarmVoBeanDao();
        QueryBuilder<AlarmVoBean> queryBuilder = alarmVoBeanDao.queryBuilder();
        list = queryBuilder.list();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (LoginService.getWarningTimer() != null) {
            LoginService.getWarningTimer().cancel();
            Log.d(TAG, "提醒关闭");
        }

    }

    @OnClick(R.id.edit_monitor_message)
    public void editMonitorMessage() {
        if (editMonitorMessage.getText().toString().equals("编辑")) {
            editBtns.setVisibility(View.VISIBLE);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.appear_from_bottom);
            editBtns.startAnimation(animation);
            initAdapter(true, false, false);
            editMonitorMessage.setText("取消");
            editBtns.setEnabled(true);
        } else {
            editBtns.setEnabled(false);
            Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.disappear_to_bottom);
            editBtns.startAnimation(animation);
            chosenList.clear();
            initAdapter(false, false, false);
            editMonitorMessage.setText("编辑");
            editBtns.setVisibility(View.INVISIBLE);
        }
    }

    @OnClick(R.id.delete_selected_messages)
    public void deleteChosenMessages() {
        list.removeAll(chosenList);
        for (AlarmVoBean a : chosenList
                ) {
            alarmVoBeanDao.delete(a);
        }
        chosenList.clear();
        initAdapter(true, false, false);
    }

    @OnClick(R.id.selectAll)
    public void selectAll() {
        chosenList.addAll(list);
        initAdapter(true, true, false);
    }

    @OnClick(R.id.selectNone)
    public void selectNone() {
        chosenList.clear();
        initAdapter(true, false, true);
    }


    @OnClick(R.id.monitor_config)
    public void skipToMonitorConfigFragment() {
        Intent intent = new Intent(getActivity(), MonitorConfigActivity.class);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public void onResume() {
        super.onResume();
        initAlarms();
        initAdapter(false, false, false);
        try {
            resetMessagesUnreadNum();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void resetMessagesUnreadNum() throws InterruptedException {
        SharedPreferences preferences = getActivity().getSharedPreferences(Constant.ALARM_MESSAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(Constant.ALARM_MESSAGE_NUMBER, 0);
        editor.apply();
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.refresh();
    }


    private void initAlarms() {
        int last = alarmPreferences.getInt(Constant.ALARM_MESSAGE_LAST_ID, 0);
        ApiManage.getInstence().getMonitorApiService().queryAlarms(last)
                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Observer<SmartResult<List<AlarmVoBean>>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult<List<AlarmVoBean>> listSmartResult) {
                        if (listSmartResult.getCode() == 1) {
                            for (AlarmVoBean a : listSmartResult.getData()
                                    ) {
                                AlarmVoBean alarmVoBean = new AlarmVoBean(a.getId(), a.getMachineName(), a.getUrls(), a.getAlarmTime());
                                alarmVoBeanDao.insertOrReplace(alarmVoBean);
                            }
                            SharedPreferences.Editor editor = alarmPreferences.edit();
                            if (listSmartResult.getData().size() != 0) {
                                editor.putInt(Constant.ALARM_MESSAGE_LAST_ID, (int) listSmartResult.getData().get(listSmartResult.getData().size() - 1).getId());
                            }
                            editor.apply();
                            Log.d(TAG, "success");
                        } else {
                            Log.d(TAG, "failed");
                            Toast.makeText(getContext(), listSmartResult.getMsg() != null ? listSmartResult.getMsg() : "获取报警信息失败", Toast.LENGTH_LONG).show();
                        }
                    }
                });

    }

    public void initAdapter(final boolean isCheckBoxOn, final boolean isAllselected, final boolean isNoneSelected) {
        monitorMessages.setAdapter(new MonitorAdapter(getContext(), list, isCheckBoxOn, isAllselected, isNoneSelected));
    }

    public class MonitorAdapter extends ArrayAdapter {
        private Context context;
        private LayoutInflater inflater;
        Boolean isCheckBoxOn;
        Boolean isAllSelected;
        Boolean isNoneSelected;
        private List<AlarmVoBean> alarmVos;

        MonitorAdapter(Context context, List<AlarmVoBean> alarmVos, Boolean isCheckBoxOn, Boolean isAllSelected, Boolean isNoneSelected) {
            super(context, R.layout.adapter_monitor, alarmVos);
            this.context = context;
            this.alarmVos = alarmVos;
            this.isCheckBoxOn = isCheckBoxOn;
            this.isAllSelected = isAllSelected;
            this.isNoneSelected = isNoneSelected;
            inflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = new ViewHolder();
            if (null == convertView) {
                convertView = inflater.inflate(R.layout.adapter_monitor, parent, false);
            }
            viewHolder.alarmText = (TextView) convertView.findViewById(R.id.warning_text);
            viewHolder.warningImg1 = (ImageView) convertView.findViewById(R.id.warning_img1);
            viewHolder.warningImg2 = (ImageView) convertView.findViewById(R.id.warning_img2);
            viewHolder.warningImg3 = (ImageView) convertView.findViewById(R.id.warning_img3);
            viewHolder.warningImg4 = (ImageView) convertView.findViewById(R.id.warning_img4);
            viewHolder.alarmTime = (TextView) convertView.findViewById(R.id.warning_time);
            viewHolder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.chosen_layout);
            viewHolder.isMessageChoosen = (CheckBox) convertView.findViewById(R.id.is_message_choosen);
            convertView.setTag(viewHolder);

            String urls = alarmVos.get(position).getUrls();
            final String[] urlArray = convertUrlsToFourUrl(urls);
            ImageView warningImg1 = viewHolder.warningImg1;
            ImageView warningImg2 = viewHolder.warningImg2;
            ImageView warningImg3 = viewHolder.warningImg3;
            ImageView warningImg4 = viewHolder.warningImg4;


            if (urlArray.length > 0) {
                if (urlArray[0] != null) {
                    Glide
                            .with(context)
                            .load(urlArray[0])
                            .error(R.drawable.monitor_error)
                            .into(warningImg1);
                    warningImg1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            playMonitorPhoto(position, urlArray[0]);
                        }
                    });
                }
                if (urlArray.length > 1) {
                    if (urlArray[1] != null) {
                        Glide
                                .with(context)
                                .load(urlArray[1])
                                .error(R.drawable.monitor_error)
                                .into(warningImg2);
                        warningImg2.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                playMonitorPhoto(position, urlArray[1]);
                            }
                        });
                    }
                    if (urlArray.length > 2) {
                        if (urlArray[2] != null) {
                            Glide
                                    .with(context)
                                    .load(urlArray[2])
                                    .error(R.drawable.monitor_error)
                                    .into(warningImg3);
                            warningImg3.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    playMonitorPhoto(position, urlArray[2]);
                                }
                            });
                        }
                        if (urlArray.length > 3) {
                            if (urlArray[3] != null) {
                                Glide
                                        .with(context)
                                        .load(urlArray[3])
                                        .error(R.drawable.monitor_error)
                                        .into(warningImg4);
                                warningImg4.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        playMonitorPhoto(position, urlArray[3]);
                                    }
                                });
                            }
                        }
                    }
                }
            }


            TextView alarmText = viewHolder.alarmText;
            String alarmMessage = "您的船舶(" + alarmVos.get(position).getMachineName() + ")监测到可疑人员上船，请注意！";
            alarmText.setText(alarmMessage);

            RelativeLayout relativeLayout = viewHolder.relativeLayout;
            if (!isCheckBoxOn) {
                relativeLayout.setVisibility(View.INVISIBLE);
                relativeLayout.setEnabled(false);
            }


            CheckBox isMessageChoosen = viewHolder.isMessageChoosen;
            if (isAllSelected) {
                isMessageChoosen.setChecked(true);
            }
            if (isNoneSelected) {
                isMessageChoosen.setChecked(false);
            }
            isMessageChoosen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                            @Override
                                                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                                if (isChecked) {
                                                                    chosenList.add(alarmVos.get(position));
                                                                } else {
                                                                    chosenList.remove(alarmVos.get(position));
                                                                }
                                                            }
                                                        }
            );

            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日   hh:mm:ss", Locale.SIMPLIFIED_CHINESE);
            TextView alarmTime = viewHolder.alarmTime;
            alarmTime.setText(format.format(alarmVos.get(position).getAlarmTime()));

            return convertView;
        }

        private String[] convertUrlsToFourUrl(String url) {
            StringTokenizer st = new StringTokenizer(url, ",");//把","作为分割标志，然后把分割好的字符赋予StringTokenizer对象。
            String[] urlArray = new String[st.countTokens()];//通过StringTokenizer 类的countTokens方法计算在生成异常之前可以调用此 tokenizer 的 nextToken 方法的次数。
            int i = 0;
            while (st.hasMoreTokens()) {//看看此 tokenizer 的字符串中是否还有更多的可用标记。
                urlArray[i++] = st.nextToken();//返回此 string tokenizer 的下一个标记。
            }
            return urlArray;
        }

        class ViewHolder {
            ImageView warningImg1;
            ImageView warningImg2;
            ImageView warningImg3;
            ImageView warningImg4;
            TextView alarmTime;
            RelativeLayout relativeLayout;
            TextView alarmText;
            CheckBox isMessageChoosen;
        }

        void playMonitorPhoto(int position, String imagePath) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日   hh:mm:ss", Locale.CHINA);
            Intent intent = new Intent(getActivity(), PlayMonitorPhotoActivity.class);
            Bundle extra = new Bundle();
            extra.putString("imagePath", imagePath);
            extra.putString("boatName", alarmVos.get(position).getMachineName());
            extra.putString("monitorTime", format.format(alarmVos.get(position).getAlarmTime()));
            intent.putExtras(extra);
            startActivity(intent);
            getActivity().overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
        }
    }

}

