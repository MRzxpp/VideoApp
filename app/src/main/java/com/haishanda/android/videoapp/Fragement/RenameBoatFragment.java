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
import android.widget.ImageView;
import android.widget.Toast;

import com.haishanda.android.videoapp.Activity.BoatConfigActivity;
import com.haishanda.android.videoapp.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Zhongsz on 2016/10/26.
 */

public class RenameBoatFragment extends Fragment {
    @BindView(R.id.boat_new_name)
    EditText boatNewName;
    @BindView(R.id.clear5)
    ImageView clear5;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rename_boat, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.save_rename_boat_btn)
    public void saveBoatNewName() {
        Toast.makeText(getContext(), boatNewName.getText().toString(), Toast.LENGTH_SHORT).show();
        //Todo save new name;
    }

    @OnClick(R.id.back_to_boat_config_btn)
    public void backToFrontPage() {
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.remove(this);
        fragmentTransaction.commit();
    }
}
