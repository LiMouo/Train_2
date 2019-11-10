package com.ziker.train.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.ziker.train.R;

import java.util.List;
import java.util.Map;

public class BusQueryAdapter extends BaseExpandableListAdapter {
    private Context context;
    private String[] GroupNames;
    private List<Map>[] ChildData;

    public BusQueryAdapter(Context context, String[] GroupNames, List<Map>[] ChildData) {
        this.context = context;
        this.GroupNames = GroupNames;
        this.ChildData = ChildData;
    }

    @Override
    public int getGroupCount() {//分组数量
        return GroupNames.length;
    }

    @Override
    public int getChildrenCount(int groupPosition) {//分组类子项数量
        return ChildData[groupPosition].size();
    }

    @Override
    public Object getGroup(int groupPosition) {//分组项
        return GroupNames[groupPosition];
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {//子项
        return ChildData[groupPosition].get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {//分组ID
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {//子项ID
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {//分组和子选项是否持有稳定的ID, 就是说底层数据的改变会不会影响到它们
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {//分组视图
        if(convertView ==null)
            convertView = LayoutInflater.from(context).inflate(R.layout.busquery_one_item, parent, false);
        TextView T_title = convertView.findViewById(R.id.label_group);
        T_title.setText(GroupNames[groupPosition]);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {//子项视图
        if(convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.busquery_two_item, parent, false);
        TextView T_person = convertView.findViewById(R.id.T_person);
        TextView T_time = convertView.findViewById(R.id.T_time);
        TextView T_number = convertView.findViewById(R.id.T_number);
        T_person.setText(ChildData[groupPosition].get(childPosition).get("carId")+"号("+ChildData[groupPosition].get(childPosition).get("person")+"人)");
        T_number.setText("距离站台"+ChildData[groupPosition].get(childPosition).get("distance")+"米");
        T_time.setText(ChildData[groupPosition].get(childPosition).get("time")+"分钟到达");
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {//子项是否可点击
        return true;
    }

}
