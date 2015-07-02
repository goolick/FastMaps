package com.fastmaps.andrew.fastmaps.adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.fastmaps.andrew.fastmaps.MapData;
import com.fastmaps.andrew.fastmaps.R;
import com.fastmaps.andrew.fastmaps.activities.MainActivity;

import java.util.Collections;
import java.util.List;

public class MyAdapter extends RecyclerView.Adapter <MyAdapter.MyViewHolder>{
    List<MapData> mapDataList = Collections.emptyList();
    public MyAdapter(List<MapData> mapDataList) {

        this.mapDataList = mapDataList;
        Log.d("constructor", "Constructor called");
    }
    public void DeleteRow (int position) {
        mapDataList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, MainActivity.MAX_ROW);;
        Log.d("DeleteRow", "ran DeleteRow on row " + position);
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_row,parent,false);
        MyViewHolder holder = new MyViewHolder(view);
        Log.d("onCreateViewHolder", "created MyViewHolder");
        return holder;
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Log.d("onBindViewHolder", "ran onBindViewHolder");
        final MapData mapData = mapDataList.get(position);
        holder.primaryText.setText(mapData.getName());
        holder.secondaryText.setText(mapData.getPlace());

        holder.navButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            MainActivity.Navigate(mapData, v.getContext());
            }
        });
        holder.dirButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            MainActivity.Direct(mapData, v.getContext());
            }
        });

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                // On long click, create dialog prompting to delete the row
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.itemView.getContext());
                builder.setMessage("Delete this shortcut?")
                        .setCancelable(true)
                        .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Delete row
                                DeleteRow(position);
                                dialog.cancel();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return false;
            }
        });
    }

    @Override
    public int getItemCount() {
        return mapDataList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView primaryText;
        TextView secondaryText;
        ImageButton navButton;
        ImageButton dirButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            primaryText = (TextView) itemView.findViewById(R.id.primary_text);
            secondaryText = (TextView) itemView.findViewById(R.id.secondary_text);
            navButton = (ImageButton) itemView.findViewById(R.id.image_button_nav);
            dirButton = (ImageButton) itemView.findViewById(R.id.image_button_dir);
            }
        }


    }


