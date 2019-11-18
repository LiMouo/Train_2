package com.ziker.train.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.Adapter.AccountMangerAdapter;
import com.ziker.train.R;
import com.ziker.train.Utils.Info.AccountInfo;
import com.ziker.train.Utils.ToolClass.Menu;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AccountMangerActivity extends MyAppCompatActivity {
    private static int[] icons = new int[]{R.drawable.baoma,R.drawable.benchi,R.drawable.biyadi,R.drawable.bentian};
    private static String[] names = new String[]{username,username,username,username};
    private static String[] car_id = new String[]{"京A123456","川A147369","渝A123789","广A159357"};
    private List<AccountInfo> AccountInfo = new ArrayList<>();
    private List<Integer> Checkeds = new ArrayList<>();
    private RecyclerView RV_main;
    private AccountMangerAdapter adapter;
    private Handler handler = new Handler();
    /*对话框控件*/
    private AlertDialog.Builder builder;
    private View DialogView;
    private TextView t_car_id;
    private EditText e_money;
    private Button btn_save, btn_cancel;
    private AlertDialog alertDialog;
    private Integer money = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_manger);
        Menu menu = super.SetMenu(this,"账户管理",R.layout.account_more);
        View more = menu.getExpand();
        View t_recharge = more.findViewById(R.id.T_recharge);
        t_recharge.setOnClickListener(v->{
            Integer[] id = new Integer[Checkeds.size()];
            for (int i = 0; i < Checkeds.size(); i++) {
                id[i] =  Checkeds.get(i);
            }
            SaveData(id);
        });
        more.findViewById(R.id.T_saveList).setOnClickListener(v->{
            Intent intent = new Intent(this,UserInfoActivity.class);
            Bundle bundle = new Bundle();
            bundle.putBoolean("isList",true);
            intent.putExtras(bundle);
            startActivity(intent);
        });
        InitView();
        BindData();
        SetData();
    }

    private void InitView(){
        RV_main = findViewById(R.id.RV_main);
        builder = new AlertDialog.Builder(this);
        DialogView = LayoutInflater.from(this).inflate(R.layout.accountmanager_dialog, null);
        t_car_id = DialogView.findViewById(R.id.t_car_id);
        e_money = DialogView.findViewById(R.id.e_money);
        btn_save = DialogView.findViewById(R.id.btn_save);
        btn_cancel = DialogView.findViewById(R.id.btn_cancel);
        builder.setView(DialogView).setCancelable(false);
        alertDialog = builder.create();
    }

    private void BindData(){
        RV_main.setLayoutManager(new LinearLayoutManager(this));
        RV_main.addItemDecoration(new MyItemDecoration());
        adapter = new AccountMangerAdapter(this, AccountInfo, position -> SaveData(new Integer[]{position}), list -> Checkeds = list);
        RV_main.setAdapter(adapter);
        Tools.SetButtonColor(btn_save);
        Tools.SetButtonColor(btn_cancel);
    }


    /**
     *更新数据
     */
    private void SetData(){
        new Thread(()->{
            AccountInfo.clear();
            for (int i = 0; i < names.length; i++) {
                AccountInfo accountInfo = new AccountInfo();
                accountInfo.setIcon(icons[i]);
                accountInfo.setCar_id(car_id[i]);
                accountInfo.setCar_user(names[i]);
                Cursor cursor = DB.query("Account",null,"CARID='"+car_id[i]+"'",null,null,null,"id desc");
                if(cursor.moveToFirst()){
                    int money = cursor.getInt(cursor.getColumnIndex("balance"));
                    accountInfo.setMoney(money);
                }else {
                    accountInfo.setMoney(0);
                }
                AccountInfo.add(accountInfo);
            }
            handler.post(()->adapter.notifyDataSetChanged());
        }).start();
    }
    /**
     *保存数据
     */
    private void SaveData(Integer[] positions){
        if(positions.length > 0){
            alertDialog.show();
            if(positions.length>1){
                String car_id="";
                for (int i = 0; i < positions.length; i++)
                    car_id += AccountInfo.get(Checkeds.get(positions[i])).getCar_id();
                String finalCar_id1 = car_id;
                t_car_id.setText(finalCar_id1);
            } else{
                t_car_id.setText(AccountInfo.get(positions[0]).getCar_id());
            }
            e_money.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) { }
                @Override
                public void afterTextChanged(Editable s) {
                    String text = s.toString();
                    int len = s.toString().length();
                    if (len > 0 && text.startsWith("0")) {
                        s.replace(0,1,"");
                    }
                    if(!Tools.isEmpty(s.toString()))
                        money = Integer.parseInt(s.toString());
                }
            });
            btn_save.setOnClickListener((view1)->{
                e_money.setText("");
                alertDialog.dismiss();
                if(money != null){
                    ProgressDialog progressDialog = Tools.WaitDialog(this,"正在充值...");
                    progressDialog.setCancelable(true);
                    progressDialog.show();
                    for (int i = 0; i < positions.length; i++) {
                        ContentValues values = new ContentValues();
                        values.put("CARID", AccountInfo.get(positions[i]).getCar_id());
                        values.put("MONEY",money);
                        values.put("balance",money+ AccountInfo.get(positions[i]).getMoney());
                        values.put("user", username);
                        values.put("image",Tools.BitmapToByte(BitmapFactory.decodeResource(getResources(),icons[positions[i]]),40));
                        values.put("date",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(System.currentTimeMillis())));
                        DB.insert("Account",null,values);
                    }
                    handler.postDelayed(()-> {
                        progressDialog.dismiss();
                        Toast.makeText(this,"充值完成",Toast.LENGTH_LONG).show();
                        SetData();
                    },1000);
                    money = null;
                }
            });
            btn_cancel.setOnClickListener((view2)->{
                e_money.setText("");
                alertDialog.dismiss();
            });//取消按钮，隐藏弹窗
        }
    }

    /**
     *下边框实现
     */
    class MyItemDecoration extends RecyclerView.ItemDecoration{
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            outRect.set(0,0,0,1);
        }
    }
}
