package com.ziker.train.Utils;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.ziker.train.ETCActivity;
import com.ziker.train.ManagerActivity;
import com.ziker.train.R;
import com.ziker.train.RedGreenActivity;

import java.util.ArrayList;
import java.util.List;

public class MyAppCompatActivity extends AppCompatActivity {
    private List<Integer> idList = new ArrayList<>();
    private List<Class<?>> ActivityList = new ArrayList<>();
    private static int i = 0;
    private Menu menu;

    @Override
    protected void onRestart() {
        super.onRestart();
        i--;
        Log.d("MyActivityCompat", "onRestart: onRestart");
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        i++;
        idList.add(R.id.one);
        idList.add(R.id.two);
        idList.add(R.id.three);
        ActivityList.add(ETCActivity.class);
        ActivityList.add(RedGreenActivity.class);
        ActivityList.add(ManagerActivity.class);
    }
    public Menu setMenu(Context context, String Title, Integer Top_more_id){
        View Top_View = findViewById(android.R.id.content);
        ViewGroup parent = (ViewGroup) Top_View.getParent();
        parent.removeView(Top_View);
        menu = new Menu(context,Top_View,Title, R.drawable.amap,Top_more_id,R.layout.linear_menu);
        try {
            if(idList.size()!=0){
                if(i>1)
                    menu.setOnMenuItemStartActivity(idList, ActivityList, v -> finish());
                else
                    menu.setOnMenuItemStartActivity(idList,ActivityList,null);
            }else{
                Log.d("MyActivityCompat", "setMenu: "+ActivityList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        parent.addView(menu);
        try {
            return menu;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }
}
