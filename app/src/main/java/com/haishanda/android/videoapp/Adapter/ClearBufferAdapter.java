package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/11/14.
 */

public class ClearBufferAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;

    public ClearBufferAdapter(Context context) {
        super();
        this.context = context;

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
//        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_clear_buffer, parent, false);
//        }
        ButterKnife.bind(this, convertView);

        return convertView;
    }

    @OnClick(R.id.clear_buffer_btn)
    public void clearBuffer() {
        Toast.makeText(context, "clear success", Toast.LENGTH_SHORT).show();
    }

    @OnClick(R.id.not_clear_buffer_btn)
    public void notClearBuffer() {
        Toast.makeText(context, "clear canceled", Toast.LENGTH_SHORT).show();
    }
}
