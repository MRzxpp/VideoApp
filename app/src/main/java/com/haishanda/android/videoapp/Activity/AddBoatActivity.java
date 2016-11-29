package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.BoatMessage;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.Listener.LoginListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.BoatMessageDao;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
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

    private final String Tag = "添加船舶";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_boat);
        ButterKnife.bind(this);
        confirmAddBoatBtn.setEnabled(false);
        boatPassword.addTextChangedListener(new LoginListener(boatNumber, boatPassword, confirmAddBoatBtn, blueBtn, greyBtn, white, white));
    }

    @OnClick(R.id.back_to_boat_fragment_btn)
    public void backToLastPage(View view) {
        this.finish();
    }

    @OnClick(R.id.add_boat_scan_qrcode_btn)
    public void scanBoatQRCode() {
        Intent intent = new Intent(AddBoatActivity.this, CaptureActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            // ZXing回傳的內容
            String contents = intent.getStringExtra("codedContent");
            boatNumber.setText(contents);
        }
    }

    @OnClick(R.id.confirm_add_boat_btn)
    public void addBoat(View view) {
        String boatNum = boatNumber.getText().toString();
        String boatPwd = boatPassword.getText().toString();
        //Todo add boat logic
        if (boatNum == "") {
            Toast.makeText(this, "请输入船舶序列号", Toast.LENGTH_LONG).show();
        } else if (boatPwd == "") {
            Toast.makeText(this, "请输入船舶绑定密码", Toast.LENGTH_LONG).show();
        } else {
            ApiManage.getInstence().getBoatApiService().addBoat(boatNum, boatPwd)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<SmartResult>() {
                        @Override
                        public void onCompleted() {
                            Log.i(Tag, "add completed");
                        }

                        @Override
                        public void onError(Throwable e) {
                            Log.i(Tag, "add failed");
                            e.printStackTrace();
                        }

                        @Override
                        public void onNext(SmartResult smartResult) {
                            if (smartResult.getCode() == 1) {
                                Log.i(Tag, "add successfully");
                                Toast.makeText(getApplicationContext(), "添加船舶成功", Toast.LENGTH_LONG).show();
                            } else {
                                Log.i(Tag, "add failed!");
                                Toast.makeText(getApplicationContext(), smartResult.getMsg(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}
