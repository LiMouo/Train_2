package com.ziker.train.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MySqLiteOpenHelper;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ThresholdService extends Service {
    private static String[] item = {"temperature","humidity","LightIntensity","co2","pm2.5","Road"};
    private static String[] names = {"温度","湿度","光照","CO2","pm2.5","道路状态"};
    private static int[] id = {120,121,122,123,124,125};
    private int[] maxNumber = new int[6];
    private int[] nowNumber = new int[6];
    private NotificationManager notificationManager;
    private SQLiteDatabase db ;
    private SharedPreferences sharedPreferences;
    private Thread thread;
    private boolean isFirst = true;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(isFirst){
            isFirst = false;
            thread = new Thread(()->{
                try {
                    while (true){
                        Read();
                        for (int j = 0; j < nowNumber.length; j++) {
                            if(nowNumber[j]>maxNumber[j] && maxNumber[j]>0){
                                showNotification(names[j],maxNumber[j],nowNumber[j],j);
                            }
                        }
                        Thread.sleep(10000);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }
    private void Read(){
        sharedPreferences = getSharedPreferences("ThresholdSeting",MODE_PRIVATE);
        for (int i = 0; i < maxNumber.length; i++) {
            maxNumber[i] = sharedPreferences.getInt(item[i],0);
        }
        db = new MySqLiteOpenHelper(this).getWritableDatabase();
        Cursor cursor = db.query("Environment",null,null,null,null,null,"id desc");
        if(cursor.moveToFirst()){
            for (int i = 0; i < nowNumber.length; i++) {
                nowNumber[i] = cursor.getInt(i+1);
            }
        }
    }
    @Override
    public void onDestroy() {
        if(notificationManager!=null)
            notificationManager.cancelAll();
        if(thread.isAlive()){
            thread.interrupt();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }

    private void showNotification(String name,int set,int now,int i){
        int icon = R.mipmap.ic_launcher;
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=26) {
            NotificationChannel channel = new NotificationChannel("阈值超标", "自动检测网络", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Service Name");
            notificationManager.createNotificationChannel(channel);
            Notification notification = new Notification.Builder(this,"阈值超标")
                    .setContentTitle("阈值超标")
                    .setContentText(name+"报警，阈值 "+set+",当前值 "+now+"\t时间:"+new SimpleDateFormat("HH:mm:ss").format(new Date(System.currentTimeMillis())))
                    .setSmallIcon(icon)
                    .build();
            notificationManager.notify(this.id[i], notification);
        }
    }
}
