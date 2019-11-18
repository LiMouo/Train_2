package com.ziker.train.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ziker.train.R;
import com.ziker.train.Utils.Info.AccountInfo;
import com.ziker.train.Utils.ToolClass.MyAppCompatActivity;
import com.ziker.train.Utils.ToolClass.Tools;

import java.util.ArrayList;
import java.util.List;

public class AccountMangerAdapter extends RecyclerView.Adapter<AccountMangerAdapter.ViewHolder> {
    private Context context;
    private List<Integer> CheckedList = new ArrayList<>();
    private  List<AccountInfo> AccountInfo;
    private OnCheckeds OnCheckeds;
    private OnClick onClick;

    public interface OnCheckeds {
        void getCheckId(List<Integer> list);
    }
    public interface OnClick {
        void setClick(int position);
    }

    public AccountMangerAdapter(Context context, List<AccountInfo> accountInfo, OnClick onClick, OnCheckeds iOnCheckeds){
        this.context = context;
        this.onClick = onClick;
        this.OnCheckeds = iOnCheckeds;
        this.AccountInfo = accountInfo;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.account_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AccountInfo accountInfo = AccountInfo.get(position);
        if(MyAppCompatActivity.Trip_money  > accountInfo.getMoney() && MyAppCompatActivity.Trip_money >0)//拉数据改背景色
            holder.root.setBackgroundColor(Color.parseColor("#ffcc00"));
        else
            holder.root.setBackgroundColor(Color.parseColor("#ffffff"));
        holder.t_id.setText((position+1)+"");
        holder.t_car_id.setText(accountInfo.getCar_id());
        holder.t_car_user.setText(accountInfo.getCar_user());
        holder.t_money.setText(accountInfo.getMoney()+"");
        Object icon = accountInfo.getIcon();
        if(icon instanceof String)//适配泛型E类型加载图片
            Glide.with(context).load((String)icon).into(holder.i_icon);
        else if(icon instanceof Integer)
            holder.i_icon.setImageResource((Integer) icon);
        else if(icon instanceof Bitmap)
            holder.i_icon.setImageBitmap((Bitmap) icon);
        else if(icon instanceof Drawable)
            holder.i_icon.setImageDrawable((Drawable) icon);
        holder.c_checked.setChecked(false);
        holder.c_checked.setOnCheckedChangeListener((view,isCheck)->{
            if(isCheck){
                CheckedList.add(position);
                OnCheckeds.getCheckId(CheckedList);
            }else {
                CheckedList.remove(CheckedList.indexOf(position));
                OnCheckeds.getCheckId(CheckedList);
            }
        });
        if(onClick !=null)
            holder.btn_recharge.setOnClickListener(v -> onClick.setClick(position));
        Tools.SetButtonColor(holder.btn_recharge);
    }

    @Override
    public int getItemCount() {
        return AccountInfo.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        private TextView t_id,t_car_user,t_car_id,t_money;
        private ImageView i_icon;
        private CheckBox c_checked;
        private Button btn_recharge;
        private LinearLayout root;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            t_id = itemView.findViewById(R.id.t_id);
            t_car_user = itemView.findViewById(R.id.t_car_user);
            t_car_id = itemView.findViewById(R.id.t_car_id);
            t_money = itemView.findViewById(R.id.t_money);
            i_icon = itemView.findViewById(R.id.i_icon);
            c_checked = itemView.findViewById(R.id.c_checked);
            btn_recharge = itemView.findViewById(R.id.btn_recharge);
            root = itemView.findViewById(R.id.root);
        }
    }
}
