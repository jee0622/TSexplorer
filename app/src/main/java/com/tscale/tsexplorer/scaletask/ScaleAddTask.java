package com.tscale.tsexplorer.scaletask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.tscale.tsexplorer.model.Product;
import com.tscale.tsexplorer.util.AsyncCallBackListener;
import com.tscale.tsexplorer.util.SQLUtil;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by rd-19 on 2015/7/16.
 */
public class ScaleAddTask extends AsyncTask<Void, Integer, Integer> {

    private ProgressDialog dialog;

    private Context context;

    private String address;

    private String username;

    private String password;

    private String url;

    private JSONArray array;

    private AsyncCallBackListener listener;

    private Product product;

    private int result;

    public ScaleAddTask(Context context, String address, String username, String password, AsyncCallBackListener listener,Product product) {
        this.context = context;
        this.address = address;
        this.username = username;
        this.password = password;
        this.url = "jdbc:mysql://" + address + "/p101?useUnicode=true&characterEncoding=utf-8";
        this.listener = listener;
        this.product = product;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("请稍候");
        dialog.show();
        array = new JSONArray();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Connection conn = SQLUtil.openConnection(url, username, password);
        if (conn == null) return SQLUtil.CONNECTION_ERROR;
        result = SQLUtil.add(conn, product);
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if (result > 0) {
            JSONArray array = new JSONArray();
            array.put(result);
            listener.onAsyncTaskDone(array);
            dialog.cancel();
        } else {
            dialog.cancel();
            listener.onAsyncTaskCancel();
        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        dialog.cancel();
        listener.onAsyncTaskCancel();
    }
}
