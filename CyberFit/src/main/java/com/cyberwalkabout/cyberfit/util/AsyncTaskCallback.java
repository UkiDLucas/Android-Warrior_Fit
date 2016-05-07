package com.cyberwalkabout.cyberfit.util;

public interface AsyncTaskCallback<R> {
    void onPreExecute();

    void onPostExecute(R result);
}
