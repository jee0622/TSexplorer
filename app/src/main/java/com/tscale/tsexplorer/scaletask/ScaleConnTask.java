package com.tscale.tsexplorer.scaletask;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import com.tscale.tsexplorer.R;
import com.tscale.tsexplorer.util.AsyncCallBackListener;
import com.tscale.tsexplorer.util.SQLUtil;

import org.json.JSONArray;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Created by rd-19 on 2015/7/10.
 */
public class ScaleConnTask extends AsyncTask<Void, Integer, Integer> {

    private ProgressDialog dialog;

    private Context context;

    private String address;

    private String username;

    private String password;

    private String url;

    private JSONArray array;

    private AsyncCallBackListener listener;


    public ScaleConnTask(Context context, String address, String username, String password, AsyncCallBackListener listener) {
        this.context = context;
        this.address = address;
        this.username = username;
        this.password = password;
        this.url = "jdbc:mysql://" + address + "/p101?useUnicode=true&characterEncoding=utf-8";
        this.listener = listener;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        dialog = new ProgressDialog(context);
        dialog.setMessage(context.getResources().getString(R.string.loading));
        dialog.show();
        array = new JSONArray();
    }

    @Override
    protected Integer doInBackground(Void... params) {
        Connection conn = SQLUtil.openConnection(url, username, password);
        if (conn == null) return SQLUtil.CONNECTION_ERROR;
        return 0;
    }

    @Override
    protected void onPostExecute(Integer s) {
        super.onPostExecute(s);
        switch (s) {
            case 0:
                listener.onAsyncTaskDone(array);
                dialog.cancel();
                break;
            case 1:
                Toast.makeText(context, "网络连接错误，请检查账号信息", Toast.LENGTH_SHORT).show();
                listener.onAsyncTaskCancel();
                dialog.cancel();
                break;
            default:
                break;

        }
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        dialog.cancel();
        listener.onAsyncTaskCancel();
    }
}
