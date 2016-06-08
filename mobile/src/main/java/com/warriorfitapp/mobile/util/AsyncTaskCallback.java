package com.warriorfitapp.mobile.util;

public interface AsyncTaskCallback<R> {
    void onPreExecute();

    void onPostExecute(R result);
}
