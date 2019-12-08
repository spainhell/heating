package eu.spanhel.heating;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public class NetworkThread extends Thread {
    private Handler handler;

    public NetworkThread(Handler handler) {

        this.handler = handler;
    }

    public void run() {
        // data předáváme přes handler
        Message m = handler.obtainMessage();
        Bundle b = new Bundle();
        b.putFloat("temperature", (float) 23.5);
        m.setData(b);
        m.sendToTarget();


    }

    /*
    v aktivitě:
    new Handler() {
    v metodě handleMessage(Message msg):
        Bundle b = msg.getData();
        a zpracuju data ...
    }



     */
}
