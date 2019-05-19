package com.example.zhangjianwei.weiweiweather.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.zhangjianwei.weiweiweather.R;
import com.example.zhangjianwei.weiweiweather.db.Area;

import java.util.List;

public class AreaAdapter extends RecyclerView.Adapter<AreaAdapter.ViewHolder> {

    private List<Area> mAreaList;

    private OnItemClickListener onItemClickListener;

    private int mPosition = -1;

    public AreaAdapter(List<Area> mAreaList) {
        this.mAreaList = mAreaList;
    }

    public void setSelection(int position) {
        this.mPosition = position;
    }


    class ViewHolder extends RecyclerView.ViewHolder {
        public LinearLayout areaLinearLayout;
        public TextView areaName;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            areaLinearLayout = itemView.findViewById(R.id.ll_area_item_layout);
            areaName = itemView.findViewById(R.id.tv_area_name);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.area_item, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        Area area = mAreaList.get(position);
        holder.areaName.setText(area.getAreaName());

        /*if (mPosition != -1) {
            if (position!=mPosition) {
                //position=mPosition;
                holder.areaLinearLayout.setBackgroundColor(Color.GRAY);
            }
        }*/

        if (onItemClickListener != null) {
            holder.areaLinearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(view, position);
                }
            });

            holder.areaName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onItemClickListener.onClick(view, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return mAreaList.size();
    }

    public interface OnItemClickListener {

        void onClick(View view, int position);

        void onLongClick(View view, int position);

    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
