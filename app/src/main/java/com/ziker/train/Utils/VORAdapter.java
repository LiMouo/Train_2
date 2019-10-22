package com.ziker.train.Utils;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ziker.train.R;
import com.ziker.train.VideoPlayerActivity;

import java.util.List;

public class VORAdapter extends RecyclerView.Adapter {
    private Context context;
    private List<VORinfo> voRinfos;

    public VORAdapter(Context context, List<VORinfo> voRinfos){
        this.context = context;
        this.voRinfos = voRinfos;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new GridViewHolder(LayoutInflater.from(context).inflate(R.layout.vor_item,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(voRinfos.get(position).getImage() ==null){
            ((GridViewHolder)holder).title.setText(voRinfos.get(position).getTitle());
            ((GridViewHolder)holder).root.setOnClickListener(v -> {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("video",voRinfos.get(position).getVideo());
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            });
        }else {
            ((GridViewHolder)holder).image.setImageResource(voRinfos.get(position).getImage());
            ((GridViewHolder)holder).root.setOnClickListener(v -> {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("image",voRinfos.get(position).getImage());
                intent.putExtras(bundle);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return voRinfos.size();
    }

    class GridViewHolder extends RecyclerView.ViewHolder{
        private TextView title;
        private ImageView image;
        private LinearLayout root;
        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root);
            title = itemView.findViewById(R.id.title);
            image = itemView.findViewById(R.id.image);
        }
    }
}
