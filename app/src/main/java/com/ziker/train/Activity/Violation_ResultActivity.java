package com.ziker.train.Activity;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.Adapter.Violation_ResultAdapter;
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
import java.util.Map;

public class Violation_ResultActivity extends MyAppCompatActivity {
    private static String CodeUrl = "GetPeccancyType.do";
    private List<Map> LeftInfo = new ArrayList<>();
    private List<Map> RightInfo = new ArrayList<>();
    private List<Map> AllCodeInfo = new ArrayList<>();
    private List<List<Map>> AllRightInfo = new ArrayList<>();
    private RecyclerView RV_left,RV_right;
    private Violation_ResultAdapter adapter_right,adapter_left;
    private Handler handler = new Handler();
    private SQLiteDatabase db;
    private String FirstNumber;
    private int ClickItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_violation_result);
        super.SetMenu(this,"查询结果",null);
        super.SetAnimation(FLAG_NO_ANIMATION);
        InitView();
        BindData();
        QueryData();
    }

    private void InitView(){
        RV_left = findViewById(R.id.RV_left);
        RV_right = findViewById(R.id.RV_right);
        FirstNumber = getIntent().getExtras().getString("carnumber");
        db = new MySqLiteOpenHelper(this).getWritableDatabase();
    }
    private void BindData(){
        LinearLayoutManager left_Manger = new LinearLayoutManager(this);
        RV_left.setLayoutManager(left_Manger);
        adapter_left = new Violation_ResultAdapter(this,false,LeftInfo);

        RV_right.setLayoutManager(new LinearLayoutManager(this));
        adapter_right = new Violation_ResultAdapter(this,true,RightInfo);
        /*左边每一项*/
        adapter_left.setOnItemClick((v,position) -> {
            RV_left.getViewTreeObserver().addOnDrawListener(() -> {//为RV添加onDraw监听事件
                for (int i = 1; i < left_Manger.getItemCount(); i++) {//遍历所有item项
                    View view = left_Manger.findViewByPosition(i);//findViewByPosition 找到所有item的position项
                    if( view != null )//RV独特有的回收机制，在item不可见时，ViewHolder会被回收，会拿到一个null
                        view.findViewById(R.id.L_root).setBackground(getDrawable(R.drawable.violation_result_item_board));//为所有item设置边框加白背景色
                    v.setBackground(getDrawable(R.drawable.violation_result_item_background));//为点击的item设置边框加灰背景色
                }
            });
            ClickItem = position;
            RightInfo.clear();
            RightInfo.addAll(AllRightInfo.get(position));
            adapter_right.notifyDataSetChanged();
//            RightInfo = AllRightInfo.get(position);
            /*不能用此类方法赋值、hashCode不会改变。   RightInfo = AllRightInfo.get(position);
            hashCode返回的并不一定是对象的（虚拟）内存地址、具体取决于运行时库和JVM的具体实现。但是在程序的一次执行过程中、对同一个对象必须一致地返回同一个整数。
            对象一样、notifyDataSetChanged 就不会刷新*/
        });
        /*减号图标*/
        adapter_left.setOnReduceClick(position -> {
            if(ClickItem == position){//点击的减号项与选中的项一致
                String number = LeftInfo.get(position).get("carnumber").toString();
                db.delete("Violation","carnumber='"+number+"'",null);
                LeftInfo.remove(position);
                AllRightInfo.remove(position);
                adapter_left.RemoveItem(position);
                if(LeftInfo.size() <= 0){
                    Toast.makeText(this,"已无历史记录,请重新查询!",Toast.LENGTH_LONG).show();
                    finish();
                }
            }else{
                Toast.makeText(this,"先选中数据好吗",Toast.LENGTH_LONG).show();
            }
        });
        /*加号图标*/
        adapter_left.setOnPlusClick(() -> {
            Intent intent = new Intent(this,ViolationActivity.class).putExtras(new Bundle());
            startActivityForResult(intent,1);
            overridePendingTransition(R.anim.gradual_in,R.anim.gradual_out);
        });
        /*右边列表项点击*/
        adapter_right.setOnItemClick((v,position)-> startActivity(new Intent(this,Violation_CaptureActivity.class)));

        RV_left.setAdapter(adapter_left);
        RV_right.setAdapter(adapter_right);
        RV_left.setOverScrollMode(View.OVER_SCROLL_NEVER);//取消 上滑到顶、下拉到底 的效果
        RV_right.setOverScrollMode(View.OVER_SCROLL_NEVER);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK){
                    FirstNumber = data.getExtras().getString("carnumber");
                    QueryData();
                }else {
                    handler.postDelayed(()->Tools.Toast(this,"取消查询",false),400);
                }
                break;
        }
    }

    private void QueryData(){
        new Thread(()->{
                try {
                    AllCodeInfo.clear();
                    AllRightInfo.clear();
                    LeftInfo.clear();
                    /*找到所有违章代码存到 AllCodeInfo 集合里*/
                    String Data = Tools.SendRequest(CodeUrl,new HashMap());
                    JSONObject jsonObject = new JSONObject(Data);
                    JSONArray jsonArray = jsonObject.getJSONArray("ROWS_DETAIL");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject info = jsonArray.getJSONObject(i);
                        Map code = new HashMap();
                        code.put("pcode",info.get("pcode"));
                        code.put("premarks",info.get("premarks"));
                        code.put("pmoney",info.get("pmoney"));
                        code.put("pscore",info.get("pscore"));
                        AllCodeInfo.add(code);
                    }
                    /*按车牌号分组查到所有车牌名*/
                    Cursor cursor = db.query("Violation",null,null,null,"carnumber",null,null);
                    if(cursor.moveToFirst()){
                        /*将车牌存到number里待用*/
                        List<String> number = new ArrayList<>();
                        do{
                            number.add(cursor.getString(1));
                        }while (cursor.moveToNext());
                        /*设置第一个显示为查询的车牌、用了 remove 下面查询的时候就不会重复*/
                        setChildInfo(number.remove(number.indexOf(FirstNumber)));
                        /*后面接着显示为查询过的车牌*/
                        for (int j = 0; j < number.size(); j++) {
                            setChildInfo(number.get(j));
                        }
                    }
                    handler.post(()->{
                        adapter_left.notifyDataSetChanged();
                        adapter_right.notifyDataSetChanged();
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//            }
        }).start();
    }

    private void setChildInfo(String number){
        Cursor single = db.query("Violation",null,"carnumber='"+number+"'", null,null,null,null);
        if(single.moveToFirst()){
            List<Map> list = new ArrayList<>();
            String carnumber = number;
            int count= single.getCount() ;
            int reduce = 0;
            int money = 0;
            do {
                Map info = new HashMap();
                info.put("car_number",single.getString(1));
                info.put("road",single.getString(3));
                info.put("time",single.getString(4));
                for (int i = 0; i < AllCodeInfo.size(); i++) {
                    if(AllCodeInfo.get(i).get("pcode").equals(single.getString(2))){
                        info.put("info",AllCodeInfo.get(i).get("premarks"));
                        info.put("money",AllCodeInfo.get(i).get("pmoney"));
                        info.put("reduce",AllCodeInfo.get(i).get("pscore"));
                        money += Integer.parseInt(AllCodeInfo.get(i).get("pmoney").toString());
                        reduce += Integer.parseInt(AllCodeInfo.get(i).get("pscore").toString());
                        break;
                    }
                }
                list.add(info);
            }while (single.moveToNext());
            Map map = new HashMap();
            map.put("carnumber",carnumber);
            map.put("count",count);
            map.put("reduce",reduce);
            map.put("money",money);
            LeftInfo.add(map);
            AllRightInfo.add(list);
        }
    }
}
