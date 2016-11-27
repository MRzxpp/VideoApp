package com.haishanda.android.videoapp.Fragement;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.bigkoo.alertview.AlertView;
import com.bigkoo.alertview.OnItemClickListener;
import com.bumptech.glide.Glide;
import com.haishanda.android.videoapp.Activity.MyCenterActivity;
import com.haishanda.android.videoapp.Api.ApiManage;
import com.haishanda.android.videoapp.Bean.LoginMessage;
import com.haishanda.android.videoapp.Bean.UserMessageBean;
import com.haishanda.android.videoapp.Config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
import com.haishanda.android.videoapp.greendao.gen.LoginMessageDao;
import com.haishanda.android.videoapp.greendao.gen.UserMessageBeanDao;

import org.greenrobot.greendao.query.QueryBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Zhongsz on 2016/11/26.
 */

public class SetPortraitFragment extends Fragment {
    @BindView(R.id.portrait)
    ImageView portraitView;
    @BindDrawable(R.drawable.default_portrait)
    Drawable defaultPortrait;
    private final int REQUEST_CODE_PICK_IMAGE = 1;
    private final int REQUEST_CODE_CAPTURE_CAMEIA = 0;

    private String portraitUrl;
    private UserMessageBean userMessageBean;
    private UserMessageBeanDao userMessageBeanDao;
    private final String TAG = "修改头像";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_head_portrait, container, false);
        ButterKnife.bind(this, view);
        LoginMessageDao loginMessageDao = VideoApplication.getApplication().getDaoSession().getLoginMessageDao();
        QueryBuilder<LoginMessage> loginMessageQueryBuilder = loginMessageDao.queryBuilder();
        long id = loginMessageQueryBuilder.unique().getId();
        userMessageBeanDao = VideoApplication.getApplication().getDaoSession().getUserMessageBeanDao();
        QueryBuilder<UserMessageBean> queryBuilder = userMessageBeanDao.queryBuilder();
        userMessageBean = queryBuilder.where(UserMessageBeanDao.Properties.Id.eq(id)).unique();
        initPortrait();
        return view;
    }

    @OnClick(R.id.back_to_my_center_btn)
    public void backToLastPage() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.portrait)
    public void editPortrait() {
        String[] options = new String[]{"拍照", "从相册中选择", "保存图片"};
        new AlertView("上传头像", null, "取消", null,
                options,
                getContext(), AlertView.Style.ActionSheet, new OnItemClickListener() {
            public void onItemClick(Object o, int position) {
                if (position == 0) {
                    String state = Environment.getExternalStorageState();
                    if (state.equals(Environment.MEDIA_MOUNTED)) {
                        Intent getImageByCamera = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(getImageByCamera, REQUEST_CODE_CAPTURE_CAMEIA);
                    } else {
                        Toast.makeText(getContext(), "请确认已经插入SD卡", Toast.LENGTH_LONG).show();
                    }
                } else if (position == 1) {
                    Intent intent = new Intent(Intent.ACTION_PICK);
                    intent.setType("image/*");//相片类型
                    startActivityForResult(intent, REQUEST_CODE_PICK_IMAGE);
                } else if (position == 2) {
                    Log.d("test", String.valueOf(position));
                    saveImageToGallery(getContext(), ((BitmapDrawable) portraitView.getDrawable()).getBitmap());
                }
                Log.d("test", String.valueOf(position));
            }
        }).show();
    }


    private void initPortrait() {
        String url = userMessageBean.getPortraitUrl();
        Glide
                .with(this)
                .load(url)
                .error(defaultPortrait)
                .into(portraitView);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_PICK_IMAGE) {
            Uri uri = data.getData();
            String path = getRealFilePath(getContext(), uri);
            Glide
                    .with(this)
                    .load(path)
                    .error(defaultPortrait)
                    .into(portraitView);
            uploadAndEditPortrait(new File(path));
            //to do find the path of pic

        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            Uri uri = data.getData();
            String path = getRealFilePath(getContext(), uri);
            Glide
                    .with(this)
                    .load(path)
                    .error(defaultPortrait)
                    .into(portraitView);
            uploadAndEditPortrait(new File(path));
            //to do find the path of pic
        }
    }

    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    private void uploadAndEditPortrait(File file) {
        ApiManage.getInstence().getUserApiServiceWithToken().uploadPortrait(file)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult<String>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(final SmartResult<String> stringSmartResult) {
                        if (stringSmartResult.getCode() == 1) {
                            ApiManage.getInstence().getUserApiServiceWithToken().editPortrait(stringSmartResult.getData())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<SmartResult>() {
                                        @Override
                                        public void onCompleted() {

                                        }

                                        @Override
                                        public void onError(Throwable e) {

                                        }

                                        @Override
                                        public void onNext(SmartResult smartResult) {
                                            if (smartResult.getCode() == 1) {
                                                userMessageBean.setPortraitUrl(stringSmartResult.getData());
                                                userMessageBeanDao.update(userMessageBean);
                                                Log.d(TAG, "success");
                                            } else {
                                                Log.d(TAG, smartResult.getMsg() != null ? smartResult.getMsg() : "修改未成功！");
                                            }

                                        }
                                    });
                        } else {
                            Log.d(TAG, stringSmartResult.getMsg() != null ? stringSmartResult.getMsg() : "上传未成功！");
                        }
                    }
                });
        MyCenterActivity myCenterActivity = (MyCenterActivity) getActivity();
        myCenterActivity.refresh();
    }

    public static void saveImageToGallery(Context context, Bitmap bmp) {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), "/VideoApp/Portrait");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + Environment.getExternalStorageDirectory().getPath() + "/VideoApp/Portrait")));
    }

}
