package com.fastmaps.andrew.fastmaps;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.Collections;
import java.util.List;

/**
 * Created by Andrew on 6/15/2015.
 */
public class MyAdapter extends RecyclerView.Adapter <MyAdapter.MyViewHolder>{
    List<MapData> mapDataList = Collections.emptyList();
    MyAdapter ( List<MapData> mapDataList) {

        this.mapDataList = mapDataList;
        Log.d("constructor", "Constructor called");
    }
    public void DeleteRow (int position){
        mapDataList.remove(position);
        notifyItemRemoved(position);
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

        holder.imageButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
            MainActivity.Navigate(mapData, v.getContext());
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
                                Toast.makeText(holder.itemView.getContext(), "Entry deleted", Toast.LENGTH_SHORT)
                                        .show();
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
        ImageButton imageButton;

        public MyViewHolder(View itemView) {
            super(itemView);

            primaryText = (TextView) itemView.findViewById(R.id.primary_text);
            secondaryText = (TextView) itemView.findViewById(R.id.secondary_text);
            imageButton = (ImageButton) itemView.findViewById(R.id.image_button);

            }
        }


    }


