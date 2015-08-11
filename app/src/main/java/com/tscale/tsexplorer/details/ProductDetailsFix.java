package com.tscale.tsexplorer.details;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.base.SwipeBackActivity;
import com.tscale.tsexplorer.scaletask.FixProductTask;
import com.tscale.tsexplorer.util.AsyncCallBackListener;
import com.tscale.tsexplorer.zxing.MipcaActivityCapture;

import org.json.JSONArray;
import org.json.JSONException;

import roboguice.inject.InjectView;

/**
 * Created by rd-19 on 2015/7/25.
 */
public class ProductDetailsFix extends SwipeBackActivity implements View.OnClickListener {

    private static final int CAMERA_REQUEST = 10;
    private String id;
    private String key;
    private String value;

    private SharedPreferences sp;
    private String address;
    private String username;
    private String password;
    private AsyncCallBackListener listener;

    @InjectView(R.id.details_guide_textview)
    private TextView guide;

    @InjectView(R.id.details_fix_edittext)
    private EditText editText;

    @InjectView(R.id.details_fix_confirm)
    private Button confirm;

    @InjectView(R.id.scan_button)
    private ImageView camera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_details_fix);
        initView();
    }

    private void initView() {
        Bundle b = getIntent().getExtras();
        id = b.getString("id");
        key = b.getString("key");
        value = b.getString("value");
        String key_zh = getString(getResources().getIdentifier(key, "string", getPackageName()));
        guide.setText("请输入" + key_zh + ":");
        editText.setHint(value);
        confirm.setOnClickListener(this);

        sp = getSharedPreferences("current_count", Context.MODE_PRIVATE);
        address = sp.getString("address", null);
        username = sp.getString("username", null);
        password = sp.getString("password", null);
        listener = new AsyncCallBackListener() {
            @Override
            public void onAsyncTaskDone(JSONArray array) {
                try {
                    int result = array.getInt(0);
                    handleProductDate(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAsyncTaskCancel() {

            }
        };

        if (key_zh.contentEquals(getString(R.string.barcode))) {
            camera.setVisibility(View.VISIBLE);
            camera.setOnClickListener(this);
        }
    }

    private void handleProductDate(int result) {
        if (result > 0) {
            Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            this.finish();
        } else {
            Toast.makeText(this, "修改失败，请稍后重试", Toast.LENGTH_SHORT).show();
        }
    }


    public void onBackPress(View v) {
        onBackPressed();
    }


    @Override
    public void onClick(View v) {
        String valueFixed = editText.getText().toString().trim();

        switch (v.getId()) {
            case R.id.details_fix_confirm:
                if (id == null) {
                    Bundle b = getIntent().getExtras();
                    b.putString("value", valueFixed);
                    Intent intent = new Intent();
                    intent.putExtras(b);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    FixProductTask fixProductTask = new FixProductTask(this, address, username, password, id, key, valueFixed, listener);
                    fixProductTask.execute();
                }
                break;
            case R.id.scan_button:
                Intent intent = new Intent();
                intent.setClass(this, MipcaActivityCapture.class);
                startActivityForResult(intent, CAMERA_REQUEST);
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bundle b = data.getExtras();
            String result = b.getString("result");
            editText.setText(result);
        }
    }
}
