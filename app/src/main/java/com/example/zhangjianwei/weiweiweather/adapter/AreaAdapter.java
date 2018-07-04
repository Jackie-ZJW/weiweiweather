package com.example.zhangjianwei.weiweiweather.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.zhangjianwei.weiweiweather.R;
import com.example.zhangjianwei.weiweiweather.db.Area;

import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder> {

    private List<Area> mAreaList;

    public AreaAdapter(List<Area> mAreaList) {
        this.mAreaList = mAreaList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Area area = mAreaList.get(position);
        holder.areaName.setText(area.getAreaName());
    }

    @Override
    public int getItemCount() {
        return mAreaList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView areaName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            areaName = itemView.findViewById(R.id.tv_area_name);
        }
    }
}
