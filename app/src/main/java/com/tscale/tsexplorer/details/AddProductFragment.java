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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.base.BaseFragment;
import com.tscale.tsexplorer.menu.MenuAdapter;
import com.tscale.tsexplorer.model.Product;
import com.tscale.tsexplorer.scaletask.ScaleAddTask;
import com.tscale.tsexplorer.ui.CharacterParser;
import com.tscale.tsexplorer.util.AsyncCallBackListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.w3c.dom.Text;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import roboguice.inject.InjectView;

public class AddProductFragment extends BaseFragment implements View.OnClickListener {


    private static final int REQUEST = 1;
    private SharedPreferences sp;
    private Product product;

    private String address;
    private String username;
    private String password;

    private CharacterParser characterParser;


    @InjectView(R.id.title)
    private TextView title;


    @InjectView(R.id.details_product_name)
    private ViewGroup details_product_name;
    @InjectView(R.id.details_price)
    private ViewGroup details_price;
    @InjectView(R.id.details_unit)
    private ViewGroup details_unit;
    @InjectView(R.id.details_abbr)
    private ViewGroup details_abbr;
    @InjectView(R.id.details_barcode)
    private ViewGroup details_barcode;
    @InjectView(R.id.add_confirm)
    private Button confirm;


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
        initView();
    }

    private void initView() {

        details_product_name.setOnClickListener(this);
        details_price.setOnClickListener(this);
        details_unit.setOnClickListener(this);
        details_abbr.setOnClickListener(this);
        details_barcode.setOnClickListener(this);
        confirm.setOnClickListener(this);
        TextView product_name = (TextView) details_product_name.findViewById(R.id.item_label);
        product_name.setText(getString(R.string.product_name));
        TextView price = (TextView) details_price.findViewById(R.id.item_label);
        price.setText(getString(R.string.price));
        TextView unit = (TextView) details_unit.findViewById(R.id.item_label);
        unit.setText(getString(R.string.unit_text));
        TextView abbr = (TextView) details_abbr.findViewById(R.id.item_label);
        abbr.setText(getString(R.string.abbr));
        TextView barcode = (TextView) details_barcode.findViewById(R.id.item_label);
        barcode.setText(getString(R.string.barcode));
        handleProductDate(product);
        characterParser = new CharacterParser();

    }

    private void handleProductDate(Product product) {
        if (product != null) {
            TextView product_name = (TextView) details_product_name.findViewById(R.id.item_value);
            product_name.setText(product.getProduct_name());
            TextView price = (TextView) details_price.findViewById(R.id.item_value);
            price.setText(product.getPrice());
            TextView unit = (TextView) details_unit.findViewById(R.id.item_value);
            unit.setText(product.getUnit_text());
            TextView abbr = (TextView) details_abbr.findViewById(R.id.item_value);
            abbr.setText(product.getAbbr());
            TextView barcode = (TextView) details_barcode.findViewById(R.id.item_value);
            barcode.setText(product.getBarcode());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST && resultCode == mActivity.RESULT_OK) {
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
            handleProductDate(product);
        }
    }

    @Override
    public void onClick(View v) {
        Log.d("DETCLI", "" + v.getId());
        Intent intent = new Intent(mActivity, ProductDetailsFix.class);
        Bundle b = new Bundle();
        switch (v.getId()) {
            case R.id.details_price:
                b.putString("key", "price");
                b.putString("value", product.getPrice());
                break;
            case R.id.details_unit:
                b.putString("key", "unit_text");
                b.putString("value", product.getUnit_text());
                break;
            case R.id.details_abbr:
                b.putString("key", "abbr");
                b.putString("value", product.getAbbr());
                break;
            case R.id.details_barcode:
                b.putString("key", "barcode");
                b.putString("value", product.getBarcode());
                break;
            case R.id.details_product_name:
                b.putString("key", "product_name");
                b.putString("value", product.getProduct_name());
                break;
            case R.id.add_confirm:
                saveProduct();
                return;
            default:
                break;
        }
        intent.putExtras(b);
        startActivityForResult(intent, REQUEST);
    }

    private void saveProduct() {
        if (!productCheck()) {
            Toast.makeText(mActivity, "请完成全部信息", Toast.LENGTH_SHORT).show();
            return;
        }
        product.setName_spell(characterParser.getSpelling(product.getProduct_name()));
        product.setAbbr_spell(characterParser.getSpelling(product.getAbbr()));
        product.setProduct_num(product.getBarcode());
        ScaleAddTask scaleAddTask = new ScaleAddTask(mActivity, address, username, password, new AsyncCallBackListener() {
            @Override
            public void onAsyncTaskDone(JSONArray array) {
                try {
                    String id = array.get(0).toString();
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), ProductDetails.class);
                    Bundle b = new Bundle();
                    b.putString("id", id);
                    intent.putExtras(b);
                    startActivity(intent);
                    mActivity.finish();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAsyncTaskCancel() {

            }
        }, product);
        scaleAddTask.execute();

    }

    private boolean productCheck() {
        return (product.getProduct_name() != null &&
                product.getPrice() != null &&
                product.getAbbr() != null &&
                product.getUnit_text() != null &&
                product.getBarcode() != null);
    }
}
