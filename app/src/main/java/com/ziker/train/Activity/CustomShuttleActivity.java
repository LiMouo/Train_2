package com.ziker.train.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ExpandableListView;
import android.widget.ImageView;

import com.google.gson.Gson;
import com.ziker.train.Adapter.CustomShuttleAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.Info.CustomShuttleInfo;
import com.ziker.train.Utils.ToolClass.Menu;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.MyDialog;
import com.ziker.train.Utils.ToolClass.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomShuttleActivity extends MyAppCompatActivity {
    private static String UrlInfo = "GetBusInfo.do";
    private static String UrlLine = "GetUserBusLine.do";
    private ExpandableListView EL_main;
    private CustomShuttleAdapter adapter;
    private List<CustomShuttleInfo> ListData = new ArrayList<>();
    private Menu menu;
    private ImageView i_QRCode;
    private Gson gson = new Gson();
    private Handler handler = new Handler();
    private Thread QRthread = null,ChangeImage = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_shuttle);
        super.SetAnimation(FLAG_NO_ANIMATION);
        menu = super.SetMenu(this, "定制班车", R.layout.customshuttle_more).SetOnBackClick(R.drawable.back, () -> finish());
        InitView();
        BindData();
    }

    private void InitView() {
        EL_main = findViewById(R.id.EL_main);
    }

    private void BindData() {
        MyDialog myDialog = new MyDialog(this, 0.5, 0.6, R.layout.myappcompat_iconimage);
        i_QRCode = menu.findViewById(R.id.i_QRcode);
        i_QRCode.setOnLongClickListener(v -> {
            myDialog.Do(dialog -> {
                ChangeImage = new Thread(() -> {
                    while (true) {
                        try {
                            handler.post(() -> {
                                ImageView image = dialog.findViewById(R.id.image);
                                image.setImageDrawable(i_QRCode.getDrawable());
                            });
                            Thread.sleep(3000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
                ChangeImage.start();
            }).show();
            return true;
        });/*二维码长按*/
        menu.findViewById(R.id.t_order).setOnClickListener(v -> startActivity(new Intent(this, CustomShuttleOrderListActivity.class)));/*我的订单*/
        adapter = new CustomShuttleAdapter(this, ListData);
        EL_main.setAdapter(adapter);
        new Thread(() -> {
            try {
                String Data = Tools.SendPostRequest(UrlInfo, new HashMap());
                JSONObject object = new JSONObject(Data);
                JSONArray array = object.getJSONArray("ROWS_DETAIL");
                ListData.clear();
                for (int i = 0; i < array.length(); i++) {
                    JSONObject d = array.getJSONObject(i);
                    CustomShuttleInfo info = gson.fromJson(d.toString(), CustomShuttleInfo.class);
                    ListData.add(info);
                }
                handler.post(() -> adapter.notifyDataSetChanged());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }).start();/*获取班车信息*/

        QRthread = new Thread(() -> {
            try {
                while (true) {
                    boolean isFirst = true;
                    String Data = Tools.SendPostRequest(UrlLine, new HashMap());
                    JSONArray array = new JSONObject(Data).getJSONArray("ROWS_DETAIL");
                    for (int i = 0; i < array.length(); i++) {
                        if (array.getJSONObject(i).getInt("Flag") == 1) {
                            isFirst = false;
                            int Id = array.getJSONObject(i).getInt("Id");
                            handler.post(() -> {
                                i_QRCode.setImageBitmap(Tools.QRCode("{\"UserName\":\"user1\",\"Id\":[" + Id + "]}", 100, 100));
                            });
                            Thread.sleep(3000);
                        }
                    }
                    if (isFirst)
                        Thread.sleep(3000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        });/*动态改变二维码*/
        QRthread.start();

    }

    @Override
    protected void onDestroy() {
        if (QRthread != null && QRthread.isAlive()) {
            QRthread.interrupt();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (ChangeImage != null && ChangeImage.isAlive()) {
            ChangeImage.interrupt();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
