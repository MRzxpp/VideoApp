package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import com.haishanda.android.videoapp.Adapter.ClearBufferAdapter;
import com.haishanda.android.videoapp.Adapter.PhotosAdapter;
import com.haishanda.android.videoapp.R;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.OnItemClickListener;
import com.orhanobut.dialogplus.ViewHolder;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/11/12.
 */

public class CommonSettingsActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_settings);
        ButterKnife.bind(this);
    }

    public Activity getCommonSettingsActivity() {
        return this;
    }

    @OnClick(R.id.back_to_my_btn)
    public void backToMyPage() {
        this.finish();
    }

    @OnClick(R.id.modify_password_layout)
    public void modifyPassword() {

    }

    @OnClick(R.id.clear_buffer_layout)
    public void clearBuffer() {
        DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setAdapter(new ClearBufferAdapter(this))
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setOnItemClickListener(new OnItemClickListener() {
                    @Override
                    public void onItemClick(DialogPlus dialog, Object item, View view, int position) {
                        Log.i("DialogPlus", "Item clicked");
                    }
                })
                .setExpanded(true)
                .create();
        dialogPlus.show();
    }

    @OnClick(R.id.about_us_layout)
    public void skipToAboutUsPage() {

    }

    @OnClick(R.id.help_and_feedback_layout)
    public void skipToHelpAndFeedbackLayout() {

    }

    @OnClick(R.id.logout_btn)
    public void logOut() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("确认退出吗?");
        builder.setTitle("提示");
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                getCommonSettingsActivity().finish();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }
}
