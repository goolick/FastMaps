package com.fastmaps.andrew.fastmaps;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
    private RadioGroup radioGroup;
    private RadioButton walkButton;
    private RadioButton bikeButton;
    private RadioButton driveButton;



    private static List<MapData> mapDataList = new ArrayList<>(25);
    public static Boolean Updated = false;
    public static int Radio_selected;
    public final long ADD_DURATION = 700;
    public final long DELETE_DURATION = 700;
    public static final int WALK_BUTTON = 1;
    public static final int BIKE_BUTTON = 2;
    public static final int DRIVE_BUTTON = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Set up radiogroup
        radioGroup = (RadioGroup) findViewById(R.id.radio_group);

        //Find buttons and set IDs
        walkButton = (RadioButton) findViewById(R.id.walk_button);

        driveButton = (RadioButton) findViewById(R.id.drive_button);

        bikeButton = (RadioButton) findViewById(R.id.bike_button);



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



        walkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Radio_selected = WALK_BUTTON;
            }
        });

        bikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Radio_selected = BIKE_BUTTON;
            }
        });

        driveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Radio_selected = DRIVE_BUTTON;
            }
        });


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

    // Public method to open the SearchDialog, which allows the user to enter a new map location
    public void ShowDialog(){
        FragmentManager fragmentManager = getFragmentManager();
        SearchDialog searchDialog = new SearchDialog();
        searchDialog.show(fragmentManager,"");
    }

    public int GetRadioID (){
        return radioGroup.getCheckedRadioButtonId();
    }

    public static void Navigate(MapData mapData, Context context) {
        Log.d("navigate", "Navigate method called on " + mapData.getPlace());
        Log.d("radiobutton", "radio selected: " + Radio_selected);
        // create intent to navigate to the destination contained in mapData.place
        // switch on selected radio button

        Intent intent;
        switch (Radio_selected){
            case WALK_BUTTON: intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + mapData.getPlace() + "&mode=w"));
                 break;

            case BIKE_BUTTON: intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + mapData.getPlace() + "&mode=b"));
                 break;

            default: intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + mapData.getPlace()));
                     break;
        }



        // add flag in order to start activity from RecyclerView
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Get application context and Launch activity to handle the intent
        // Handle exceptions in case the user does not have a maps application available
        try {
            context.getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context.getApplicationContext(), "Install Google Maps for best results", Toast.LENGTH_LONG)
                    .show();
        }
        }
    public static void Direct(MapData mapData, Context context) {
        Log.d("Direct", "Direct method called on " + mapData.getPlace());

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + mapData.getPlace()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);

        } catch (ActivityNotFoundException e) {
            Toast.makeText(context.getApplicationContext(), "No Maps application available!", Toast.LENGTH_LONG)
                    .show();
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            mapDataList.clear();
            mAdapter.notifyDataSetChanged();
        }

        return super.onOptionsItemSelected(item);
    }


}
