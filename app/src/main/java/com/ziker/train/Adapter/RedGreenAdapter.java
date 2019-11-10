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
import com.ziker.train.Utils.Info.RedGreenInfo;

import java.util.List;

public class RedGreenAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private Context context;
    private List<RedGreenInfo> redGreenInfo;
    public RedGreenAdapter(Context context, List<RedGreenInfo> redGreenInfo){
        this.context = context;
        this.redGreenInfo = redGreenInfo;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return  new LinearViewHolder(LayoutInflater.from(context).inflate(R.layout.redgreen_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position==0){
            ((LinearViewHolder)holder).one.setText("路口");
            ((LinearViewHolder)holder).two.setText("红灯时长（S）");
            ((LinearViewHolder)holder).three.setText("黄灯时长（S）");
            ((LinearViewHolder)holder).four.setText("绿灯时长（S)");
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) ((LinearViewHolder)holder).root.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            ((LinearViewHolder)holder).root.setLayoutParams(lp);
        }else{
            ((LinearViewHolder)holder).one.setText(redGreenInfo.get(position-1).getId()+"");
            ((LinearViewHolder)holder).two.setText(redGreenInfo.get(position-1).getRedTime()+"");
            ((LinearViewHolder)holder).three.setText(redGreenInfo.get(position-1).getGreenTime()+"");
            ((LinearViewHolder)holder).four.setText(redGreenInfo.get(position-1).getYellowTime()+"");
        }
    }

    @Override
    public int getItemCount() {
        return redGreenInfo.size()+1;
    }

    class LinearViewHolder extends RecyclerView.ViewHolder{
        private TextView one,two,three,four;
        private LinearLayout root;
        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            one = itemView.findViewById(R.id.one);
            two = itemView.findViewById(R.id.two);
            three = itemView.findViewById(R.id.three);
            four = itemView.findViewById(R.id.four);
            root = itemView.findViewById(R.id.linear_root);
        }
    }

}
