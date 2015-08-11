package com.tscale.tsexplorer.scaletask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.tscale.tsexplorer.util.AsyncCallBackListener;
import com.tscale.tsexplorer.util.SQLUtil;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by rd-19 on 2015/7/10.
 */
public class FixProductTask extends AsyncTask<Void, Integer, Integer> {

    private ProgressDialog dialog;

    private Context context;

    private String username;

    private String password;

    private String url;

    private String id;

    private int result;

    private String key;

    private AsyncCallBackListener listener;

    private String value;


    public FixProductTask(Context context, String address, String username, String password, String id, String key, String value, AsyncCallBackListener listener) {
        this.context = context;
        this.id = id;
        this.username = username;
        this.password = password;
        this.key = key;
        this.value = value;
        this.url = "jdbc:mysql://" + address + "/p101?useUnicode=true&characterEncoding=utf-8";
        this.listener = listener;

    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage("请稍候");
        dialog.show();
        result = 0;
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Connection conn = SQLUtil.openConnection(url, username, password);
        if (conn == null) return result;
        result = SQLUtil.fix(conn, id, key, value);
        try {
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
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
