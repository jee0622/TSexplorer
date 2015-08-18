package com.tscale.tsexplorer.ui;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.base.BaseFragment;

import java.util.ArrayList;

import roboguice.inject.InjectView;

/**
 * Created by rd-19 on 2015/8/18.
 */
public class FormFragment extends BaseFragment implements AdapterView.OnItemClickListener {


    private ArrayList<String> nameList;

    private ArrayList<Drawable> drawableList;

    @InjectView(R.id.form_grid)
    private GridView formGrid;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        return inflater.inflate(R.layout.scale_form_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        TextView title = (TextView) getView().findViewById(R.id.title);
        title.setText(mActivity.getString(R.string.reprot_title));

        nameList = new ArrayList<>();
        drawableList = new ArrayList<>();
        getGridItemLists();
        GridAdapter adapter = new GridAdapter(mActivity, nameList, drawableList);
        Log.d("FormFragment", String.valueOf(formGrid == null));
        formGrid.setAdapter(adapter);
        formGrid.setOnItemClickListener(this);
    }

    private void getGridItemLists() {
        String[] texts = getResources().getStringArray(R.array.form_grid_item_text);
        TypedArray drawables = getResources().obtainTypedArray(R.array.form_grid_item_img);
        int[] icons = new int[drawables.length()];

        if (texts.length == drawables.length()) {
            for (int i = 0; i < texts.length; i++) {
                icons[i] = drawables.getResourceId(i, 0);
                nameList.add(i, texts[i]);
                drawableList.add(i, getResources().getDrawable(icons[i]));
            }
        }
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        int index = position + 1;// id是从0开始的，所以需要+1
        Toast.makeText(mActivity, "你按下了选项：" + index, Toast.LENGTH_SHORT).show();
    }
}
