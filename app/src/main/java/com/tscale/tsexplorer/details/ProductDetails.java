package com.tscale.tsexplorer.details;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.base.SwipeBackActivity;

import roboguice.activity.RoboFragmentActivity;

public class ProductDetails extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        FragmentManager fm = getSupportFragmentManager();
        ProductDetailsFragment fragment = new ProductDetailsFragment();
        fragment.setArguments(getIntent().getExtras());
        fm.beginTransaction().add(R.id.home_container, fragment, fragment.getClass().toString()).commit();
    }

    public void onBackPress(View V){
        onBackPressed();
    }
}
