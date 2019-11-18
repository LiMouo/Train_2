package com.ziker.train.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ziker.train.R;
import com.ziker.train.Utils.Info.CustomShuttleOrderListInfo;

import java.util.List;

public class CustomShuttleOrderListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<CustomShuttleOrderListInfo> ListData;

    public CustomShuttleOrderListAdapter(Context context, List<CustomShuttleOrderListInfo> ListData) {
        this.context = context;
        this.ListData = ListData;
    }

    @Override
    public int getGroupCount() {
        return ListData.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ListData.get(groupPosition).getBusDate().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return ListData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ListData.get(groupPosition).getBusDate().get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.customshuttleorderlist_item, parent, false);
        CustomShuttleOrderListInfo info = ListData.get(groupPosition);
        TextView t_route = convertView.findViewById(R.id.t_route);
        TextView t_money = convertView.findViewById(R.id.t_money);
        TextView t_state = convertView.findViewById(R.id.t_state);
        t_money.setText("票价:"+info.getTicket()+"元");
        t_route.setText(info.getStartSite()+"—"+info.getEndSite());
        t_state.setText("订单编号:"+info.getId());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.customshuttle_chlid_item, parent, false);
        List<String> info = ListData.get(groupPosition).getBusDate();
        TextView tPoint = convertView.findViewById(R.id.t_point);
        TextView tSite = convertView.findViewById(R.id.t_site);
        if(childPosition==0)
            tPoint.setText("乘车日期:");
        tSite.setText(info.get(childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }


}
