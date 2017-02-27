package com.haishanda.android.videoapp.fragment;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.haishanda.android.videoapp.activity.MyCenterActivity;
import com.haishanda.android.videoapp.api.ApiManage;
import com.haishanda.android.videoapp.bean.UserMessageBean;
import com.haishanda.android.videoapp.config.Constant;
import com.haishanda.android.videoapp.config.SmartResult;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.VideoApplication;
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
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * 修改头像
 * Created by Zhongsz on 2016/11/26.
 */

public class SetPortraitFragment extends Fragment {
    @BindView(R.id.portrait)
    ImageView portraitView;
    @BindDrawable(R.drawable.default_portrait)
    Drawable defaultPortrait;
    private final int REQUEST_CODE_PICK_IMAGE = 1;
    private final int REQUEST_CODE_CAPTURE_CAMEIA = 0;

    private UserMessageBean userMessageBean;
    private UserMessageBeanDao userMessageBeanDao;
    private final String TAG = "修改头像";
    SharedPreferences preferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_set_head_portrait, container, false);
        ButterKnife.bind(this, view);
        preferences = getActivity().getSharedPreferences(Constant.USER_PREFERENCE, Context.MODE_PRIVATE);
        long id = preferences.getInt(Constant.USER_PREFERENCE_ID, -1);
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
        fragmentTransaction.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right, android.R.anim.slide_in_left, android.R.anim.slide_out_right);
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
                    try {
                        saveImageToGallery(getContext(), ((BitmapDrawable) portraitView.getDrawable()).getBitmap());
                    } catch (ClassCastException e) {
                        saveImageToGallery(getContext(), ((GlideBitmapDrawable) portraitView.getDrawable()).getBitmap());
                    }
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
            if (data != null) {
                Uri uri = data.getData();
                String path = getRealFilePath(getContext(), uri);
                Glide
                        .with(this)
                        .load(path)
                        .error(defaultPortrait)
                        .into(portraitView);
                uploadAndEditPortrait(new File(path));
            }
            //to do find the path of pic

        } else if (requestCode == REQUEST_CODE_CAPTURE_CAMEIA) {
            Uri uri = data.getData();
            if (uri == null) {
                //use bundle to get data
                Bundle bundle = data.getExtras();
                if (bundle != null) {
                    Bitmap photo = (Bitmap) bundle.get("data"); //get bitmap
                    //spath :生成图片取个名字和路径包含类型
                    String path = userMessageBean.getNickName() + "_portrait";
                    saveImage(photo, path);
                    String imagePath = Environment.getExternalStorageDirectory() + "/VideoApp/Portrait/" + path + ".jpg";
                    Glide
                            .with(this)
                            .load(imagePath)
                            .error(defaultPortrait)
                            .into(portraitView);
                    uploadAndEditPortrait(new File(imagePath));
                }
            } else {
                //to do find the path of pic by uri
                String path = getRealFilePath(getContext(), uri);
                Glide
                        .with(this)
                        .load(path)
                        .error(defaultPortrait)
                        .into(portraitView);
                uploadAndEditPortrait(new File(path));
            }
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
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        // MultipartBody.Part is used to send also the actual filename
        MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);
        // adds another part within the multipart request
//        String descriptionString = "image";
        final String token = preferences.getString(Constant.USER_PREFERENCE_TOKEN, "");
        ApiManage.getInstence().getUserApiService().uploadPortrait(token, body)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SmartResult<String>>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "upload completed");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "upload error");
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(SmartResult<String> stringSmartResult) {
                        final String portraitUrl = stringSmartResult.getData();
                        if (stringSmartResult.getCode() == 1) {
                            Log.d(TAG, "upload success");
                            ApiManage.getInstence().getUserApiServiceWithToken().editPortrait(token, stringSmartResult.getData())
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(new Observer<SmartResult>() {
                                        @Override
                                        public void onCompleted() {
                                            Log.d(TAG, "edit completed");
                                        }

                                        @Override
                                        public void onError(Throwable e) {
                                            Log.d(TAG, "edit error");
                                            e.printStackTrace();
                                        }

                                        @Override
                                        public void onNext(SmartResult smartResult) {
                                            if (smartResult.getCode() == 1) {
                                                userMessageBean.setPortraitUrl(portraitUrl);
                                                userMessageBeanDao.update(userMessageBean);
                                                Log.d(TAG, "edit success");
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

    public static void saveImage(Bitmap photo, String spath) {
        File sdcardDir = Environment.getExternalStorageDirectory();
        String path = sdcardDir.getPath() + "/VideoApp";
        File appDir = new File(path);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        File boatDir = new File(appDir + "/Portrait");
        if (!boatDir.exists()) {
            boatDir.mkdir();
        }
        String imgName = spath + ".jpg";
        File file = new File(boatDir, imgName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            photo.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
