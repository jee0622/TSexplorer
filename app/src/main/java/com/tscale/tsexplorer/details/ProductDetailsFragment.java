package com.tscale.tsexplorer.details;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.base.BaseFragment;
import com.tscale.tsexplorer.menu.MenuAdapter;
import com.tscale.tsexplorer.scaletask.ProductDetailTask;
import com.tscale.tsexplorer.util.AsyncCallBackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProductDetailsFragment extends BaseFragment implements AdapterView.OnItemClickListener {


    private SharedPreferences sp;

    private String product_name;
    private String product_num;
    private String id;

    @InjectView(R.id.details_product_name)
    private TextView details_product_name;
    @InjectView(R.id.details_product_num)
    private TextView details_product_num;

    @InjectView(R.id.title)
    private TextView title;


    @InjectView(R.id.details_listView)
    private ListView detailsListView;

    private List<Map<String, Object>> detailsList = new ArrayList<>();
    private MenuAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        return inflater.inflate(R.layout.product_details_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(R.string.details_title);
        getProductData();
        adapter = new MenuAdapter(mActivity, detailsList, R.layout.details_list_item, new String[]{"key_zh", "value"}, new int[]{R.id.item_label, R.id.item_value});
        detailsListView.setAdapter(adapter);
        detailsListView.setOnItemClickListener(this);
    }

    private void getProductData() {
        Bundle b = getArguments();
        id = b.getString("id");
        sp = mActivity.getSharedPreferences("current_count", Context.MODE_PRIVATE);
        String address = sp.getString("address", null);
        String username = sp.getString("username", null);
        String password = sp.getString("password", null);
        AsyncCallBackListener listener = new AsyncCallBackListener() {
            @Override
            public void onAsyncTaskDone(JSONArray array) {
                try {
                    JSONObject object = array.getJSONObject(0);
                    handleProductDate(object);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAsyncTaskCancel() {
                getActivity().finish();
            }
        };
        ProductDetailTask productDetailTask = new ProductDetailTask(mActivity, id, address, username, password, listener);
        productDetailTask.execute();
    }

    private void handleProductDate(JSONObject object) {
        Log.d(" object.toString()", object.toString());
        if (object != null) {
            try {
                product_num = object.getString("product_num");
                product_name = object.getString("product_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            detailsList.clear();
            Iterator<String> iterator = object.keys();
            while (iterator.hasNext()) {
                try {
                    Map<String, Object> map = new HashMap<>();
                    String key = iterator.next().toString();
                    String key_zh = getString(getResources().getIdentifier(key, "string", mActivity.getPackageName()));
                    map.put("key", key);
                    map.put("key_zh", key_zh);
                    map.put("value", object.getString(key));
                    detailsList.add(map);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            adapter.notifyDataSetChanged();
            details_product_name.setText(product_name);
            details_product_num.setText(product_num);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long _id) {
        Toast.makeText(mActivity, detailsList.get(position).get("key").toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mActivity, ProductDetailsFix.class);
        Bundle b = getArguments();
        b.putString("key", detailsList.get(position).get("key").toString());
        b.putString("value", detailsList.get(position).get("value").toString());
        intent.putExtras(b);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        getProductData();
    }
}
