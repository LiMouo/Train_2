package com.ziker.train.Utils.Login;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.ziker.train.MainActivity;
import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.Menu;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.MyDialog;
import com.ziker.train.Utils.ToolClass.Tools;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends MyAppCompatActivity {
    private EditText le_username, le_password, le_user, le_pass1, le_pass2, le_telephone, le_idCardNumber;
    private CheckBox c_remember, c_auto;
    private RadioButton g_sex;
    private Button btn_login, btn_register, btn_back, btn_registered;
    private LinearLayout l_login, l_register;
    private Menu menu;
    private SharedPreferences sp;
    private SharedPreferences.Editor editor;
    private Handler handler;
    private Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        menu = super.SetMenu(this, "用户登录", R.layout.login_more);
        super.SetAnimation(FLAG_NO_ANIMATION);//设置跳转动画
        InitView();
        BindData();
    }

    /*初始化控件*/
    private void InitView() {
        sp = getSharedPreferences("MyAppCompatActivity", MODE_PRIVATE);
        editor = sp.edit();
        handler = new Handler();
        bundle = getIntent().getExtras();
        le_username = findViewById(R.id.e_username);
        le_password = findViewById(R.id.e_password);
        btn_login = findViewById(R.id.btn_login);
        btn_register = findViewById(R.id.btn_register);
        c_remember = findViewById(R.id.c_remember);
        c_auto = findViewById(R.id.c_auto);

        l_login = findViewById(R.id.l_login);
        l_register = findViewById(R.id.l_register);
        le_user = findViewById(R.id.le_user);
        le_pass1 = findViewById(R.id.le_pass1);
        le_pass2 = findViewById(R.id.le_pass2);
        le_telephone = findViewById(R.id.le_telephone);
        le_idCardNumber = findViewById(R.id.le_idCardNumber);
        g_sex = findViewById(R.id.lg_sex1);
        btn_back = findViewById(R.id.lbtn_back);
        btn_registered = findViewById(R.id.lbtn_register);
    }

    /*绑定数据*/
    private void BindData() {
        c_auto.setChecked(sp.getBoolean("auto", false));//绑定自动登录状态
        c_remember.setChecked(sp.getBoolean("remember", false));//绑定记住密码选择框状态
        le_username.setText(sp.getString("username", null));//设置用户上一次登录的用户
        if (c_remember.isChecked())
            le_password.setText(sp.getString("password", null));//设置用户上一次登录的密码
        menu.SetOnBackClick(null, null);//隐藏菜单图标，设置为一个标题栏，取消滑动菜单功能
        menu.getExpand().findViewById(R.id.l_setting).setOnClickListener(v -> {
            MyDialog dialog = new MyDialog(this, 0.6, 0.6, R.layout.networksetting);
            dialog.setCancelable(false);
            dialog.Do(setting -> NetWork(setting)).show();
        });//expand内容点击事件
        btn_login.setOnClickListener(v -> Login());//登录点击事件
        btn_registered.setOnClickListener(v -> Register());//注册区注册按钮点击事件
        btn_register.setOnClickListener(v -> {
            l_login.setVisibility(View.GONE);
            l_register.setVisibility(View.VISIBLE);
        });//登录区注册按钮点击事件
        btn_back.setOnClickListener(v -> {
            le_user.setText("");
            le_pass1.setText("");
            le_pass2.setText("");
            le_telephone.setText("");
            l_login.setVisibility(View.VISIBLE);
            l_register.setVisibility(View.GONE);
        });//注册区返回按钮点击事件
        /*六个输入框不允许输入空格*/
        addChange(le_username);
        addChange(le_password);
        addChange(le_user);
        addChange(le_pass1);
        addChange(le_pass2);
        addChange(le_telephone);
        /*自动登录的实现*/
        if (bundle != null && bundle.getBoolean("boot") && c_auto.isChecked()) {
            btn_login.callOnClick();
            Tools.Toast(this, "自动登录成功,你可能离上一次登录有点久,自动注销了", true);
        } else if (bundle != null && bundle.getBoolean("auto") && c_auto.isChecked()) {
            btn_login.callOnClick();
            Tools.Toast(this, "自动登录成功", true);
        }
    }

    /**
     * 登录,代码流程：
     * 先拿到输入信息，与输入不为空与数据库用户信息比对
     * 对比成功，进入登录状态，显示等待对话框，更新SP信息，跳转
     * 对比不成功记录用户名存在状态，显示提示信息
     * 用户名不存在显示Dialog提示注册，跳转注册或取消
     */
    private void Login() {
        String e_name = le_username.getText().toString();
        String e_pass = le_password.getText().toString();
        if (!Tools.isEmpty(e_name) && !Tools.isEmpty(e_pass)) {//输入不为空进入对比
            Cursor cursor = DB.query("user", null, null, null, null, null, null);
            boolean userExisting = false;//用户存在标识
            if (cursor.moveToFirst()) {
                do {
                    String username = cursor.getString(1);//数据库用户名称
                    String password = cursor.getString(2);//数据库用户密码
                    if (e_name.equals(username) && e_pass.equals(password)) {
                        ProgressDialog dialog = Tools.WaitDialog(this, "正在登录...");
                        dialog.show();
                        MyAppCompatActivity.Sex = cursor.getInt(3) == 0 ? true : false;//设置用户性别,0为男，1为女
                        MyAppCompatActivity.username = username;
                        editor.putString("username", username);//更新登录用户
                        editor.putString("password", password);//更新登录密码
                        editor.putBoolean("auto", c_auto.isChecked());//更新自动登录状态
                        editor.putBoolean("remember", c_remember.isChecked());//更新记住密码状态
                        editor.putBoolean("isLogin", true);//更新登录状态
                        editor.apply();
                        handler.postDelayed(() -> {
                            dialog.dismiss();
                            Intent intent = new Intent(this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }, 600);
                        return;
                    } else if (e_name.equals(username)) {//用户存在设置状态
                        userExisting = true;
                    }
                } while (cursor.moveToNext());
            }
            if (Tools.isEmpty(e_name) || Tools.isEmpty(e_pass)) {//账号或密码未输入
                Tools.Toast(this, "账号或密码不能为空", false);
            } else if (userExisting) {//账号存在
                Tools.Toast(this, "密码错误", false);
            } else {//账号不存在
                MyDialog myDialog = new MyDialog(this, 0.4, null, R.layout.login_dialog);
                myDialog.Do(v -> {
                    TextView text = v.findViewById(R.id.t_info);
                    text.setText("账号尚未注册,立即注册吗");
                    v.findViewById(R.id.btn_ok).setOnClickListener(ok -> {//确认点击事件
                        btn_register.callOnClick();
                        le_user.setText(e_name);//注册账号设置为登录账号
                        v.dismiss();
                    });
                    v.findViewById(R.id.btn_cancel).setOnClickListener(ok -> v.dismiss());
                }).show();
            }
        }
    }

    /**
     * 注册，代码流程：
     * 先检查输入完整性
     * 检查通过检查用户名是否存在
     * 用户输入信息通过写库，dialog提示登录
     */
    private void Register() {
        Cursor cursor = DB.query("user", null, null, null, null, null, null);
        String res_name = le_user.getText().toString();
        String res_pass1 = le_pass1.getText().toString();
        String res_pass2 = le_pass2.getText().toString();
        String res_telephone = le_telephone.getText().toString();
        String res_idcardNumber = le_idCardNumber.getText().toString();
        String num = "[1][345678]\\d{9}";//手机号正则规则
        if (Tools.isEmpty(res_name) || Tools.isEmpty(res_pass1) || Tools.isEmpty(res_pass2) || Tools.isEmpty(res_telephone) || Tools.isEmpty(res_idcardNumber)) {
            Tools.Toast(this, "请输入完整信息好吗", false);
            return;
        }
        if (!res_pass1.equals(res_pass2)) {
            Tools.Toast(this, "两次密码不一样，请检查输入", false);
            return;
        }
        if (res_name.length() < 4) {
            Tools.Toast(this, "用户名长度需大于5", false);
            return;
        }
        if (res_pass1.length() < 5) {
            Tools.Toast(this, "密码长度需大于5", false);
            return;
        }
        if (res_telephone.length() != 11 || !res_telephone.matches(num)) {
            Tools.Toast(LoginActivity.this, "请输入正确的手机号码", false);
            return;
        }
        if (res_idcardNumber.length() != 18) {
            Tools.Toast(this, "请输入正确18位身份证号", false);
            return;
        }
        if (cursor.moveToFirst()) {
            do {
                String username = cursor.getString(1);//取得数据库中的用户名称
                if (res_name.equals(username)) {
                    Tools.Toast(this, "用户名已被占用，请换一个注册", false);
                    return;
                }
            } while (cursor.moveToNext());
        }

        ContentValues values = new ContentValues();
        values.put("username", res_name);
        values.put("password", res_pass1);
        values.put("sex", g_sex.isChecked() ? 0 : 1);//0表示男，1表示女
        values.put("telephone", res_telephone);
        values.put("idcardnumber", res_idcardNumber);
        values.put("registrytime", new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis())));
        DB.insert("user", null, values);
        handler.postDelayed(() -> {
            MyDialog myDialog = new MyDialog(this, 0.4, null, R.layout.login_dialog);
            myDialog.Do(v -> {
                TextView view = v.findViewById(R.id.t_info);
                view.setText("注册成功，立即去登录吗");
                v.findViewById(R.id.btn_ok).setOnClickListener(ok -> {
                    btn_back.callOnClick();
                    le_username.setText(res_name);
                    le_password.setText(res_pass1);
                    v.dismiss();
                });
                v.findViewById(R.id.btn_cancel).setOnClickListener(ok -> v.dismiss());
            }).show();
        }, 500);
    }

    /**
     * 网络设置，代码流程:
     * 先取出IP地址为editText 设置hint显示，默认为127.0.0.1
     * IP地址为全局静态变量，存放于MyAppcompatActivity，初始化于MyAppcompatActivity的OnCreate
     * 设置EditText 的输入检查
     * btn_ok保存设置，SetPort为于MyAppcompatActivity中，可用于其他地方调用设置IP和端口
     * btn_cancel 隐藏对话框
     * @param dialog dialog对象
     */
    private void NetWork(Dialog dialog) {
        Button btn_ok = dialog.findViewById(R.id.btn_ok);
        Button btn_cancel = dialog.findViewById(R.id.btn_cancel);
        EditText[] e_number = new EditText[4];
        int[] ID = {R.id.e_number1, R.id.e_number2, R.id.e_number3, R.id.e_number4};
        String[] ips = Ip.split("\\."); //分段取出IP
        for (int i = 0; i < e_number.length; i++) {
            e_number[i] = dialog.findViewById(ID[i]);
            e_number[i].setHint(ips[i]);
        }
        MaxNumber(e_number);
        EditText e_port = dialog.findViewById(R.id.e_port);
        e_port.setHint(Port);
        btn_ok.setOnClickListener(v -> {
            String inputIp = "";
            String inputPort;
            for (int i = 0; i < e_number.length; i++) {
                if (e_number[i].getText().length() == 0) {
                    inputIp += e_number[i].getHint().toString();
                } else{
                    inputIp += e_number[i].getText().toString();
                }
                if(i<3) inputIp += ".";
            }
            if(e_port.getText().length()>0) inputPort = e_port.getText().toString();
            else inputPort = e_port.getHint().toString();
            super.SetPort(inputIp,inputPort);
            dialog.dismiss();
        });
        btn_cancel.setOnClickListener(v -> dialog.dismiss());
    }

    /**
     * 完成检查输入，输入位数达到3自动切换下一输入框，删除剩余0自动切换上一输入框
     * @param e
     */
    private void MaxNumber(EditText[] e) {
        for (int i = 0; i < e.length; i++) {
            int finalI = i;
            e[i].addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0){
                        if (s.charAt(s.length() - 1) == '.') {
                            if (finalI < 3 && s.length() > 1)
                                e[finalI + 1].requestFocus();
                            e[finalI].setText(s.toString().substring(0, s.length() - 1));
                            e[finalI].setSelection(start);
                        }
                    }
                }
                @Override
                public void afterTextChanged(Editable s) {
                    if (s.length() == 3 && !s.toString().contains(".")) {
                        if (finalI < 3)
                            e[finalI + 1].requestFocus();
                    } else if (s.length() == 0 )
                        if (finalI > 0)
                            e[finalI - 1].requestFocus();
                }
            });
        }
    }

    /**
     * 输入框不允许输入空格检查
     *
     * @param e EditText对象
     */
    private void addChange(EditText e) {
        e.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().contains(" ")) {
                    String[] str = s.toString().split(" ");
                    String str1 = "";
                    for (int i = 0; i < str.length; i++) {
                        str1 += str[i];
                    }
                    e.setText(str1);
                    e.setSelection(start);
                }
            }
            @Override
            public void afterTextChanged(Editable s) { }
        });
    }
}

