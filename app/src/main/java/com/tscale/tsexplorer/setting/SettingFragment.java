package com.tscale.tsexplorer.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.base.BaseFragment;

/**
 * Created by rd-19 on 2015/7/22.
 */
public class SettingFragment extends BaseFragment implements View.OnClickListener {

    private TextView title;

    private ViewGroup count;

    private Button logout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = (TextView) getView().findViewById(R.id.title);
        title.setText(R.string.menu_item_setting);


        count = (ViewGroup) getView().findViewById(R.id.setting_count);
        count.setOnClickListener(this);

        logout = (Button) getView().findViewById(R.id.setting_logout);
        logout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_logout:
                getActivity().finish();
                break;
            case R.id.setting_count:
                startCountSetting();
                break;
            default:
                break;
        }
    }

    private void startCountSetting() {
        Intent intent = new Intent();
        intent.setClass(getActivity(),CountSettingActivity.class);
        getActivity().startActivity(intent);
    }
}
