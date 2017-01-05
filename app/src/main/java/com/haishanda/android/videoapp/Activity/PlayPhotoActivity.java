package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.FileSizeUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.Views.MaterialDialog;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/11/2.
 */

public class PlayPhotoActivity extends Activity {
    Bundle extra;
    @BindView(R.id.photo_main)
    ImageView photoMain;

    private String imagePath;
    private List<ImageMessage> imageMessage;
    private String boatName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_photos);
        ButterKnife.bind(this);
        extra = getIntent().getExtras();
        imagePath = extra.getString("imagePath");
        boatName = extra.getString("boatName");
        Glide
                .with(this)
                .load(imagePath)
                .into(photoMain);

    }

    @OnClick(R.id.back_to_photos_btn)
    public void backToPhotos(View view) {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @OnClick(R.id.show_image_info)
    public void showImgInfo() {
        String imageName = imagePath.substring("/sdcard/VideoApp/".length() + boatName.length() + 2 + "yyyy年MM月dd日".length(), imagePath.length());
        ImageMessageDao imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        QueryBuilder<ImageMessage> queryBuilder = imageMessageDao.queryBuilder();
        imageMessage = queryBuilder.where(ImageMessageDao.Properties.ImgPath.eq(imageName)).list();
        ImageMessage im = imageMessage.get(0);
        ViewHolder viewHolder = new ViewHolder(R.layout.adapter_image_info);
        DialogPlus dialogPlus = DialogPlus.newDialog(this)
                .setContentHolder(viewHolder)
                .setCancelable(true)
                .setGravity(Gravity.CENTER)
                .setContentWidth(ViewGroup.LayoutParams.MATCH_PARENT)  // or any custom width ie: 300
                .setContentHeight(ViewGroup.LayoutParams.WRAP_CONTENT)
                .setExpanded(true)
                .create();
        View view = viewHolder.getInflatedView();
        TextView nameView = (TextView) view.findViewById(R.id.image_info_name);
        TextView addTimeView = (TextView) view.findViewById(R.id.image_info_addtime);
        TextView sizeView = (TextView) view.findViewById(R.id.image_info_size);
        nameView.setText("文件名称：" + imageName);
        addTimeView.setText("拍摄时间：" + im.getAddTime());
        sizeView.setText("文件大小：" + FileSizeUtil.getAutoFileOrFilesSize(imagePath));
        dialogPlus.show();
    }

    @OnClick(R.id.share_image)
    public void shareImage() {

    }

    @OnClick(R.id.delete_photo)
    public void deletePhoto(final View view) {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setMessage("是否确认删除?");
        materialDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAction(view);
                materialDialog.dismiss();
            }
        });
        materialDialog.setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialDialog.dismiss();
            }
        });
        materialDialog.show();
    }

    private void deleteAction(View view) {
        String imagePath = extra.getString("imagePath");
        String boatName = extra.getString("boatName");
        String imageName = imagePath.substring("/sdcard/VideoApp/".length() + boatName.length() + 2 + "yyyy年MM月dd日".length(), imagePath.length());
        File file = new File(imagePath);
        file.delete();
        ImageMessageDao imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        QueryBuilder<ImageMessage> queryBuilder = imageMessageDao.queryBuilder();
        imageMessage = queryBuilder.where(ImageMessageDao.Properties.ImgPath.eq(imageName)).list();
        ImageMessage im = imageMessage.get(0);
        imageMessageDao.delete(im);
        Log.d("PhotoAction", "删除成功");
        backToPhotos(view);
    }
}
