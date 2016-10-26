package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.haishanda.android.videoapp.Activity.BoatConfigActivity;
import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/26.
 */

public class DeleteBoatFragment extends Fragment {
    @BindView(R.id.boat_username_text)
    EditText boatUsername;
    @BindView(R.id.boat_password_text)
    EditText boatPassword;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_boat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.delete_boat_next_step_btn)
    public void skipToConfirmDeleteFragment() {
        ConfirmDeleteBoatFragment confirmDeleteBoatFragment = new ConfirmDeleteBoatFragment();
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.delete_boat_message, confirmDeleteBoatFragment);
        fragmentTransaction.commit();
    }

    @OnClick(R.id.back_to_boat_config_btn)
    public void backToBoatConfigActivity() {
        Intent intent = new Intent(getActivity(), BoatConfigActivity.class);
        startActivity(intent);
    }
}
