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

import java.util.List;
import java.util.Map;

public class Violation_ResultAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<Map> CardInfo;
    private Listener listener = new Listener();//回调接口类
    private int reFreshId = 0;//处于选中状态的id
    private boolean isRight;

    public Violation_ResultAdapter(Context context,boolean isRight,List<Map> CardInfo){
        this.context = context;
        this.isRight = isRight;
        this.CardInfo = CardInfo;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(!isRight){//左边视图
            if (viewType == 0){
                return new ViewHolder_Title(LayoutInflater.from(context).inflate(R.layout.violation_result_title,parent,false));
            }else{
                return new LeftViewHolder_Container(LayoutInflater.from(context).inflate(R.layout.violation_result_left_container,parent,false));
            }
        }else {//右边视图
            if (viewType == 0){
                return new ViewHolder_Title(LayoutInflater.from(context).inflate(R.layout.violation_result_title,parent,false));
            }else{
                return new RightViewHolder_Container(LayoutInflater.from(context).inflate(R.layout.violation_result_right_container,parent,false));
            }
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(!isRight){//左边内容
            if (position == 0){
                ViewHolder_Title view =((ViewHolder_Title)holder);
                view.T_title.setText("汽车资料卡片");
                view.I_icon.setImageResource(R.drawable.plus);
                if(listener.onPlusClick != null)//设置加号点击监听
                    view.I_icon.setOnClickListener(v->listener.onPlusClick.OnCLick());
            }else{
                LeftViewHolder_Container view = ((LeftViewHolder_Container)holder);
                if(listener.onItemClick != null)//设置单项点击监听
                    view.L_root.setOnClickListener(v-> listener.onItemClick.OnClick(v,position-1));
                if(listener.onReduceClick != null)//设置减号点击监听
                    view.I_icon.setOnClickListener(v->listener.onReduceClick.OnCLick(position-1));
                if(reFreshId == position-1 ){//需要处于点击状态，触发点击事件,
                    View reFreshView = view.L_root;
                    reFreshView.callOnClick();
                }
                Map info = CardInfo.get(position-1);
                view.T_car_id.setText(info.get("carnumber").toString());
                view.T_number.setText("未处理违章"+info.get("count").toString()+"次");
                view.T_reduce.setText("扣"+info.get("reduce").toString()+"分");
                view.T_money.setText("罚款"+info.get("money").toString()+"元");
            }
        }else {//右边内容
            if (position == 0){
                ((ViewHolder_Title)holder).T_title.setText("违章详情");
            }else{
                RightViewHolder_Container view = ((RightViewHolder_Container)holder);
                if(listener.onItemClick != null)
                    view.L_root.setOnClickListener(v->listener.onItemClick.OnClick(v,position-1));
                Map info = CardInfo.get(position-1);
                view.T_time.setText(info.get("time").toString());
                view.T_road.setText(info.get("road").toString());
                view.T_info.setText(info.get("info").toString());
                view.T_reduce.setText("扣"+info.get("reduce").toString()+"分");
                view.T_money.setText("罚款"+info.get("money").toString()+"元");
            }
        }
    }

    public void RemoveItem(int position){
        this.reFreshId = position;
        if(position == CardInfo.size())//删除的是最后一项，将处于选中状态的id改变为上一项
            reFreshId = CardInfo.size() -1;
        notifyDataSetChanged();//更新视图
    }

    @Override
    public int getItemCount() {//item总数
        return CardInfo.size()+1;
    }

    @Override
    public int getItemViewType(int position) {//item类型，onCreateViewHolder的viewType是此处返回的数值
        return position;
    }

    class ViewHolder_Title extends RecyclerView.ViewHolder{
        private TextView T_title;
        private ImageView I_icon;
        public ViewHolder_Title(@NonNull View itemView) {
            super(itemView);
            T_title = itemView.findViewById(R.id.t_title);
            I_icon =  itemView.findViewById(R.id.i_icon);
        }
    }
    class LeftViewHolder_Container extends RecyclerView.ViewHolder{
        private TextView T_car_id,T_number,T_reduce,T_money;
        private ImageView I_icon;
        private LinearLayout L_root;
        public LeftViewHolder_Container(@NonNull View itemView) {
            super(itemView);
            L_root = itemView.findViewById(R.id.L_root);
            T_car_id = itemView.findViewById(R.id.t_car_id);
            T_number = itemView.findViewById(R.id.t_number);
            T_reduce = itemView.findViewById(R.id.t_reduce);
            T_money = itemView.findViewById(R.id.t_money);
            I_icon = itemView.findViewById(R.id.i_icon);
        }
    }
    class RightViewHolder_Container extends RecyclerView.ViewHolder{
        private TextView T_time,T_road,T_reduce,T_money,T_info;
        private LinearLayout L_root;
        public RightViewHolder_Container(@NonNull View itemView) {
            super(itemView);
            T_time = itemView.findViewById(R.id.t_time);
            T_road = itemView.findViewById(R.id.t_road);
            T_reduce = itemView.findViewById(R.id.t_reduce);
            T_money = itemView.findViewById(R.id.t_money);
            T_info = itemView.findViewById(R.id.t_info);
            L_root =  itemView.findViewById(R.id.L_root);
        }
    }

    private static class Listener{
        private OnItemClick onItemClick;
        private OnReduceClick onReduceClick;
        private OnPlusClick onPlusClick;
    }
    public void setOnItemClick(OnItemClick onItemClick){
        this.listener.onItemClick = onItemClick;
    }
    public void setOnReduceClick(OnReduceClick onReduceClick){
        this.listener.onReduceClick = onReduceClick;
    }
    public void setOnPlusClick(OnPlusClick onPlusClick){
        listener.onPlusClick = onPlusClick;
    }
    public interface OnPlusClick{
        void OnCLick();
    }
    public interface OnReduceClick{
        void OnCLick(int position);
    }
    public interface OnItemClick{
        void OnClick(View v,int position);
    }
}
