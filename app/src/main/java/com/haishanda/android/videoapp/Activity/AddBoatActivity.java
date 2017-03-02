package com.haishanda.android.videoapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.config.StringConstant;
import com.haishanda.android.videoapp.utils.textwatcher.LoginWatcher;
import com.haishanda.android.videoapp.R;
import com.uuzuche.lib_zxing.activity.CaptureActivity;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 添加船舶
 * Created by Zhongsz on 2016/10/26.
 */

public class AddBoatActivity extends Activity {
    @BindView(R.id.boat_number)
    EditText boatNumber;
    @BindView(R.id.boat_password)
    EditText boatPassword;
    @BindView(R.id.confirm_add_boat_btn)
    Button confirmAddBoatBtn;
    @BindColor(R.color.white)
    int white;
    @BindDrawable(R.drawable.corners_blue_btn)
    Drawable blueBtn;
    @BindDrawable(R.drawable.corners_grey_btn)
    Drawable greyBtn;

    private static final String Tag = "添加船舶";
    private static final String ADD_BOAT_COMPLETED = "添加船舶结束";
    private static final String ADD_BOAT_SUCCESS = "添加船舶成功";
    private static final String ADD_BOAT_FAIL = "添加船舶失败";
    private static final String SCAN_QR_CODE_FAIL = "扫描二维码失败";
    private static final String PLEASE_INPUT_BOAT_SERIAL_NUMBER = "请输入船舶序列号";
    private static final String PLEASE_INPUT_BOAT_PASSWORD = "请输入船舶密码";
    private AddBoatActivity instance;
    private static final int REQUEST_CODE = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_boat);
        ButterKnife.bind(this);
        instance = this;
        confirmAddBoatBtn.setEnabled(false);
        boatPassword.addTextChangedListener(new LoginWatcher(boatNumber, boatPassword, confirmAddBoatBtn, blueBtn, greyBtn, white, white));
    }

    @OnClick(R.id.back_to_boat_fragment_btn)
    public void backToLastPage(View view) {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.add_boat_scan_qrcode_btn)
    public void scanBoatQRCode() {
        Intent intent = new Intent(AddBoatActivity.this, CaptureActivity.class);
        startActivityForResult(intent, REQUEST_CODE);
        overridePendingTransition(R.anim.slide_right_in, R.anim.slide_left_out);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != intent) {
                Bundle bundle = intent.getExtras();
                if (bundle == null) {
                    return;
                }
                if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_SUCCESS) {
                    String result = bundle.getString(CodeUtils.RESULT_STRING);
                    boatNumber.setText(result);

                } else if (bundle.getInt(CodeUtils.RESULT_TYPE) == CodeUtils.RESULT_FAILED) {
                    Toast.makeText(getApplicationContext(), SCAN_QR_CODE_FAIL, Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @OnClick(R.id.confirm_add_boat_btn)
    public void addBoat(View view) {
        String boatNum = boatNumber.getText().toString();
        String boatPassword = this.boatPassword.getText().toString();
        if (boatNum.equals("")) {
            Toast.makeText(this, PLEASE_INPUT_BOAT_SERIAL_NUMBER, Toast.LENGTH_LONG).show();
        } else if (boatPassword.equals("")) {
            Toast.makeText(this, PLEASE_INPUT_BOAT_PASSWORD, Toast.LENGTH_LONG).show();
        } else {
            ApiManage.getInstence().getBoatApiService().addBoat(boatNum, boatPassword)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.i(Tag, ADD_BOAT_COMPLETED);
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(Tag, ADD_BOAT_FAIL);
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            if (smartResult.getCode() == 1) {
                                Log.i(Tag, ADD_BOAT_SUCCESS);
                                instance.finish();
                            } else {
                                Log.i(Tag, ADD_BOAT_FAIL);
                                Toast.makeText(getApplicationContext(), smartResult.getMsg() != null ? smartResult.getMsg() : StringConstant.MESSAGE_SERVER_RETURN_FALSE, Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
