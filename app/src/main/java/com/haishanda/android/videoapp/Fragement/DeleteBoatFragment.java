package com.haishanda.android.videoapp.Fragement;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.haishanda.android.videoapp.Activity.BoatConfigActivity;
import com.haishanda.android.videoapp.Listener.ClearBtnListener;
import com.haishanda.android.videoapp.Listener.LoginListener;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ChangeVisiable;

import butterknife.BindColor;
import butterknife.BindDrawable;
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
    @BindView(R.id.eye4)
    TextView eye4;
    @BindView(R.id.clear4)
    ImageView clear4;
    @BindView(R.id.delete_boat_next_step_btn)
    Button nextStepBtn;
    @BindColor(R.color.white)
    int white;
    @BindDrawable(R.drawable.corners_blue_btn)
    Drawable blueBtn;
    @BindDrawable(R.drawable.corners_grey_btn)
    Drawable greyBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_delete_boat, container, false);
        ButterKnife.bind(this, view);
        clear4.setVisibility(View.INVISIBLE);
        boatPassword.addTextChangedListener(new ClearBtnListener(clear4, boatPassword));
        boatPassword.addTextChangedListener(new LoginListener(boatUsername, boatPassword, nextStepBtn, blueBtn, greyBtn, white, white));
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

    @OnClick(R.id.eye4)
    public void setBoatPasswordVisiable(View view) {
        ChangeVisiable.changeVisiable(eye4, boatPassword);
    }

    @OnClick(R.id.clear4)
    public void clearBoatPassword(View view) {
        boatPassword.setText("");
    }


}
