package com.tscale.tsexplorer.util;

import org.json.JSONArray;

/**
 * Created by rd-19 on 2015/7/13.
 */
public interface AsyncCallBackListener {

    void onAsyncTaskDone(JSONArray array);

    void onAsyncTaskCancel();
}
