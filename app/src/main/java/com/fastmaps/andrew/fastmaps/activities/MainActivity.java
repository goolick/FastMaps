package com.fastmaps.andrew.fastmaps.activities;

import android.app.FragmentManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import com.fastmaps.andrew.fastmaps.DragSortRecycler;
import com.fastmaps.andrew.fastmaps.MapData;
import com.fastmaps.andrew.fastmaps.adapters.MyAdapter;
import com.fastmaps.andrew.fastmaps.adapters.MyDatabaseAdapter;
import com.fastmaps.andrew.fastmaps.R;
import com.fastmaps.andrew.fastmaps.SearchDialog;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.melnykov.fab.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class MainActivity extends ActionBarActivity {

    public MyAdapter mAdapter;
    public static LatLngBounds CURRENT_BOUNDS;
    public static int Radio_selected = 3;
    public final long ADD_DURATION = 650;
    public final long DELETE_DURATION = 650;
    public static final int WALK_BUTTON = 1;
    public static final int BIKE_BUTTON = 2;
    public static final int DRIVE_BUTTON = 3;
    public static final int MIN_ROW = 0;
    public static final int MAX_ROW = 100;
    private static List<MapData> mapDataList = new ArrayList<>(MAX_ROW);
    public static RadioButton walkButton;
    public static RadioButton bikeButton;
    public static RadioButton driveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView mRecyclerView;
        RecyclerView.LayoutManager mLayoutManager;
        FloatingActionButton floatingActionButton;


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Find buttons and set use OnClickListeners to keep track of currently selected button
        walkButton = (RadioButton) findViewById(R.id.walk_button);
        driveButton = (RadioButton) findViewById(R.id.drive_button);
        bikeButton = (RadioButton) findViewById(R.id.bike_button);


        // Get data from SQLiteDatabse
        MyDatabaseAdapter myDatabaseAdapter = new MyDatabaseAdapter(this);
        mapDataList = myDatabaseAdapter.GetAllData();

        /* Reference to FAB and onClickListener - show the dialog box upon click */
        floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        // Create LinearLayoutManager and Adapter
        mLayoutManager = new LinearLayoutManager(this);
        mAdapter = new MyAdapter(this, mapDataList);

        // Set up DefaultItemAnimator with fade in/out times
        DefaultItemAnimator defaultItemAnimator = new DefaultItemAnimator();
        defaultItemAnimator.setAddDuration(ADD_DURATION);
        defaultItemAnimator.setRemoveDuration(DELETE_DURATION);

        // Create RecyclerView and connect to LayoutManager, Adapter, and Item Animator
        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setItemAnimator(defaultItemAnimator);

        // Set up DragSortRecycler for drag/drop rearrangement
        DragSortRecycler dragSortRecycler = new DragSortRecycler();
        dragSortRecycler.setViewHandleId(R.id.primary_text);
        dragSortRecycler.setFloatingAlpha(0.4f);
        dragSortRecycler.setFloatingBgColor(0x8090CAF9);
        dragSortRecycler.setAutoScrollSpeed(0.3f);
        dragSortRecycler.setAutoScrollWindow(0.1f);

        dragSortRecycler.setOnItemMovedListener(new DragSortRecycler.OnItemMovedListener() {
            @Override
            public void onItemMoved(int from, int to) {
                //Log.d("onItemMoved", "onItemMoved " + from + " to " + to);
                MapData mapData = mapDataList.remove(from);
                mapDataList.add(to, mapData);
                mAdapter.notifyDataSetChanged();
            }
        });

        mRecyclerView.addItemDecoration(dragSortRecycler);
        mRecyclerView.addOnItemTouchListener(dragSortRecycler);
        mRecyclerView.setOnScrollListener(dragSortRecycler.getScrollListener());
    }

    @Override
    protected void onResume() {
        // Use Network provided location to create bounds, used for Place autocomplete to favor
        // current location
        LocationManager mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        Location location = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            LatLng latlng1 = new LatLng(location.getLatitude() - 1, location.getLongitude() - 1);
            LatLng latlng2 = new LatLng(location.getLatitude() + 1, location.getLongitude() + 1);

            CURRENT_BOUNDS = new LatLngBounds(latlng1, latlng2);
        }

        // Make navigation type buttons invisible if there is nothing in the list
        if (mapDataList.isEmpty()) {
            HideButtons();
        }
        else {
            showButtons();
        }
        super.onResume();
    }
    public static void HideButtons() {
        bikeButton.setVisibility(View.INVISIBLE);
        driveButton.setVisibility(View.INVISIBLE);
        walkButton.setVisibility(View.INVISIBLE);
    }

    public void showButtons() {
        bikeButton.setVisibility(View.VISIBLE);
        driveButton.setVisibility(View.VISIBLE);
        walkButton.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onStop() {
        //Log.d("onStop", "onStop run");
        // When the activity is stopped, replace all data in SQLite database with what is contained
        // in mapDataList variable
        MyDatabaseAdapter myDatabaseAdapter = new MyDatabaseAdapter(this);
        myDatabaseAdapter.DeleteAllData();
        for (int i = 0; i< mapDataList.size(); i++){
            myDatabaseAdapter.AddData(mapDataList.get(i).getName(), mapDataList.get(i).getPlace());
            //Log.d("for", "" + i + " " + mapDataList.get(i).getName() + " " +  mapDataList.get(i).getPlace());
        }
        // When exiting the activity, reset Radio_Selected to drive, the default
        //Radio_selected = DRIVE_BUTTON;


        super.onStop();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void addData(String name, String place){
    // Take in a name and place and add them to the list of MapData */
        MapData newMapData = new MapData();
        newMapData.setName(name);
        newMapData.setPlace(place);
        //Log.e("Name and address", "Name = " + newMapData.getName() + " place = " + newMapData.getPlace());
        mapDataList.add(newMapData);
        //Log.e("addData", "Data added. Count = " + mapDataList.size());
    }

    // Public method to open the SearchDialog, which allows the user to enter a new map location
    public void showDialog(){
        FragmentManager fragmentManager = getFragmentManager();
        SearchDialog searchDialog = new SearchDialog();
        searchDialog.show(fragmentManager,"");
    }

    public static void Navigate(MapData mapData, Context context) {
        //Log.d("navigate", "Navigate method called on " + mapData.getPlace());
        // First, geocode the string supplied from autocomplete
        String coords = "";
        Geocoder geocoder = new Geocoder(context, Locale.US);
        try {
            // Take first suggestion from geocoder object
            List<Address> coordinates = geocoder.getFromLocationName(mapData.getPlace(),1);

            //If a geocode result is supplied, use it. Otherwise, use the string from mapData
            if (coordinates.size() > 0){
                double latitude = coordinates.get(coordinates.size() - 1).getLatitude();
                double longitude = coordinates.get(coordinates.size() - 1).getLongitude();
                coords = (latitude + ", " + longitude);}

            else{
                coords = mapData.getPlace();}

        } catch (IOException e) {
            e.printStackTrace();
        }

        // create intent to navigate to the destination contained in mapData.place
        // switch on selected radio button
        if (walkButton.isChecked()){
            Radio_selected = WALK_BUTTON;
        }
        if (bikeButton.isChecked()){
            Radio_selected = BIKE_BUTTON;
        }
        if (driveButton.isChecked()){
            Radio_selected = DRIVE_BUTTON;
        }

        Intent intent;
        switch (Radio_selected){
            case WALK_BUTTON: intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + coords + "&mode=w"));
                break;

            case BIKE_BUTTON: intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + coords + "&mode=b"));
                break;

            default: intent = new Intent(Intent.ACTION_VIEW, Uri.parse("google.navigation:q=" + coords));
                break;
        }
        // add flag in order to start activity from RecyclerView
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Get application context and Launch activity to handle the intent
        // Handle exceptions in case the user does not have a maps application available
        try {
            context.getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context.getApplicationContext(), "Please install a map application such as Google Maps", Toast.LENGTH_LONG)
                    .show();
        }
        }

    // method to open a location in maps application, given a MapData object
        public static void Direct(MapData mapData, Context context) {
        //Log.d("Direct", "Direct method called on " + mapData.getPlace());

        try {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=" + mapData.getPlace()));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.getApplicationContext().startActivity(intent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(context.getApplicationContext(), "Please install a map application such as Google Maps", Toast.LENGTH_LONG)
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
            mAdapter.notifyItemRangeRemoved(MIN_ROW, MAX_ROW);
        }

        return super.onOptionsItemSelected(item);
    }
}
