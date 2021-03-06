package com.tscale.tsexplorer.details;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.tscale.tsexplorer.R;

import roboguice.activity.RoboFragmentActivity;

public class AddProduct extends RoboFragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        FragmentManager fm = getSupportFragmentManager();
        AddProductFragment fragment = new AddProductFragment();
        fm.beginTransaction().add(R.id.home_container, fragment, fragment.getClass().toString()).commit();
    }

    public void onBackPress(View V){
        onBackPressed();
    }
}
