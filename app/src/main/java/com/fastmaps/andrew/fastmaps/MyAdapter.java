package com.fastmaps.andrew.fastmaps;

import android.content.Context;
import android.media.Image;
import android.support.v7.widget.RecyclerView;
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
    private LayoutInflater inflater;
    List<MapData> mapDataList = Collections.emptyList();
    MyAdapter (Context context, List<MapData> mapDataList) {
        inflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.custom_row,parent,false);
        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        MapData mapData = mapDataList.get(position);
        holder.primaryText.setText(mapData.name);
        holder.secondaryText.setText(mapData.address);
        // set up navigation button
        // holder.imageButton.;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView primaryText;
        TextView secondaryText;
        ImageButton imageButton;

        public MyViewHolder(View itemView) {
            super(itemView);
            primaryText = (TextView) primaryText.findViewById(R.id.primary_text);
            secondaryText = (TextView) secondaryText.findViewById(R.id.secondary_text);
            imageButton = (ImageButton) imageButton.findViewById(R.id.image_button);
        }
}


}