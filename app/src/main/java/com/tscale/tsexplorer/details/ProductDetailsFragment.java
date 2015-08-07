package com.tscale.tsexplorer.details;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.base.BaseFragment;
import com.tscale.tsexplorer.scaletask.ProductDetailTask;
import com.tscale.tsexplorer.util.AsyncCallBackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import roboguice.inject.InjectView;

/**
 * A placeholder fragment containing a simple view.
 */
public class ProductDetailsFragment extends BaseFragment implements View.OnClickListener {


    private static final int REQUEST = 1;
    private SharedPreferences sp;

    private String product_name;
    private String product_num;
    private String id;

    private JSONObject object;

    @InjectView(R.id.title)
    private TextView title;
    @InjectView(R.id.details_product_name)
    private TextView details_product_name;
    @InjectView(R.id.details_product_num)
    private TextView details_product_num;
    @InjectView(R.id.details_price)
    private ViewGroup details_price;
    @InjectView(R.id.details_unit)
    private ViewGroup details_unit;
    @InjectView(R.id.details_barcode)
    private ViewGroup details_barcode;
    @InjectView(R.id.details_abbr)
    private ViewGroup details_abbr;

    private TextView label;
    private TextView value;

    private List<Map<String, Object>> detailsList = new ArrayList<>();

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
        details_price.setOnClickListener(this);
        details_unit.setOnClickListener(this);
        details_abbr.setOnClickListener(this);
        details_barcode.setOnClickListener(this);
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
                    object = array.getJSONObject(0);
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
                details_product_num.setText(product_num);
                details_product_name.setText(product_name);
                label = (TextView) details_price.findViewById(R.id.item_label);
                value = (TextView) details_price.findViewById(R.id.item_value);
                label.setText(getString(getResources().getIdentifier("price", "string", mActivity.getPackageName())));
                value.setText(object.getString("price"));
                label = (TextView) details_unit.findViewById(R.id.item_label);
                value = (TextView) details_unit.findViewById(R.id.item_value);
                label.setText(getString(getResources().getIdentifier("unit_text", "string", mActivity.getPackageName())));
                value.setText(object.getString("unit_text"));
                label = (TextView) details_abbr.findViewById(R.id.item_label);
                value = (TextView) details_abbr.findViewById(R.id.item_value);
                label.setText(getString(getResources().getIdentifier("abbr", "string", mActivity.getPackageName())));
                value.setText(object.getString("abbr"));
                label = (TextView) details_barcode.findViewById(R.id.item_label);
                value = (TextView) details_barcode.findViewById(R.id.item_value);
                label.setText(getString(getResources().getIdentifier("barcode", "string", mActivity.getPackageName())));
                value.setText(object.getString("barcode"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onClick(View v) {
        Log.d("DETCLI", "" + v.getId());
        Intent intent = new Intent(mActivity, ProductDetailsFix.class);
        Bundle b = getArguments();
        try {
            switch (v.getId()) {
                case R.id.details_price:
                    b.putString("key", "price");
                    b.putString("value", object.getString("price"));
                    break;
                case R.id.details_unit:
                    b.putString("key", "unit_text");
                    b.putString("value", object.getString("unit_text"));
                    break;
                case R.id.details_abbr:
                    b.putString("key", "abbr");
                    b.putString("value", object.getString("abbr"));
                    break;
                case R.id.details_barcode:
                    b.putString("key", "barcode");
                    b.putString("value", object.getString("barcode"));
                    break;
                default:
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        intent.putExtras(b);
        startActivityForResult(intent, REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST && resultCode == mActivity.RESULT_OK) getProductData();
    }
}
