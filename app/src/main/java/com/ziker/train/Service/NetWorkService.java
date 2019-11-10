package com.ziker.train.Service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class NetWorkService extends Service {
    private Context context;
    private Thread thread;

    public NetWorkService(Context context) {
        this.context = context;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(thread != null && thread.isAlive()){
            thread.interrupt();
        }
    }
}
