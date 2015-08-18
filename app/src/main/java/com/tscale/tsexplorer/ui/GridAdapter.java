package com.tscale.tsexplorer.ui;

/**
 * Created by rd-19 on 2015/8/18.
 */

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tscale.tsexplorer.R;

import java.util.ArrayList;

public class GridAdapter extends BaseAdapter {
    LinearLayout.LayoutParams params;
    private ArrayList<String> mNameList = new ArrayList<>();
    private ArrayList<Drawable> mDrawableList = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context mContext;

    public GridAdapter(Context context, ArrayList<String> nameList, ArrayList<Drawable> drawableList) {
        mNameList = nameList;
        mDrawableList = drawableList;
        mContext = context;
        mInflater = LayoutInflater.from(context);

        params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
    }

    public int getCount() {
        return mNameList.size();
    }

    public Object getItem(int position) {
        return mNameList.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ItemViewTag viewTag;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.form_grid_item, null);

            // construct an item tag
            viewTag = new ItemViewTag((ImageView) convertView.findViewById(R.id.itemImage), (TextView) convertView.findViewById(R.id.itemName));
            convertView.setTag(viewTag);
        } else {
            viewTag = (ItemViewTag) convertView.getTag();
        }

        // set name
        viewTag.mName.setText(mNameList.get(position));

        // set icon
        viewTag.mIcon.setBackgroundDrawable(mDrawableList.get(position));
        //添加后会报错
//        viewTag.mIcon.setLayoutParams(params);
        return convertView;
    }

    class ItemViewTag {
        protected ImageView mIcon;
        protected TextView mName;

        /**
         * The constructor to construct a navigation view tag
         *
         * @param name the name view of the item
         * @param size the size view of the item
         * @param icon the icon view of the item
         */
        public ItemViewTag(ImageView icon, TextView name) {
            this.mName = name;
            this.mIcon = icon;
        }
    }

}
