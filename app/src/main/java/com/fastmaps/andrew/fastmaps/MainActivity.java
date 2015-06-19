package com.fastmaps.andrew.fastmaps;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
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


public class MainActivity extends ActionBarActivity {

    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private RecyclerView mRecyclerView;
    private FloatingActionButton floatingActionButton;
   // private DefaultItemAnimator mDefaultItemAnimator;
    private static List<MapData> mapDataList = new ArrayList<>(25);
    public static Boolean Updated = false;
    public final long ADD_DURATION = 700;
    public final long DELETE_DURATION = 700;

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

        // Create LinearLayoutManager and Adapter
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MyAdapter(mapDataList);

        // Set up DefaultItemAnimator with fade in/out times
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(ADD_DURATION);
        defaultItemAnimator.setRemoveDuration(DELETE_DURATION);

        // Create RecyclerView and connect to LayoutManager, Adapter, and Item Animator
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(defaultItemAnimator);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Updated) {
            mRecyclerView.getAdapter().notifyItemInserted(mapDataList.size());
            Log.d("updated", "updated dataset");
        }
        Log.d("OnResume", "call to OnResume");
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public static void AddData(String name, String place){
/* Take in a name and place and add them to the list of MapData */

        MapData newMapData = new MapData();
        newMapData.setName(name);
        newMapData.setPlace(place);
        Log.e("Name and address","Name = " + newMapData.getName() + " place = " + newMapData.getPlace());
        mapDataList.add(newMapData);
        Log.e("AddData","Data added. Count = " + mapDataList.size());
        Updated = false;

    }


    public void ShowDialog(){
        FragmentManager fragmentManager = getFragmentManager();
        SearchDialog searchDialog = new SearchDialog();

        searchDialog.show(fragmentManager,"");
    }



    public static void Navigate(MapData mapData, Context context){
        Log.d("navigate", "Navigate method called on " + mapData.getPlace());

    // create intent to navigate to the destination contained in mapData.place
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + mapData.getPlace()) );
    // add flag in order to start activity from RecyclerView
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    // Get application context and Launch activity to handle the intent
        context.getApplicationContext().startActivity(intent);
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
