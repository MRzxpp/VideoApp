package com.haishanda.android.videoapp.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.haishanda.android.videoapp.Bean.PackageVo;
import com.haishanda.android.videoapp.R;
import com.haishanda.android.videoapp.Utils.ExpandableLayout;

import java.util.List;

/**
 * Created by Zhongsz on 2016/11/29.
 */

public class AccountBalanceAdapter extends ArrayAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<PackageVo> packageVoList;

    public AccountBalanceAdapter(Context context, List<PackageVo> packageVoList) {
        super(context, R.layout.adapter_account_balance, packageVoList);
        this.context = context;
        this.packageVoList = packageVoList;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        if (null == convertView) {
            convertView = inflater.inflate(R.layout.adapter_account_balance, parent, false);
        }

        TextView machineName = (TextView) convertView.findViewById(R.id.balance_machine_name);
        machineName.setText(packageVoList.get(position).getMachineName());

        TextView balanceText = (TextView) convertView.findViewById(R.id.balance_text);
        balanceText.setText(String.valueOf(packageVoList.get(position).getBalance()));

        TextView currentMeal = (TextView) convertView.findViewById(R.id.meal_current);
        currentMeal.setText(packageVoList.get(position).getPackageName());

        TextView monthRent = (TextView) convertView.findViewById(R.id.month_rent);
        monthRent.setText(String.valueOf(packageVoList.get(position).getMonthRent()));

        TextView balanceDetailText = (TextView) convertView.findViewById(R.id.balance_detail_text);
        balanceDetailText.setText("当前套餐详情是xxxxx\n当前套餐详情是xxxxx\n当前套餐详情是xxxxx");

        final ExpandableLayout expandableLayout = (ExpandableLayout) convertView.findViewById(R.id.balance_detail_open);

        final ImageView expand = (ImageView) convertView.findViewById(R.id.balance_expand);
        final ImageView collapse = (ImageView) convertView.findViewById(R.id.balance_collapse);
        collapse.setClickable(false);
        collapse.setVisibility(View.INVISIBLE);
        expand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableLayout.show();
                expand.setClickable(false);
                expand.setVisibility(View.INVISIBLE);
                collapse.setClickable(true);
                collapse.setVisibility(View.VISIBLE);
            }
        });
        collapse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableLayout.hide();
                collapse.setClickable(false);
                collapse.setVisibility(View.INVISIBLE);
                expand.setClickable(true);
                expand.setVisibility(View.VISIBLE);
            }
        });

        TextView packup = (TextView) convertView.findViewById(R.id.balance_detail_pack_up);
        packup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                expandableLayout.hide();
                collapse.setClickable(false);
                collapse.setVisibility(View.INVISIBLE);
                expand.setClickable(true);
                expand.setVisibility(View.VISIBLE);
            }
        });

        return convertView;
    }
}
