package com.ziker.train.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Button;
import android.widget.EditText;

import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.MySqLiteOpenHelper;
import com.ziker.train.Utils.ToolClass.Tools;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ViolationActivity extends MyAppCompatActivity {
    private static String AllInfoUrl = "GetAllCarPeccancy.do";
    private List<JSONObject> AllInfo = new ArrayList<>();
    private List<JSONObject> QueryInfo = new ArrayList<>();
    private Button btn_query;
    private EditText e_number;
    private SQLiteDatabase db;
    private Handler handler = new Handler();
    private Thread thread;
    private ProgressDialog waitDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation);
        super.SetMenu(this, "车辆违章", null);
        InitView();
        BindData();
        QueryData();
    }

    private void InitView() {
        btn_query = findViewById(R.id.btn_query);
        e_number = findViewById(R.id.e_number);
        db = new MySqLiteOpenHelper(this).getWritableDatabase();
    }

    private void BindData() {
        Tools.SetButtonColor(btn_query);
        btn_query.setOnClickListener(v -> {
            btn_query.setEnabled(false);
            new Thread(() -> {
                try {
                    QueryInfo.clear();
                    String number = e_number.getText().toString();
                    boolean isNull = true;
                    for (int i = 0; i < AllInfo.size(); i++) {
                        if (AllInfo.get(i).getString("carnumber").equals("鲁"+number)) {
                            QueryInfo.add(AllInfo.get(i));
                            isNull = false;
                        }
                    }
                    if (isNull){
                        if(number.equals(""))
                            handler.post(()->Tools.Toast(this,"请输入查询车牌好吗？",true));
                        else
                            handler.post(()-> Tools.Toast(this,"没有查询到鲁"+number+"车的违章数据！",true));
                    } else {
                        handler.post(()->{
                            waitDialog = Tools.WaitDialog(this,"正在查询");
                            waitDialog.show();
                        });
                        Cursor cursor = db.query("Violation",new String[]{"carnumber"},"carnumber == '鲁"+number+"'",null,null,null,null);
                        if(!(cursor.getCount() > 0)){
                            for (int i = 0; i < QueryInfo.size(); i++) {
                                ContentValues values = new ContentValues();
                                JSONObject data = QueryInfo.get(i);
                                values.put("carnumber",data.getString("carnumber"));
                                values.put("pcode",data.getString("pcode"));
                                values.put("paddr",data.getString("paddr"));
                                values.put("pdatetime",data.getString("pdatetime"));
                                db.insert("Violation",null,values);
                            }
                        }
                        handler.post(()->{
                            waitDialog.dismiss();
                            Bundle b = getIntent().getExtras();
                            if(b == null){
                                Intent intent = new Intent(this,Violation_ResultActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putString("carnumber","鲁"+number);
                                intent.putExtras(bundle);
                                startActivity(intent);
                            }else {
                                super.Logout();
//                                Intent intent = new Intent();
//                                Bundle bundle = new Bundle();
//                                bundle.putString("carnumber","鲁"+number);
//                                intent.putExtras(bundle);
//                                setResult(RESULT_OK,intent);
//                                super.SetAnimation(FLAG_NO_ANIMATION);
//                                finish();
                            }
                        });
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                handler.post(() -> btn_query.setEnabled(true));
            }).start();
        });
    }

    private void QueryData() {
        thread = new Thread(() -> {
            while (true) {
                try {
                    String Data = Tools.SendRequest(AllInfoUrl, new HashMap());
                    JSONObject jsonObject = new JSONObject(Data);
                    JSONArray jsonArray = jsonObject.getJSONArray("ROWS_DETAIL");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        AllInfo.add(object);
                    }
                    break;
                } catch (IOException e) {
                    try {
                        Thread.sleep(100);//这个线程只需要执行一次，但是第一次进入如果没有连接到正确的网络，请求会有异常，设定一直请求，请求成功则退出，连续请求间的间隙
                    } catch (InterruptedException ex) {
                        ex.printStackTrace();
                    }
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount()==0 && getIntent().getExtras()!=null){
            Intent intent = new Intent();
            Bundle bundle1 = new Bundle();
            intent.putExtras(bundle1);
            setResult(RESULT_CANCELED,intent);
            super.SetAnimation(FLAG_NO_ANIMATION);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        if (thread != null && thread.isAlive()) {
            thread.interrupt();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        super.onDestroy();
    }
}
