package com.tscale.tsexplorer.menu;

import android.content.Context;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

/**
 * Created by rd-19 on 2015/7/10.
 */
public class MenuAdapter extends SimpleAdapter {


    public MenuAdapter(Context context, List<Map<String,Object>> list, int resource, String[] from, int[] to) {
        super(context, list, resource, from, to);


    }




}
