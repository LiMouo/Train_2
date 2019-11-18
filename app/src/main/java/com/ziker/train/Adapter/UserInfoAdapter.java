package com.ziker.train.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.R;
import com.ziker.train.Utils.ToolClass.CircleImageView;
import com.ziker.train.Utils.ToolClass.Tools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;

public class UserInfoAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context context;
    private List<Map> List;
    private boolean isInfo;
    public UserInfoAdapter(Context context, List<Map> List,boolean isInfo){
        this.context = context;
        this.List = List;
        this.isInfo = isInfo;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(!isInfo)
            return new HistoryViewHolder(LayoutInflater.from(context).inflate(R.layout.userinfo_history_item,parent,false));
        else
            return new InfoViewHolder(LayoutInflater.from(context).inflate(R.layout.userinfo_info_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(!isInfo){
            try {
                Map map = List.get(position);
                String time = map.get("date").toString();
                String[] t_date = time.substring(0,time.indexOf(" ")).split("-");
                String date = "";
                for (int i = 0; i < t_date.length; i++) {
                    date += t_date[i];
                    if(i != t_date.length-1)
                        date += ".";
                }
                String week = new SimpleDateFormat("EEEE").format(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(time));
                String name = map.get("user").toString();
                String carid = map.get("CARID").toString();
                int money = Integer.parseInt(map.get("MONEY").toString());
                int balance = Integer.parseInt(map.get("balance").toString());
                ((HistoryViewHolder)holder).t_date.setText(date);
                ((HistoryViewHolder)holder).t_week.setText(week);
                ((HistoryViewHolder)holder).t_name.setText(name);
                ((HistoryViewHolder)holder).t_carid.setText(carid);
                ((HistoryViewHolder)holder).t_money.setText("充值:"+money);
                ((HistoryViewHolder)holder).t_balance.setText("余额:"+balance);
                if(position == List.size()-1){
                    ((HistoryViewHolder)holder).t_separate.setBackgroundColor(Color.parseColor("#00ffffff"));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }else {
            Log.d("", "UserInfoAdapter: "+List.hashCode());
            Map map = List.get(position);
            Bitmap bitmap = Tools.BitmapFromByte((byte[]) map.get("icon"));
            ((InfoViewHolder)holder).i_icon.setImageBitmap(bitmap);
            ((InfoViewHolder)holder).t_car_id.setText(map.get("CARID").toString());
            ((InfoViewHolder)holder).t_money.setText("余额:"+map.get("money").toString());
        }
    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder{
        private TextView t_date,t_week,t_name,t_carid,t_money,t_balance,t_time,t_separate;
        public HistoryViewHolder(@NonNull View itemView) {
            super(itemView);
            t_date = itemView.findViewById(R.id.t_date);
            t_week = itemView.findViewById(R.id.t_week);
            t_name = itemView.findViewById(R.id.t_name);
            t_carid = itemView.findViewById(R.id.t_carid);
            t_money = itemView.findViewById(R.id.t_money);
            t_balance = itemView.findViewById(R.id.t_balance);
            t_time = itemView.findViewById(R.id.t_time);
            t_separate = itemView.findViewById(R.id.t_separate);
        }
    }

    class InfoViewHolder extends RecyclerView.ViewHolder{
        private TextView t_car_id,t_money;
        private CircleImageView i_icon;
        public InfoViewHolder(@NonNull View itemView) {
            super(itemView);
            t_car_id = itemView.findViewById(R.id.t_car_id);
            t_money = itemView.findViewById(R.id.t_money);
            i_icon = itemView.findViewById(R.id.i_icon);
        }
    }
}
