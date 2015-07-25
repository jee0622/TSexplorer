package com.tscale.tsexplorer.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.base.BaseFragment;
import com.tscale.tsexplorer.base.DropEditText;
import com.tscale.tsexplorer.scaletask.ScaleConnTask;
import com.tscale.tsexplorer.sqlite.DBHelper;
import com.tscale.tsexplorer.util.AsyncCallBackListener;
import com.tscale.tsexplorer.util.TextUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class HomeActivityFragment extends BaseFragment implements View.OnClickListener {


    private Button connBtn;

    private String address;

    private String username;

    private String password;

    private DropEditText address_edit;

    private EditText username_edit;

    private EditText password_edit;

    private SharedPreferences sp;

    private DBHelper helper;

    private Cursor cursor;

    private List<Map<String,String>> mapList = new ArrayList<>();

    private BaseAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mActivity = getActivity();
        return inflater.inflate(R.layout.home_fragment_layout, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        connBtn = (Button) getView().findViewById(R.id.address_conn);
        connBtn.setOnClickListener(this);
        address_edit = (DropEditText) getView().findViewById(R.id.address_edit);
        username_edit = (EditText) getView().findViewById(R.id.username_edit);
        password_edit = (EditText) getView().findViewById(R.id.password_edit);
        sp = mActivity.getSharedPreferences("current_count", Context.MODE_PRIVATE);
        address = sp.getString("address", null);
        username = sp.getString("username", null);
        password = sp.getString("password", null);
        address_edit.setText(address);
        username_edit.setText(username);
        password_edit.setText(password);
        password_edit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER) connBtn.callOnClick();
                return false;
            }
        });
        helper = new DBHelper(mActivity);
        cursor = helper.select();

        if (cursor.getCount() > 0) {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                Map<String,String> map = new HashMap<>();
                //add count to droplistview
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    Log.d("Cursor", cursor.getColumnName(i) + ":" + cursor.getString(i));
                    map.put(cursor.getColumnName(i), cursor.getString(i));
                }
                mapList.add(map);
                cursor.moveToNext();
            }

        }

        adapter = new AddressAdapter();
        address_edit.setAdapter(adapter);
    }

        @Override
        public void onClick (View v){
            switch (v.getId()) {
                case R.id.address_conn:
                    getConnection();
                    break;
                default:
                    break;
            }
        }

    private void getConnection() {
        address = String.valueOf(address_edit.getText()).trim();
        username = String.valueOf(username_edit.getText()).trim();
        password = String.valueOf(password_edit.getText()).trim();
        Log.d("HomeActivityFrag", address + username + password);
        if (address != null && TextUtil.isIP(address)) {
            ScaleConnTask scaleConnTask = new ScaleConnTask(mActivity, address, username, password, new AsyncCallBackListener() {
                @Override
                public void onAsyncTaskDone(JSONArray array) {
                    saveCurrentCount();
                    Log.d("getConnection","start Intent");
                    Intent intent = new Intent();
                    Bundle b = new Bundle();
                    b.putString("username", username);
                    b.putString("password", password);
                    b.putString("address", address);
                    intent.putExtras(b);
                    intent.setClass(mActivity, ScaleChargeActivity.class);
                    startActivity(intent);
                }

                @Override
                public void onAsyncTaskCancel() {
                }
            });
            scaleConnTask.execute();
        } else {
            Toast.makeText(mActivity, "address is required", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void saveCurrentCount() {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("username", username);
        editor.putString("password", password);
        editor.putString("address", address);
        editor.commit();
        cursor = helper.selectByAddress(address);
        Log.d("DBHelper", "cursor is null?: " + cursor.getCount());
        if (cursor.getCount() <= 0) {
            Log.d("DBHelper", "save: " + address + username + password);
            helper.insert(address, username, password);
        } else {
            Log.d("DBHelper", "update: " + address + username + password);
            cursor.moveToFirst();
            String id = cursor.getString(0);
            helper.updateById(id, address, username, password);
        }
    }

    public class AddressAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mapList.size();
        }

        @Override
        public Object getItem(int position) {
            return mapList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView tv = new TextView(getActivity());
            tv.setText(mapList.get(position).get("address"));
            return tv;
        }

        public void setText(Map<String,String> map){
            address_edit.setText(map.get("address"));
            username_edit.setText(map.get("username"));
            password_edit.setText(map.get("password"));
        }

    }
}
