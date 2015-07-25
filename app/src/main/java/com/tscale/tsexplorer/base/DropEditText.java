package com.tscale.tsexplorer.base;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.ui.HomeActivityFragment;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by rd-19 on 2015/7/20.
 */
public class DropEditText extends FrameLayout implements View.OnClickListener, AdapterView.OnItemClickListener {
    private EditText mEditText;  // 输入框
    private ImageView mDropImage; // 右边的图片按钮
    private PopupWindow mPopup; // 点击图片弹出popupwindow
    private WrapListView mPopView; // popupwindow的布局

    private int mDrawableLeft;
    private int mDropMode; // flower_parent or wrap_content
    private String mHit;

    public DropEditText(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DropEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        LayoutInflater.from(context).inflate(R.layout.edit_layout, this);
        mPopView = (WrapListView) LayoutInflater.from(context).inflate(R.layout.pop_view, null);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.DropEditText, defStyle, 0);
        mDrawableLeft = ta.getResourceId(R.styleable.DropEditText_drawableRight, R.mipmap.arrow_down);
        mDropMode = ta.getInt(R.styleable.DropEditText_dropMode, 0);
        mHit = ta.getString(R.styleable.DropEditText_hint);
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        mEditText = (EditText) findViewById(R.id.dropview_edit);
        mDropImage = (ImageView) findViewById(R.id.dropview_image);

        mEditText.setSelectAllOnFocus(true);
        mDropImage.setImageResource(mDrawableLeft);

        if (!TextUtils.isEmpty(mHit)) {
            mEditText.setHint(mHit);
        }

        mDropImage.setOnClickListener(this);
        mPopView.setOnItemClickListener(this);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 如果布局发生改
        // 并且dropMode是flower_parent
        // 则设置ListView的宽度
        if (changed && 0 == mDropMode) {
            mPopView.setListWidth(getMeasuredWidth());
        }
    }

    /**
     * 设置Adapter
     *
     * @param adapter ListView的Adapter
     */
    public void setAdapter(BaseAdapter adapter) {
        mPopView.setAdapter(adapter);

        mPopup = new PopupWindow(mPopView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        mPopup.setBackgroundDrawable(new ColorDrawable(color.transparent));
        mPopup.setFocusable(true); // 让popwin获取焦点
    }

    /**
     * 获取输入框内的内容
     *
     * @return String content
     */
    public String getText() {
        return mEditText.getText().toString();
    }

    public void setText(String text) {
        mEditText.setText(text);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.dropview_image) {
            if (mPopup.isShowing()) {
                mPopup.dismiss();
                return;
            }

            mPopup.showAsDropDown(this, 0, 5);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        HomeActivityFragment.AddressAdapter adapter = (HomeActivityFragment.AddressAdapter) mPopView.getAdapter();
        Map<String, String> map = (Map<String, String>) adapter.getItem(position);
        adapter.setText(map);
//        mEditText.setText(map.get("address"));
        mPopup.dismiss();
    }
}