package com.ziker.train.Activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;

import com.ziker.train.R;
import com.ziker.train.Service.ThresholdService;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

public class ThresholdSettingActivity extends MyAppCompatActivity implements CompoundButton.OnCheckedChangeListener {
    private static String[] item = {"temperature","humidity","LightIntensity","co2","pm2.5","Road"};
    private static int[] editTextsId = {R.id.edit_1,R.id.edit_2,R.id.edit_3,R.id.edit_4,R.id.edit_5,R.id.edit_6};
    private EditText[] editTexts = new EditText[6];
    private Switch S_main;
    private Button btn_save;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_threshold_seting);
        super.SetMenu(this,"阈值设置",null);
        InitView();
        BindData();
    }

    private void InitView(){
        sharedPreferences = getSharedPreferences("ThresholdSeting",MODE_PRIVATE);
        editor = sharedPreferences.edit();
        S_main = findViewById(R.id.S_main);
        for (int i = 0; i < editTexts.length; i++)
            editTexts[i] = findViewById(editTextsId[i]);
        btn_save = findViewById(R.id.btn_save);
    }

    private void BindData(){
        S_main.setOnCheckedChangeListener(this);
        SaveOrRead(false);
        btn_save.setOnClickListener((view)->SaveOrRead(true));
        Tools.SetButtonColor(btn_save);
    }

    private void SaveOrRead(boolean isSave){
        if(isSave){
            for (int i = 0; i < item.length; i++) {
                if(!Tools.isEmpty(editTexts[i].getText().toString())){
                    String n = editTexts[i].getText().toString();
                    editor.putInt(item[i],Integer.parseInt(n));
                }

            }
            editor.apply();
        }else {
            for (int i = 0; i < item.length; i++) {
                int temp = sharedPreferences.getInt(item[i],0);
                if(temp!=0){
                        editTexts[i].setText(temp+"");
                }
            }
            if(sharedPreferences.getBoolean("Switch",false)){
                S_main.setChecked(true);
                S_main.setText("开");
            }else {
                S_main.setChecked(false);
                S_main.setText("关");
            }
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        Intent intentService = new Intent(this, ThresholdService.class);
        if(isChecked){
            S_main.setText("开");
            this.startService(intentService);
        }
        else{
            S_main.setText("关");
            this.stopService(intentService);
        }
        for (int i = 0; i < editTexts.length; i++)
            editTexts[i].setEnabled(!isChecked);
        editor.putBoolean("Switch",isChecked);
        editor.apply();
    }

}
