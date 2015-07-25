package com.tscale.tsexplorer.ui;

import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.view.View;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.slidingmenu.SlidingMenu;

import roboguice.activity.RoboFragmentActivity;

public class ScaleChargeActivity extends RoboFragmentActivity {

    public static SlidingMenu menu;

    private SlidingMenu.CanvasTransformer behindTransformer;
    private SlidingMenu.CanvasTransformer aboveTransformer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_layout);
        if (findViewById(R.id.home_container) != null) {
            if (savedInstanceState != null) {
                return;
            } else {
                FragmentManager fm = getSupportFragmentManager();
                ScaleChargeFragment fragment = new ScaleChargeFragment();
                fragment.setArguments(getIntent().getExtras());
                fm.beginTransaction().add(R.id.home_container, fragment, fragment.getClass().toString()).commit();
            }
        }

        initCanvas();
        menu = new SlidingMenu(this);
        menu.setShadowWidth(0);
        menu.setShadowDrawable(R.mipmap.shadow_left);
        menu.setBehindOffset(150);
        menu.setFadeDegree(0.3f);
        menu.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
        menu.attachToActivity(this, SlidingMenu.SLIDING_CONTENT);
        menu.setMenu(R.layout.menu_layout);
        menu.setBehindCanvasTransformer(behindTransformer);
        menu.setAboveCanvasTransformer(aboveTransformer);
        menu.setBackgroundImage(R.mipmap.starnight);
    }

    private void initCanvas() {
        behindTransformer = new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (percentOpen * 0.25 + 0.75);
                canvas.scale(scale, scale, canvas.getWidth() / 2, canvas.getHeight() / 2);
            }

        };
        aboveTransformer = new SlidingMenu.CanvasTransformer() {
            @Override
            public void transformCanvas(Canvas canvas, float percentOpen) {
                float scale = (float) (1 - percentOpen * 0.25);
                canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
            }

        };
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        //disconnect from the scale
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {

        outState.putBundle("savedInfo", getIntent().getExtras());
        super.onSaveInstanceState(outState);
    }

    public static SlidingMenu getSlidingMenu() {
        return menu;
    }

    public void onBackPress(View V) {
        menu.toggle();
    }

//    @Override
//    public void onBackPressed() {
//        onBackPress(null);
//    }
//

}
