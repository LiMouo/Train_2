package com.ziker.train.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.R;
import com.ziker.train.Utils.Info.RedGreenInfo;
import com.ziker.train.Utils.ToolClass.Tools;

import java.util.ArrayList;
import java.util.List;

public class RedGreenControllerAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<RedGreenInfo> redGreenInfo;
    private List<Integer> isChecks = new ArrayList<>();
    private OnCheckedChange onCheckedChange;
    private OnButtonClick onButtonClick;

    public interface OnButtonClick{
        void OnClick(int position);
    }
    public interface OnCheckedChange{
        void getCheckeds(List<Integer> isChecks);
    }
    public RedGreenControllerAdapter(Context context, List<RedGreenInfo> redGreenInfo, OnButtonClick onButtonClick, OnCheckedChange onCheckedChange){
        this.context = context;
        this.redGreenInfo = redGreenInfo;
        this.onCheckedChange = onCheckedChange;
        this.onButtonClick = onButtonClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == 0){
            return new ViewHolderTitle(LayoutInflater.from(context).inflate(R.layout.redgreencontroller_item_title,parent,false));
        }else {
            return new ViewHolderContainer(LayoutInflater.from(context).inflate(R.layout.redgreencontroller_item_container,parent,false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(position == 0){
            ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) ((ViewHolderTitle)holder).linear_root.getLayoutParams();
            lp.setMargins(0, 0, 0, 0);
            ((ViewHolderTitle)holder).linear_root.setLayoutParams(lp);
        }else {
            ((ViewHolderContainer)holder).T_id.setText(redGreenInfo.get(position-1).getId()+"");
            ((ViewHolderContainer)holder).T_red.setText(redGreenInfo.get(position-1).getRedTime()+"");
            ((ViewHolderContainer)holder).T_yellow.setText(redGreenInfo.get(position-1).getYellowTime()+"");
            ((ViewHolderContainer)holder).T_green.setText(redGreenInfo.get(position-1).getGreenTime()+"");
            ((ViewHolderContainer)holder).c_checked.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked){
                    isChecks.add(position-1);
                }else {
                    isChecks.remove(isChecks.indexOf(position-1));
                }
                onCheckedChange.getCheckeds(isChecks);
            });
            ((ViewHolderContainer)holder).btn_set.setOnClickListener((v)-> onButtonClick.OnClick(position-1));
            Tools.SetButtonColor(((ViewHolderContainer)holder).btn_set);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return redGreenInfo.size()+1;
    }
    class ViewHolderTitle extends RecyclerView.ViewHolder{
        private LinearLayout linear_root;
        public ViewHolderTitle(@NonNull View itemView) {
            super(itemView);
            linear_root = itemView.findViewById(R.id.linear_root);
        }
    }

    class ViewHolderContainer extends RecyclerView.ViewHolder{
        private TextView T_id,T_red,T_yellow,T_green;
        private CheckBox c_checked;
        private Button btn_set;
        public ViewHolderContainer(@NonNull View itemView) {
            super(itemView);
            T_id = itemView.findViewById(R.id.T_id);
            T_red = itemView.findViewById(R.id.T_red);
            T_yellow = itemView.findViewById(R.id.T_yellow);
            T_green = itemView.findViewById(R.id.T_green);
            c_checked = itemView.findViewById(R.id.c_checked);
            btn_set = itemView.findViewById(R.id.btn_set);
        }
    }
}
