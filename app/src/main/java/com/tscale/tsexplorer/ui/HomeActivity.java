package com.tscale.tsexplorer.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.tscale.tsexplorer.R;

import roboguice.activity.RoboFragmentActivity;


public class HomeActivity extends RoboFragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);

        initView(savedInstanceState);


    }

    private void initView(Bundle savedInstanceState) {
        FragmentManager fm = getSupportFragmentManager();
        if (findViewById(R.id.home_container)!=null){
            if(savedInstanceState!=null){
                return;
            }else {
//                HomeActivityFragment fragment = HomeActivityFragment.newInstance();
                HomeActivityFragment fragment = new HomeActivityFragment();
                fm.beginTransaction().add(R.id.home_container,fragment,fragment.getClass().toString()).commit();
            }
        }
    }


}
