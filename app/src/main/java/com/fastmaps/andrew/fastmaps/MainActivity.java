package com.fastmaps.andrew.fastmaps;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;


public class MainActivity extends ActionBarActivity {

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private FloatingActionButton floatingActionButton;
    private static List<MapData> mapDataList = new ArrayList<>(25);
    public static Boolean Updated = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /* Reference to FAB and onClickListener - show the dialog box upon click */
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShowDialog();
            }
        });
        InitMapData();

        /* Create LinearLayoutManager and Adapter*/
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MyAdapter(mapDataList);

        /* Create RecyclerView and connect to LayoutManager and Adapter */
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Updated) {
            mRecyclerView.getAdapter().notifyDataSetChanged();
            Log.d("updated", "updated dataset");
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static void AddData(String name, String address){
/* Take in a name and place and add them to the list of MapData */

        MapData newMapData = new MapData();
        newMapData.address = address;
        newMapData.name = name;
        Log.e("Name and address","Name = " + newMapData.name + " address = " + newMapData.address);
        mapDataList.add(newMapData);
        Log.e("AddData","Data added. Count = " + mapDataList.size());
        Updated = false;

    }


    public static List<MapData> InitMapData(){
        MapData mapData = new MapData();
        mapData.address = "init address";
        mapData.name = "init name";
        mapDataList.add(0,mapData);

        return mapDataList;
    }

    public void ShowDialog(){
        FragmentManager fragmentManager = getFragmentManager();
        SearchDialog searchDialog = new SearchDialog();

        searchDialog.show(fragmentManager,"");
    }

    public void Navigate(MapData mapData){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        // Uncomment line below to use chooser
        // Intent chooser = null;

        //intent.setData((Uri.parse(mapData.address)));
        intent.setData((Uri.parse("geo:19,70")));
        startActivity(intent);
        // Uncomment lines below to use chooser
        //chooser = intent.createChooser(intent, "Launch Maps");
        //startActivity(chooser);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
