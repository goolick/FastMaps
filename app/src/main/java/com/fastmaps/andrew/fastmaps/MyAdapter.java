package com.fastmaps.andrew.fastmaps;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

/**
 * Created by Andrew on 6/15/2015.
 */
public class MyAdapter extends RecyclerView.Adapter <MyAdapter.MyViewHolder> {
    List<MapData> mapDataList = Collections.emptyList();
    MyAdapter ( List<MapData> mapDataList) {
        this.mapDataList = mapDataList;
        Log.d("constructor", "Constructor called");
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        Log.d("onCreateViewHolder", "created MyViewHolder");
        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Log.d("onBindViewHolder", "ran onBindViewHolder");
        MapData mapData = mapDataList.get(position);
        holder.primaryText.setText(mapData.name);
        holder.secondaryText.setText(mapData.address);
        // set up navigation button
        // holder.imageButton.;
    }

    @Override
    public int getItemCount() {
        Log.d("getItemCount", "getItemCount was run. size = " + mapDataList.size());
        return mapDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView primaryText;
        TextView secondaryText;
        ImageButton imageButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            primaryText = (TextView) itemView.findViewById(R.id.primary_text);
            secondaryText = (TextView) itemView.findViewById(R.id.secondary_text);
            imageButton = (ImageButton) itemView.findViewById(R.id.image_button);
        }
}


}