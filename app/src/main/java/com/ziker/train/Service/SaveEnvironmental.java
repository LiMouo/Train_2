package com.ziker.train.Service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import androidx.annotation.Nullable;

import com.ziker.train.Utils.ToolClass.MySqLiteOpenHelper;
import com.ziker.train.Utils.ToolClass.Tools;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SaveEnvironmental extends Service {
    private static String EUrl = "GetAllSense.do";
    private static String RUrl = "GetRoadStatus.do";
    private static String[] item = {"temperature", "humidity", "LightIntensity", "co2", "pm2.5"};
    private SQLiteDatabase db;
    private Thread thread;
    private int[] list = new int[6];
    private boolean isLive = false;

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
        if (!isLive) {
            isLive = true;
            thread = new Thread(() -> {
                while (true) {
                    long start = System.currentTimeMillis();
                    String url;
                    try {
                        for (int i = 0; i <= 1; i++) {
                            Map map = new HashMap();
                            if (i < 1) {
                                url = EUrl;
                            } else {
                                map.put("RoadId", 1);
                                url = RUrl;
                            }
                            String Data = Tools.SendPostRequest(url, map);
                            JSONObject jsonObject = new JSONObject(Data);
                            if (i < 1) {
                                for (int j = 0; j < item.length; j++)
                                    list[j] = Integer.parseInt(jsonObject.getString(item[j]));
                            } else {
                                list[item.length] = Integer.parseInt(jsonObject.getString("Status"));
                                break;
                            }
                        }
                        saveData();
                        long end = System.currentTimeMillis();
                        long time = end - start;
                        if (time > 3000) {
                        } else {
                            Thread.sleep(3000 - (end - start));
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void saveData() {
        db = MySqLiteOpenHelper.getInstance(this).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("temperature", list[0]);
        values.put("humidity", list[1]);
        values.put("LightIntensity", list[2]);
        values.put("co2", list[3]);
        values.put("pm2_5", list[4]);
        values.put("Road", list[5]);
        values.put("date", new SimpleDateFormat("mm:ss").format(new Date(System.currentTimeMillis())));
        db.insert("Environment", null, values);
        Cursor cursor = db.query("Environment", null, null, null, null, null, null);
        if (cursor.getCount() > 20) {
            db.delete("Environment", "id in (select id from Environment limit 0," + (cursor.getCount() - 20) + ")", null);
        }
    }

    @Override
    public void onDestroy() {
        thread.interrupt();
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
