package com.ziker.train.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.R;
import com.ziker.train.Activity.RealTimeActivity;

public class EnvironmentalAdapter extends RecyclerView.Adapter {
    private Context context;
    private int[] list;
    private String[] names;

    public EnvironmentalAdapter(Context context,int[] list,String[] names){
        this.context = context;
        this.list = list;
        this.names = names;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GridViewHolder(LayoutInflater.from(context).inflate(R.layout.environmental_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((GridViewHolder)holder).t_num.setText(list[position]+"");
        ((GridViewHolder)holder).t_name.setText(names[position]+"");
        if(list[5] >= 4 && names[position] == "道路状态"){
            ((GridViewHolder)holder).root.setBackgroundColor(Color.RED);
        }else{
            ((GridViewHolder)holder).root.setBackgroundColor(Color.parseColor("#00ff00"));
        }
        ((GridViewHolder)holder).root.setOnClickListener(v ->{
            Intent intent = new Intent(context, RealTimeActivity.class);
            Bundle bundle = new Bundle();
            bundle.putInt("position",position);
            intent.putExtras(bundle);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    class GridViewHolder extends RecyclerView.ViewHolder{
        private TextView t_num,t_name;
        private LinearLayout root;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            t_num = itemView.findViewById(R.id.T_number);
            t_name = itemView.findViewById(R.id.T_name);
            root = itemView.findViewById(R.id.root);
        }
    }
}
