package com.ziker.train.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ziker.train.Activity.CustomShuttleOrderActivity;
import com.ziker.train.R;
import com.ziker.train.Utils.Info.CustomShuttleInfo;

import java.util.List;


public class CustomShuttleAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<CustomShuttleInfo> ListData;

    public CustomShuttleAdapter(Context context, List<CustomShuttleInfo> mListData) {
        this.context = context;
        ListData = mListData;
    }

    @Override
    public int getGroupCount() {
        return ListData.size();

    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ListData.get(groupPosition).getSites().size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return ListData.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ListData.get(groupPosition).getSites();
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
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.customshuttle_parent_item, parent, false);
        CustomShuttleInfo info = ListData.get(groupPosition);
        List<CustomShuttleInfo.TimeBean> time = info.getTime();
        CustomShuttleInfo.TimeBean startinfo = time.get(0);
        CustomShuttleInfo.TimeBean endinfo = time.get(1);
        /*找到控件*/
        TextView tRoad = convertView.findViewById(R.id.t_road);
        TextView tMoney = convertView.findViewById(R.id.t_money);
        TextView tDistance = convertView.findViewById(R.id.t_distance);
        TextView tRoute = convertView.findViewById(R.id.t_route);
        TextView tTEndTime = convertView.findViewById(R.id.t_endTime);
        TextView tBeginTime = convertView.findViewById(R.id.t_beginTime);
        ImageView iImage = convertView.findViewById(R.id.i_image);
        /*绑定数据*/
        tRoad.setText(info.getName());
        tMoney.setText("票价:￥" + info.getMileage() + "元");
        tDistance.setText("里程:" + info.getTicket() + ".00km");
        tRoute.setText(startinfo.getSite() + "—" + endinfo.getSite());
        tBeginTime.setText(startinfo.getStarttime().substring(0, 5) + "—" + startinfo.getEndtime().substring(0, 5));
        tTEndTime.setText(endinfo.getStarttime().substring(0, 5) + "—" + endinfo.getEndtime().substring(0, 5));
        iImage.setSelected(isExpanded);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.customshuttle_chlid_item, parent, false);
        List<String> sites = ListData.get(groupPosition).getSites();
        LinearLayout lRoot = convertView.findViewById(R.id.l_root);
        TextView tPoint = convertView.findViewById(R.id.t_point);
        TextView tSite = convertView.findViewById(R.id.t_site);
        tPoint.setText("");
        if (childPosition == 0)
            tPoint.setText("起点:");
        else if (childPosition == (sites.size() - 1))
            tPoint.setText("终点:");
        tSite.setText(sites.get(childPosition));
        lRoot.setOnClickListener(v -> {
            Intent intent = new Intent(context, CustomShuttleOrderActivity.class);
            intent.putExtra("info", ListData.get(groupPosition));
            context.startActivity(intent);
        });
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
