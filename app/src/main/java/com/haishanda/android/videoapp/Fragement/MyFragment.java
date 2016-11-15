package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haishanda.android.videoapp.Activity.AccountBalanceActivity;
import com.haishanda.android.videoapp.Activity.CommonSettingsActivity;
import com.haishanda.android.videoapp.Activity.MessageCenterActivity;
import com.haishanda.android.videoapp.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/14.
 */

public class MyFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.message_center)
    public void skipToMessageCenter(View view) {
        Intent intent = new Intent(getActivity(), MessageCenterActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.common_settings)
    public void skipToCommonSettings(View view) {
        Intent intent = new Intent(getActivity(), CommonSettingsActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.account_balance)
    public void skipToAccountBalance(View view) {
        Intent intent = new Intent(getActivity(), AccountBalanceActivity.class);
        startActivity(intent);
    }
}
