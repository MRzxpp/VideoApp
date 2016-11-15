package com.haishanda.android.videoapp.Activity;

import android.app.Activity;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.haishanda.android.videoapp.Adapter.PhotosAdapter;
import com.haishanda.android.videoapp.Bean.ImageMessage;
import com.haishanda.android.videoapp.Fragement.PhotosFragment;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.ImageMessageDao;

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

    private List<ImageMessage> imageMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_photos);
        ButterKnife.bind(this);
        extra = getIntent().getExtras();
        String imagePath = extra.getString("imagePath");
        Glide
                .with(this)
                .load(imagePath)
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(photoMain);

    }

    @OnClick(R.id.back_to_photos_btn)
    public void backToPhotos(View view) {
        this.finish();
    }

    @OnClick(R.id.show_image_info)
    public void showImgInfo() {
        ImageMessageDao imageMessageDao = VideoApplication.getApplication().getDaoSession().getImageMessageDao();
        imageMessageDao.deleteAll();
    }

    @OnClick(R.id.share_image)
    public void shareImage() {

    }

    @OnClick(R.id.delete_photo)
    public void deletePhoto(View view) {
        String imagePath = extra.getString("imagePath");
        String boatName = extra.getString("boatName");
        String imageName = imagePath.substring("/sdcard/VideoApp/".length() + boatName.length() + 1, imagePath.length());
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
