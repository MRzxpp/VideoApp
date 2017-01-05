package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zhongsz on 2016/11/19.
 */

public class ImageInfoAdapter extends BaseAdapter {
    @BindView(R.id.image_info_name)
    TextView nameView;
    @BindView(R.id.image_info_addtime)
    TextView addTimeView;
    @BindView(R.id.image_info_size)
    TextView sizeView;
    private Context context;
    private LayoutInflater inflater;
    private String name;
    private String addTime;
    private String size;

    public ImageInfoAdapter(Context context, String name, String addTime, String size) {
        super();
        this.context = context;
        this.name = name;
        this.addTime = addTime;
        this.size = size;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1;
    }


    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = inflater.inflate(R.layout.adapter_image_info, parent, false);
        ButterKnife.bind(this, convertView);

        nameView.setText("文件名称：" + name);
        addTimeView.setText("拍摄时间：" + addTime);
        sizeView.setText("文件大小：" + size);

        return convertView;
    }
}
