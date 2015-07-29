package com.tscale.tsexplorer.details;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.tscale.tsexplorer.model.Product;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roboguice.inject.InjectView;

public class AddProductFragment extends BaseFragment implements AdapterView.OnItemClickListener {


    private SharedPreferences sp;
    private Product product;

    private String address;
    private String username;
    private String password;


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
        product = new Product();
        Log.d("AddProFrg", "onCreateView" + "new Product");
        return inflater.inflate(R.layout.add_product_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title.setText(R.string.details_title);
        sp = mActivity.getSharedPreferences("current_count", Context.MODE_PRIVATE);
        address = sp.getString("address", null);
        username = sp.getString("username", null);
        password = sp.getString("password", null);
        adapter = new MenuAdapter(mActivity, detailsList, R.layout.details_list_item, new String[]{"key_zh", "value"}, new int[]{R.id.item_label, R.id.item_value});
        detailsListView.setAdapter(adapter);
        detailsListView.setOnItemClickListener(this);
        initView();
    }

    private void initView() {
        handleProductDate(product);
    }

    private void handleProductDate(Product product) {
        if (product != null) {
            detailsList.clear();
            Map<String, Object> productMap = product.productToMap();
            Set<String> keys = productMap.keySet();
            Iterator<String> iterator = keys.iterator();
            while (iterator.hasNext()) {
                Map<String, Object> map = new HashMap<>();
                String key = iterator.next().toString();
                String key_zh = getString(getResources().getIdentifier(key, "string", mActivity.getPackageName()));
                map.put("key", key);
                map.put("key_zh", key_zh);
                map.put("value", productMap.get(key) == null ? "" : productMap.get(key));
                detailsList.add(map);
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long _id) {
        Toast.makeText(mActivity, detailsList.get(position).get("key").toString(), Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(mActivity, ProductDetailsFix.class);
        Bundle b = new Bundle();
        b.putString("key", detailsList.get(position).get("key").toString());
        b.putString("value", detailsList.get(position).get("value").toString());
        intent.putExtras(b);
        startActivityForResult(intent, 0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == 2) {
            Bundle result = data.getExtras();
            String key = result.getString("key");
            String value = result.getString("value");
            try {
                Field field = product.getClass().getField(key);
                field.setAccessible(true);
                try {
                    field.set(product, value);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
            initView();
        }
    }

}
