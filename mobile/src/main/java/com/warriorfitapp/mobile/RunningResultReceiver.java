package com.warriorfitapp.mobile;

import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;

/**
 * @author Maria Dzyokh
 */
public class RunningResultReceiver extends ResultReceiver {

    private Receiver receiver;

    public RunningResultReceiver(Handler handler) {
        super(handler);
    }

    public RunningResultReceiver(Handler handler, Receiver receiver) {
        super(handler);
        this.receiver = receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public interface Receiver {
        public void onReceiveResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (receiver != null) {
            receiver.onReceiveResult(resultCode, resultData);
        }
    }
}
