package com.haishanda.android.videoapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.haishanda.android.videoapp.bean.ImageMessage;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.utils.FileUtil;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.views.MaterialDialog;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;
import com.orhanobut.dialogplus.DialogPlus;
import com.orhanobut.dialogplus.ViewHolder;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 播放监控的截图
 * Created by Zhongsz on 2016/11/2.
 */

//TODO 翻页时可以翻阅所有截图而不只是某一天的

public class PlayPhotoActivity extends Activity {
    Bundle extra;
    @BindView(R.id.monitor_photo_main)
    ViewFlipper photoMain;
    @BindView(R.id.monitor_photo_serial)
    TextView photoSerial;

    private ArrayList<String> imagePathList;
    private String boatName;
    private GestureDetector gestureDetector;
    private PlayPhotoActivity instance;
    private int position = 0;

    private final static String TAG = "PlayPhotoActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_photos);
        ButterKnife.bind(this);
        if (instance == null) {
            instance = this;
        }
        extra = getIntent().getExtras();
        imagePathList = extra.getStringArrayList("dailyImagePaths");
        position = extra.getInt("position");
        boatName = extra.getString("boatName");
    }

    @Override
    protected void onResume() {
        super.onResume();
        photoMain.setAutoStart(false);
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
        for (int i = 0; i < imagePathList.size(); i++) {
            ImageView imageView = new ImageView(instance);
            Glide
                    .with(this)
                    .load(imagePathList.get(i))
                    .listener(photoListener)
                    .into(imageView);
            photoMain.addView(imageView, i);
        }
        photoMain.setDisplayedChild(position);
        gestureDetector = new GestureDetector(this, new GestureDetector.OnGestureListener() {
            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float arg2,
                                   float arg3) {
                if (e2.getX() - e1.getX() > 120) { // 从左向右滑动（左进右出）
                    Animation rInAnim = AnimationUtils.loadAnimation(instance,
                            R.anim.slide_left_in); // 向右滑动左侧进入的渐变效果（alpha 0.1 -> 1.0）
                    Animation rOutAnim = AnimationUtils.loadAnimation(instance,
                            R.anim.slide_right_out); // 向右滑动右侧滑出的渐变效果（alpha 1.0 -> 0.1）

                    photoMain.setInAnimation(rInAnim);
                    photoMain.setOutAnimation(rOutAnim);
                    photoMain.showPrevious();
                    if (position == 0) {
                        position = imagePathList.size() - 1;
                        photoSerial.setText((position + 1) + "/" + imagePathList.size());
                        return true;
                    }
                    if (position > 0) {
                        position--;
                        photoSerial.setText((position + 1) + "/" + imagePathList.size());
                    }
                    return true;
                } else if (e2.getX() - e1.getX() < -120) { // 从右向左滑动（右进左出）
                    Animation lInAnim = AnimationUtils.loadAnimation(instance,
                            R.anim.slide_right_in); // 向左滑动左侧进入的渐变效果（alpha 0.1 -> 1.0）
                    Animation lOutAnim = AnimationUtils.loadAnimation(instance,
                            R.anim.slide_left_out); // 向左滑动右侧滑出的渐变效果（alpha 1.0 -> 0.1）

                    photoMain.setInAnimation(lInAnim);
                    photoMain.setOutAnimation(lOutAnim);
                    photoMain.showNext();
                    if (position == imagePathList.size() - 1) {
                        position = 0;
                        photoSerial.setText((position + 1) + "/" + imagePathList.size());
                        return true;
                    }
                    if (position < imagePathList.size() - 1) {
                        position++;
                        photoSerial.setText((position + 1) + "/" + imagePathList.size());
                    }
                    return true;
                }
                return true;
            }

        });
        photoSerial.setText((position + 1) + "/" + imagePathList.size());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gestureDetector.onTouchEvent(event); // 注册手势事件
    }

    @OnClick(R.id.back_to_photos_btn)
    public void backToPhotos() {
        this.finish();
        overridePendingTransition(R.anim.slide_left_in, R.anim.slide_right_out);
    }

    @SuppressLint("SetTextI18n")
    @OnClick(R.id.show_image_info)
    public void showImgInfo() {
        String imageName = imagePathList.get(position)
                .substring((Environment.getExternalStorageDirectory().getPath() + "/VideoApp/").length() + boatName.length() + 1,
                        imagePathList.get(position).length());
        ImageMessageDao imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        QueryBuilder<ImageMessage> queryBuilder = imageMessageDao.queryBuilder();
        ImageMessage imageMessage = queryBuilder.where(ImageMessageDao.Properties.ImageName.eq(imageName)).unique();
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
        addTimeView.setText("拍摄时间：" + imageMessage.getAddDate());
        sizeView.setText("文件大小：" + FileUtil.getAutoFileOrFilesSize(imagePathList.get(position)));
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
        String imagePath = imagePathList.get(position);
        String boatName = extra.getString("boatName");
        String imageName;
        if (boatName != null && imagePath != null) {
            imageName = imagePath.substring((Environment.getExternalStorageDirectory().getPath() + "/VideoApp/").length() + boatName.length() + 1, imagePath.length());
            File file = new File(imagePath);
            if (file.exists()) {
                file.delete();
            }
            ImageMessageDao imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
            QueryBuilder<ImageMessage> queryBuilder = imageMessageDao.queryBuilder();
            ImageMessage imageMessage = queryBuilder.where(ImageMessageDao.Properties.ImageName.eq(imageName)).unique();
            imageMessageDao.delete(imageMessage);
            Log.d("PhotoAction", "删除成功");
        } else {
            Log.d("PhotoAction", "删除失败");
        }
        backToPhotos();
    }
}
