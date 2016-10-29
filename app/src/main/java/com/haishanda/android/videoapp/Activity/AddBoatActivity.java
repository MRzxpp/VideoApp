package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.client.android.CaptureActivity;
import com.haishanda.android.videoapp.Listener.LoginListener;
import com.haishanda.android.videoapp.R;

import butterknife.BindColor;
import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_boat);
        ButterKnife.bind(this);
        confirmAddBoatBtn.setEnabled(false);
        confirmAddBoatBtn.addTextChangedListener(new LoginListener(boatNumber, boatPassword, confirmAddBoatBtn, blueBtn, greyBtn, white, white));
    }

    @OnClick(R.id.back_to_boat_fragment_btn)
    public void backToLastPage(View view) {
        Intent intent = new Intent(AddBoatActivity.this, MainActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.add_boat_scan_qrcode_btn)
    public void scanBoatQRCode() {
        Intent intent = new Intent(AddBoatActivity.this, CaptureActivity.class);
        startActivity(intent);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == RESULT_OK) {
            // ZXing回傳的內容
            String contents = intent.getStringExtra("codedContent");
            Toast.makeText(this, contents, Toast.LENGTH_SHORT).show();
        }
    }

    @OnClick(R.id.confirm_add_boat_btn)
    public void addBoat(View view) {
        String boatNum = boatNumber.getText().toString();
        String boatPwd = boatPassword.getText().toString();
        Toast.makeText(getApplicationContext(), boatNum + boatPwd, Toast.LENGTH_SHORT).show();
        //Todo add boat logic
    }
}
