package com.ziker.train.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.Activity.SubwayPlanningActivity;
import com.ziker.train.R;

import java.util.List;
import java.util.Map;

public class SubwayQueryAdapter extends RecyclerView.Adapter<SubwayQueryAdapter.ViewHolder> {
    private Context context;
    private List<Map> list;
    public SubwayQueryAdapter(Context context, List<Map> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.subwayquery_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.t_title.setText(list.get(position).get("name").toString()+"线路图");
        holder.l_root.setOnClickListener(v->{
            Intent intent = new Intent(context, SubwayPlanningActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("map",list.get(position).get("map").toString());
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        TextView t_title;
        LinearLayout l_root;
        public ViewHolder(View v) {
            super(v);
            t_title = v.findViewById(R.id.t_title);
            l_root = v.findViewById(R.id.l_root);
        }
    }
}
