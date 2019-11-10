package com.ziker.train.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.R;
import com.ziker.train.Utils.Info.ManagerInfo;

import java.util.List;

public class ManagerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<ManagerInfo> list;

    public ManagerAdapter(Context context, List<ManagerInfo> list){
        this.context = context;
        this.list = list;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new LinearViewHolder(LayoutInflater.from(context).inflate(R.layout.manager_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position==0){
            ((LinearViewHolder)holder).one.setText("序号");
            ((LinearViewHolder)holder).two.setText("车号");
            ((LinearViewHolder)holder).three.setText("充值金额（元）");
            ((LinearViewHolder)holder).four.setText("操作人");
            ((LinearViewHolder)holder).five.setText("充值时间");
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) ((ManagerAdapter.LinearViewHolder)holder).root.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            ((ManagerAdapter.LinearViewHolder)holder).root.setLayoutParams(lp);
        }else{
            ManagerInfo managerinfo = list.get(position-1);
            ((LinearViewHolder)holder).one.setText(managerinfo.getId()+"");
            ((LinearViewHolder)holder).two.setText(managerinfo.getCarId()+"");
            ((LinearViewHolder)holder).three.setText(managerinfo.getMoney()+"");
            ((LinearViewHolder)holder).four.setText(managerinfo.getUser()+"");
            ((LinearViewHolder)holder).five.setText(managerinfo.getTime()+"");
        }
    }

    @Override
    public int getItemCount() {
        return list.size()+1;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{
        private TextView one,two,three,four,five;
        private LinearLayout root;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            one = itemView.findViewById(R.id.one);
            two = itemView.findViewById(R.id.two);
            three = itemView.findViewById(R.id.three);
            four = itemView.findViewById(R.id.four);
            five = itemView.findViewById(R.id.five);
            root = itemView.findViewById(R.id.linear_root);
        }
    }


}
