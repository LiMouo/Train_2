package com.ziker.train.Utils.ToolClass;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.ziker.train.Activity.AccountMangerActivity;
import com.ziker.train.Activity.BusQueryActivity;
import com.ziker.train.Activity.CustomShuttleActivity;
import com.ziker.train.Activity.ETCActivity;
import com.ziker.train.Activity.EnvironmentalActivity;
import com.ziker.train.Activity.ManagerActivity;
import com.ziker.train.Activity.RedGreenActivity;
import com.ziker.train.Activity.RedGreenControllerActivity;
import com.ziker.train.Activity.SubwayQueryActivity;
import com.ziker.train.Activity.ThresholdSettingActivity;
import com.ziker.train.Activity.TrafficActivity;
import com.ziker.train.Activity.TripActivity;
import com.ziker.train.Activity.UserInfoActivity;
import com.ziker.train.Activity.VORActivity;
import com.ziker.train.Activity.ViolationActivity;
import com.ziker.train.Activity.WeatherInformationActivity;
import com.ziker.train.MainActivity;
import com.ziker.train.R;
import com.ziker.train.Utils.Login.LoginActivity;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class MyAppCompatActivity extends AppCompatActivity {
    public final static int FLAG_NO_ANIMATION = 0;
    public final static int FLAG_DEFAULT = 1;
    public final static int FLAG_GRADUAL = 2;
    private final static int LOGIN_OK = 0x1001;
    private final static int LOGIN_PERMISSION_OK = 0x1002;
    public final static String TAG = "MyAppCompatActivity";
    private final static int[] LayoutID = {R.id.T_Mainactivity, R.id.T_ETCactivity, R.id.T_RedGreedactivity, R.id.T_Manageractivity, R.id.T_VORactivity, R.id.T_Environmental, R.id.T_ThresholdSeting,
            R.id.T_Trip, R.id.T_Account, R.id.T_BusQuery, R.id.T_RedGreenController, R.id.T_Violation, R.id.T_Traffic,R.id.T_SubwayQuery,R.id.T_WeatherInformation,R.id.T_CustomShuttle};//列表项ID
    private final static int[] ImageID = {R.id.image_1, R.id.image_2, R.id.image_3, R.id.image_4, R.id.image_5, R.id.image_6, R.id.image_7,
            R.id.image_8, R.id.image_9, R.id.image_10, R.id.image_11, R.id.image_12, R.id.image_13,R.id.image_14,R.id.image_15,R.id.image_16};//列表项图片ID
    private final static int[] ImageSourcesID = {R.drawable.amap, R.drawable.amap, R.drawable.amap, R.drawable.amap, R.drawable.amap, R.drawable.amap, R.drawable.amap, R.drawable.amap, R.drawable.amap, R.drawable.amap,
            R.drawable.amap, R.drawable.amap, R.drawable.amap, R.drawable.amap,R.drawable.amap};//列表项图片资源ID
    private final static Class[] activitiesID = {MainActivity.class, ETCActivity.class, RedGreenActivity.class, ManagerActivity.class, VORActivity.class, EnvironmentalActivity.class, ThresholdSettingActivity.class,
            TripActivity.class, AccountMangerActivity.class, BusQueryActivity.class, RedGreenControllerActivity.class, ViolationActivity.class, TrafficActivity.class, SubwayQueryActivity.class,
            WeatherInformationActivity.class,CustomShuttleActivity.class};//列表项对应Activity
    private final static List<Integer> idList = new ArrayList<>();//菜单项列表ID,这儿有个Int[]，还用个list，是因为绑定点击事件时方便用indexOf查找是否存在，不用手动写逻辑
    private final static List<Class<?>> ActivityList = new ArrayList<>();//跳转Activity列表

    public static boolean NetworkState = true;//网络连接状态
    public static String Ip, Port, UrlHead, user = "user1";
    public static String username;
    public static byte[] userImage = null;//登录用户名头像
    public static boolean Sex = true;//性别标识，true为男，false为女
    public static int Trip_money = 0;//汽车余额阈值设置

    private static Integer Menu_Icon = R.drawable.menu;//菜单图标
    private static List<Activity> LiveActivities = new ArrayList<>();//Activity活动栈
    private static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    public SQLiteDatabase DB;
    public static boolean isFirst = true;//初始化标识

    private Menu menu;
    private Thread NetWork = null;
    private Handler handler = new Handler();
    private int ANIMATION = FLAG_GRADUAL;

    static {//静态代码块，只加载一次，提高性能
        for (int value : LayoutID)
            idList.add(value);
        for (Class aClass : activitiesID)
            ActivityList.add(aClass);
    }

    public void SetPort(String ip, String port) {
        Ip = ip;
        Port = port;
        UrlHead = "http://" + Ip + ":" + Port + "/transportservice/action/";
        editor.putString("Ip", Ip);
        editor.putString("Port", Port);
        editor.apply();
    }

    public void SetAnimation(int FLAG) {
        ANIMATION = FLAG;
    }

    /* 刷新用户信息*/
    public void reFreshUserInfo() {
        View v_menu = menu.getMenu();
        TextView t_username = v_menu.findViewById(R.id.t_username);//用户姓名
        TextView t_sex = v_menu.findViewById(R.id.t_sex);  //用户性别
        CircleImageView i_icon = v_menu.findViewById(R.id.i_icon);  //用户头像
        LinearLayout l_user = v_menu.findViewById(R.id.l_user);//整个用户块儿
        t_username.setText(username);//设置名字
        t_sex.setText(Sex ? "男" : "女");//设置性别

        Cursor cursor = DB.query("User", null, "username='" + username + "'", null, null, null, null);
        if (cursor.moveToFirst())
            userImage = cursor.getBlob(4);
        //设置头像，默认为null，就根据性别设置男女头像
        if(userImage != null){
            i_icon.setImageBitmap(Tools.BitmapFromByte(userImage));
        }else if (!Sex){//女
            Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.touxiang_1);
            userImage = Tools.BitmapToByte(b,30);
            i_icon.setImageBitmap(Tools.BitmapFromByte(userImage));
        }else { //男
            Bitmap b = BitmapFactory.decodeResource(getResources(),R.drawable.touxiang_2);
            userImage = Tools.BitmapToByte(b,30);
            i_icon.setImageBitmap(Tools.BitmapFromByte(userImage));
        }
        l_user.setOnClickListener(v->{
            startActivity(new Intent(this, UserInfoActivity.class));
            for (int j = 1; j < LiveActivities.size(); j++)
                LiveActivities.get(j).finish();
        });
        i_icon.setOnLongClickListener(v -> {
            MyDialog dialog = new MyDialog(this, 0.4, null, R.layout.login_dialog);//自定义对话框
            dialog.Do(d -> { //
                TextView t_info = d.findViewById(R.id.t_info);
                t_info.setText("去更换头像吗？");
                Button btn_ok= d.findViewById(R.id.btn_ok);
                btn_ok.setOnClickListener(b1 -> {
                    dialog.dismiss();
                    if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                            ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED ) {
                        PhotoUtils.openPictrue(this, LOGIN_OK);
                    } else {
                        //没有权限，申请权限。
                        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.CAMERA}, LOGIN_PERMISSION_OK);
                    }
                });
                Button btn_cancel = d.findViewById(R.id.btn_cancel);
                btn_cancel.setOnClickListener(b1 -> dialog.dismiss());
                Tools.SetButtonColor(btn_cancel,2);
                Tools.SetButtonColor(btn_ok,3);
            }).show();
            return true;
        });
        i_icon.setOnClickListener(v->{
            MyDialog dialog = new MyDialog(this,0.5,1.0,R.layout.myappcompat_iconimage);
            dialog.Do(image->{
                ImageView i = image.findViewById(R.id.image);
                i.setImageDrawable(i_icon.getDrawable());
            }).show();
        });
        Button btn_logout = v_menu.findViewById(R.id.btn_logout);
        btn_logout.setOnClickListener(v -> Logout());
        Tools.SetButtonColor(btn_logout, 0);

    }

    /*退出登录*/
    public void Logout() {//退出登录
        username = "";
        userImage = null;
        for (int j = 0; j < LiveActivities.size(); j++)
            LiveActivities.get(j).finish();
        this.ANIMATION = FLAG_DEFAULT;
        editor.putBoolean("isLogin", false);
        editor.apply();
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }

    /**
     * 设置菜单
     * @param context  上下文
     * @param Title  菜单显示的标题
     * @param expand   拓展布局
     * @return
     */
    public Menu SetMenu(Context context, String Title, Integer expand) {
        View Top_View = findViewById(android.R.id.content);//拿到当前布局
        ViewGroup parent = (ViewGroup) Top_View.getParent();//拿到当前布局father
        parent.removeView(Top_View);//把当前布局从它father移除
        menu = new Menu(context, Top_View, R.layout.myappcompat_exception, Title, Menu_Icon, expand, R.layout.left_menu);
//        menu.getTitle().setBackgroundColor(Color.parseColor("#87CEFA"));
        menu.getTitle().setBackground(getDrawable(R.drawable.weatherinformation_tianqi1));
        reFreshUserInfo();//更新右侧用户信息
//        ChangeNetWork();
        View v_menu = menu.getMenu();//拿到菜单
        for (int i = 0; i < ImageID.length; i++) {//为菜单点击项设置图标
            ImageView v = v_menu.findViewById(ImageID[i]);
            if (i >= ImageSourcesID.length)//图标不够多，为后面的设置为最后一个图标
                v.setImageDrawable(getDrawable(ImageSourcesID[ImageSourcesID.length - 1]));
            else
                v.setImageDrawable(getDrawable(ImageSourcesID[i]));
        }
        try {
            if (idList.size() > 0)//菜单项ID大于0
                menu.SetMenuItem(idList, ActivityList, v -> {//为菜单项设置跳转，参数为菜单项ID，跳转Activity，结束除栈根的Activity，栈根即MainActivity
                    for (int j = 1; j < LiveActivities.size(); j++)
                        LiveActivities.get(j).finish();
                });
        } catch (Exception e) {
            e.printStackTrace();
        }
        parent.addView(menu);
        return menu;
    }

    /*检查网络*/
    private void ChangeNetWork() {
        if (getClass() != MainActivity.class && getClass() != LoginActivity.class) {
            Button btn_ok = menu.getExceptionView().findViewById(R.id.btn_go);
            Button btn_refresh = menu.getExceptionView().findViewById(R.id.btn_refresh);
            btn_ok.setOnClickListener(v -> {
                Tools.Toast(this, "wait me create it!", false);
            });
            btn_refresh.setOnClickListener(v -> Tools.Toast(this, "刷新有个屁用,有用我早消失了！", false));
            NetWork = new Thread(() -> {
                try {
                    while (true) {
                        handler.post(() -> menu.SetNetWorkState(NetworkState));
                        Thread.sleep(1200);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
            NetWork.start();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case LOGIN_PERMISSION_OK:
                    PhotoUtils.openPictrue(this, 2);
                    break;
                case LOGIN_OK://访问相册完成回调
                    Bitmap bitmap = null;
                    try {
                        bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(data.getData()));
                        ContentValues values = new ContentValues();
                        values.put("image",Tools.BitmapToByte(bitmap,30));
                        DB.update("user",values,"username='" + username + "'",null);
                        reFreshUserInfo();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LiveActivities.add(this);
        DB = MySqLiteOpenHelper.getInstance(this).getWritableDatabase();
        if (isFirst) {//初始化常亮
            sp = getSharedPreferences("MyAppCompatActivity", MODE_PRIVATE);
            editor = sp.edit();
            Ip = sp.getString("Ip", "127.0.0.1");
            Port = sp.getString("Port", "8088");
            UrlHead = "http://" + Ip + ":" + Port + "/transportservice/action/";
            username = sp.getString("username", "");
            SharedPreferences s = getSharedPreferences("UserInfo",MODE_PRIVATE);
            Trip_money = s.getInt("TripMoney",0);
            isFirst = false;
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
//        if (LiveActivities.size() > 1)
//            ChangeNetWork();
    }

    /*设置跳转动画*/
    @Override
    protected void onPause() {
        super.onPause();
        switch (ANIMATION) {
            case FLAG_NO_ANIMATION://没有动画
                overridePendingTransition(0, 0);
                break;
            case FLAG_GRADUAL://上下渐隐渐显
                overridePendingTransition(R.anim.gradual_in, R.anim.gradual_out);
                break;
            case FLAG_DEFAULT://默认动画，左进右出
                break;
        }
        if (NetWork != null && NetWork.isAlive()) {
            try {
                NetWork.interrupt();
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        /*跳转后隐藏主界面菜单栏，并且活动栈数量大于1，为什么要大于1，当我写注释的时候忘嘞，好像有个bug需要大于1*/
        if (menu != null && LiveActivities.size() > 1)
            menu.hide_menu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(getClass() == MainActivity.class){
            editor.putBoolean("isLogin", false);
            editor.putLong("OutTime",System.currentTimeMillis());
            editor.apply();
        }
        LiveActivities.remove(this);
    }
}

