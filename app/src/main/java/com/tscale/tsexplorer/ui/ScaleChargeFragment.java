package com.tscale.tsexplorer.ui;


import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.base.BaseFragment;
import com.tscale.tsexplorer.base.WrapListView;
import com.tscale.tsexplorer.details.AddProduct;
import com.tscale.tsexplorer.details.ProductDetails;
import com.tscale.tsexplorer.menu.MenuAdapter;
import com.tscale.tsexplorer.scaletask.ScaleQueryTask;
import com.tscale.tsexplorer.util.AsyncCallBackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectView;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScaleChargeFragment extends BaseFragment implements AdapterView.OnItemClickListener {


    private SortAdapter adapter;


    private List<SortModel> commodityList = new ArrayList<>();


    @InjectView(R.id.commodity_listView)
    private ListView commodityListView;
    @InjectView(R.id.sidebar)
    private SideBar sideBar;

    @InjectView(R.id.filter_edit)
    private ClearEditText mClearEditText;
    @InjectView(R.id.dialog)
    private TextView dialog;

    private String address;

    private String username;

    private String password;

    private AsyncCallBackListener listener;

    @InjectView(R.id.title)
    private TextView title;

    //option
    private List<Map<String, Object>> mapList = new ArrayList<>();
    @InjectView(R.id.titlebar_option)
    private ImageView option;
    private PopupWindow pop;
    private final String[] menuFrom = new String[]{"icon", "text"};
    private final int[] menuTo = new int[]{R.id.option_item_icon, R.id.option_item_text};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        return inflater.inflate(R.layout.scale_charge_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getAsyncData();
        sideBar.setTextView(dialog);

        //设置右侧触摸监听
        sideBar.setOnTouchingLetterChangedListener(new SideBar.OnTouchingLetterChangedListener() {

            @Override
            public void onTouchingLetterChanged(String s) {
                //该字母首次出现的位置
                int position = adapter.getPositionForSection(s.charAt(0));
                if (position != -1) {
                    commodityListView.setSelection(position);
                }

            }
        });
        mClearEditText.clearFocus();



        //根据输入框输入值的改变来过滤搜索
        mClearEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //当输入框里面的值为空，更新为原来的列表，否则为过滤数据列表
                Log.d("addTextChangedListener", "change to " + s);
                filterData(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


        title = (TextView) getView().findViewById(R.id.title);
        title.setText(address);

        option = (ImageView) getView().findViewById(R.id.titlebar_option);
        option.setVisibility(View.VISIBLE);

        initPopWindow();
        option.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!pop.isShowing()) {
                    pop.showAsDropDown(option);
                } else {
                    pop.dismiss();
                }
            }
        });
    }

    private void getAsyncData() {
        listener = new AsyncCallBackListener() {
            @Override
            public void onAsyncTaskDone(JSONArray array) {
                Log.d("doInBackground", array.toString());
                getCommodityDate(array);
                adapter.notifyDataSetChanged();
                Log.d("getAsyncData", "" + commodityList.size() + "," + adapter.getCount());
            }

            @Override
            public void onAsyncTaskCancel() {
                mActivity.finish();
            }
        };

        Bundle b = getArguments();
        if (b != null) {
            address = b.getString("address");
            username = b.getString("username");
            password = b.getString("password");
        } else {
            SharedPreferences sp = getActivity().getSharedPreferences("current_count", Context.MODE_PRIVATE);
            address = sp.getString("address", null);
            username = sp.getString("username", null);
            password = sp.getString("password", null);
        }

        //AsnycTask to get connect the scale
        ScaleQueryTask scaleQueryTask = new ScaleQueryTask(mActivity, address, username, password, listener);
        scaleQueryTask.execute();
        //Dialog to identify

        adapter = new SortAdapter(mActivity, commodityList);
        commodityListView.setAdapter(adapter);
        commodityListView.setOnItemClickListener(this);
    }


    private void initPopWindow() {
        WrapListView optionListView = (WrapListView) mActivity.getLayoutInflater().inflate(R.layout.pop_view, null);
        pop = new PopupWindow(optionListView, LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        pop.setOutsideTouchable(true);
        pop.setTouchable(true);
        pop.setFocusable(true);
        getMenuDate();
        MenuAdapter mAdapter = new MenuAdapter(mActivity, mapList, R.layout.option_item, menuFrom, menuTo);
        optionListView.setAdapter(mAdapter);
        pop.setBackgroundDrawable(getResources().getDrawable(R.drawable.abc_cab_background_internal_bg));
        optionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("SclaeCharge", "optionListView" + position);
                switch (position) {
                    case 0:
                        addDialog();
                        break;
                    case 1:
                        break;
                    default:
                        break;
                }
                pop.dismiss();
            }
        });
    }

    private void getCommodityDate(JSONArray array) {
        int len = array.length();
        commodityList.clear();
        if (len > 0) {
            for (int i = 0; i < len; i++) {
                try {
                    JSONObject object = array.getJSONObject(i);
                    SortModel model = new SortModel();
                    model.setId(object.getString("id"));
                    model.setName(object.getString("name"));
                    String pinyin = object.getString("name_spell");
                    String sortString = pinyin.length() > 0 ? pinyin.substring(0, 1).toUpperCase() : "#";

                    // 正则表达式，判断首字母是否是英文字母
                    if (sortString.matches("[A-Z]")) {
                        model.setSortLetters(sortString.toUpperCase());
                    } else {
                        model.setSortLetters("#");
                    }
                    commodityList.add(model);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    private void addDialog() {
        /*new AlertDialog.Builder(mActivity)
                .setTitle("请输入").setIcon(android.R.drawable.ic_dialog_info)
                .setView(new EditText(mActivity)).setPositiveButton("确定", optionListener)
                .setNegativeButton("取消", optionListener).show();*/
        Intent intent = new Intent();
        intent.setClass(getActivity(), AddProduct.class);
        startActivity(intent);
    }


    DialogInterface.OnClickListener optionListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d("ScaleCharge", "dialog " + which);
            switch (which) {
                case -1:
                    break;
            }
        }
    };

    private void getMenuDate() {

        String[] texts = getResources().getStringArray(R.array.option_text);
        TypedArray drawables = getResources().obtainTypedArray(R.array.option_icon);
        int[] icons = new int[drawables.length()];

        if (texts.length == drawables.length()) {
            for (int i = 0; i < texts.length; i++) {
                Map<String, Object> map = new HashMap<>();
                icons[i] = drawables.getResourceId(i, 0);
                map.put("icon", icons[i]);
                map.put("text", texts[i]);
                mapList.add(map);
            }
        }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent();
        intent.setClass(getActivity(), ProductDetails.class); 
        Bundle b = new Bundle();
        b.putString("id", commodityList.get(position).getId().toString());
        intent.putExtras(b);
        startActivity(intent);
    }


    /**
     * 根据输入框中的值来过滤数据并更新ListView
     *
     * @param filterStr
     */
    private void filterData(String filterStr) {
        ScaleQueryTask scaleQueryTask = new ScaleQueryTask(mActivity, address, username, password, listener, filterStr);
        scaleQueryTask.execute();
    }
}
