package com.ziker.train.Activity;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
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
    private static int[] icons = new int[]{R.drawable.car1,R.drawable.car2,R.drawable.car3,R.drawable.car4};
    private static String[] names = new String[]{"张三","李四","王五","赵六"};
    private static String[] car_id = new String[]{"京123456","川147369","渝123789","广159357"};
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
    private Button btn_save,btn_cancle;
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
        btn_cancle = DialogView.findViewById(R.id.btn_cancle);
        builder.setView(DialogView).setCancelable(false);
        alertDialog = builder.create();
    }

    private void BindData(){
        RV_main.setLayoutManager(new LinearLayoutManager(this));
        RV_main.addItemDecoration(new MyItemDecoration());
        adapter = new AccountMangerAdapter(this, AccountInfo, position -> SaveData(new Integer[]{position}), list -> Checkeds = list);
        RV_main.setAdapter(adapter);
        Tools.SetButtonColor(btn_save);
        Tools.SetButtonColor(btn_cancle);
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
            new Thread(()->{
                handler.post(()->alertDialog.show());
                if(positions.length>1){
                    String car_id="";
                    for (int i = 0; i < positions.length; i++)
                        car_id += AccountInfo.get(Checkeds.get(positions[i])).getCar_id();
                    String finalCar_id1 = car_id;
                    handler.post(()->t_car_id.setText(finalCar_id1));
                } else{
                    handler.post(()->t_car_id.setText(AccountInfo.get(positions[0]).getCar_id()));
                }
                e_money.setOnEditorActionListener((v1, actionId, event) -> {
                    char[] number = v1.getText().toString().toCharArray();
                    for (int i = 0; i < number.length; i++) {
                        if(number[i]=='0')
                            continue;
                        money = Integer.parseInt(v1.getText().toString());
                        if(money >999)
                            money = 999;
                        handler.post(()->e_money.setText(money+""));
                        break;
                    }
                    return false;
                });
                btn_save.setOnClickListener((view1)->{
                    e_money.setText("");
                    alertDialog.dismiss();
                    if(money != 0){
                        ProgressDialog progressDialog = new ProgressDialog(this);
                        progressDialog.setTitle("请稍等...");
                        progressDialog.setMessage("正在充值");
                        progressDialog.setCancelable(true);
                        handler.post(()-> progressDialog.show());
                        for (int i = 0; i < positions.length; i++) {
                            ContentValues values = new ContentValues();
                            values.put("CARID", AccountInfo.get(positions[i]).getCar_id());
                            values.put("MONEY",money);
                            values.put("balance",money+ AccountInfo.get(positions[i]).getMoney());
                            values.put("user", user);
                            values.put("date",new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss").format(new Date(System.currentTimeMillis())));
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
                btn_cancle.setOnClickListener((view2)->{
                    e_money.setText("");
                    alertDialog.dismiss();
                });//取消按钮，隐藏弹窗
            }).start();
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
