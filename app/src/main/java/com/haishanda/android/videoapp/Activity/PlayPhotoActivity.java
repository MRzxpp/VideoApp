package com.haishanda.android.videoapp.Activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.FileUtil;
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
 * 播放监控的截图
 * Created by Zhongsz on 2016/11/2.
 */

public class PlayPhotoActivity extends Activity {
    Bundle extra;
    @BindView(R.id.photo_main)
    ImageView photoMain;

    private String imagePath;
    private List<ImageMessage> imageMessage;
    private String boatName;

    private final static String TAG = "PlayPhotoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_photos);
        ButterKnife.bind(this);
        extra = getIntent().getExtras();
        imagePath = extra.getString("imagePath");
        boatName = extra.getString("boatName");
        RequestListener<String, GlideDrawable> photoListener = new RequestListener<String, GlideDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                Log.d(TAG, "Glide load error:" + e.toString());
                deleteAction();
                Toast.makeText(getApplicationContext(), "图片已在本地被删除", Toast.LENGTH_SHORT).show();
                return false;
            }

            @Override
            public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                return false;
            }
        };
        Glide
                .with(this)
                .load(imagePath)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .listener(photoListener)
                .into(photoMain);

    }


    @OnClick(R.id.back_to_photos_btn)
    public void backToPhotos() {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @SuppressLint("SetTextI18n")
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
                .setExpanded(false)
                .create();
        View view = viewHolder.getInflatedView();
        TextView nameView = (TextView) view.findViewById(R.id.image_info_name);
        TextView addTimeView = (TextView) view.findViewById(R.id.image_info_addtime);
        TextView sizeView = (TextView) view.findViewById(R.id.image_info_size);
        nameView.setText("文件名称：" + imageName);
        addTimeView.setText("拍摄时间：" + im.getAddTime());
        sizeView.setText("文件大小：" + FileUtil.getAutoFileOrFilesSize(imagePath));
        dialogPlus.show();
    }

    @OnClick(R.id.share_image)
    public void shareImage() {

    }

    @OnClick(R.id.delete_photo)
    public void deletePhoto() {
        final MaterialDialog materialDialog = new MaterialDialog(this);
        materialDialog.setMessage("是否确认删除?");
        materialDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteAction();
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

    private void deleteAction() {
        String imagePath = extra.getString("imagePath");
        String boatName = extra.getString("boatName");
        String imageName;
        if (boatName != null && imagePath != null) {
            imageName = imagePath.substring("/sdcard/VideoApp/".length() + boatName.length() + 2 + "yyyy年MM月dd日".length(), imagePath.length());
            File file = new File(imagePath);
            if (file.exists()) {
                file.delete();
            }
            ImageMessageDao imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
            QueryBuilder<ImageMessage> queryBuilder = imageMessageDao.queryBuilder();
            imageMessage = queryBuilder.where(ImageMessageDao.Properties.ImgPath.eq(imageName)).list();
            ImageMessage im = imageMessage.get(0);
            imageMessageDao.delete(im);
            Log.d("PhotoAction", "删除成功");
        } else {
            Log.d("PhotoAction", "删除失败");
        }
        backToPhotos();
    }
}
