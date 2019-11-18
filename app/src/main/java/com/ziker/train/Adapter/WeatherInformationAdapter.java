package com.ziker.train.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class WeatherInformationAdapter extends RecyclerView.Adapter<WeatherInformationAdapter.ViewHolder> {
    private Context context;
    private List<Map> list;
    private String[] days = {"今天", "明天", "后天"};
    private String[] state = {"晴", "阴", "小雨"};
    private int[] weather = {R.drawable.weatherinformation_tianqi1, R.drawable.weatherinformation_tianqi2,
            R.drawable.weatherinformation_tianqi3};

    public WeatherInformationAdapter(Context context, List<Map> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.weatherinformation_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        try {
            Map map = list.get(position);
            holder.t_temperature.setText(map.get("temperature").toString().replace("~", "/").replace(" ", "") + "℃");
            holder.t_info.setText(map.get("type").toString());
            //根据天气情况显示不同背景
            for (int i = 0; i < state.length; i++)
                if (state[i].equals(map.get("type")))
                    holder.l_root.setBackground(context.getDrawable(weather[i]));
            //
            if (position < 3) {
                holder.t_date.setText(map.get("WData").toString().substring(8, 10) + "(" + days[(position)] + ")");
            } else {
                long oldDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(map.get("WData").toString() + " 00:00:01").getTime();//字符串转时间
                String week = new SimpleDateFormat("EEEE").format(new Date(oldDate));//时间转字符串
                holder.t_date.setText(map.get("WData").toString().substring(8, 10) + "日(周" + week.substring(2, 3) + ")");

            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView t_temperature, t_info, t_date;
        private ImageView i_icon;
        private LinearLayout l_root;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            t_temperature = itemView.findViewById(R.id.t_temperature);
            t_info = itemView.findViewById(R.id.t_info);
            t_date = itemView.findViewById(R.id.t_date);
            i_icon = itemView.findViewById(R.id.i_icon);
            l_root = itemView.findViewById(R.id.l_root);
        }
    }
}
