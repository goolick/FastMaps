package com.fastmaps.andrew.fastmaps;


import android.app.ActionBar;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataApi;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

/**
 * Created by Andrew on 6/16/2015.
 */
public class SearchDialog extends DialogFragment{

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;
    private PlaceAutocompleteAdapter mAdapter;

    private static final LatLngBounds BOUNDS_GREATER_SYDNEY = new LatLngBounds(
            new LatLng(-34.041458, 150.790100), new LatLng(-33.682247, 151.383362));


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_dialog, container);

        getDialog().setTitle("Add Shortcut");

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().show();
        getDialog().getWindow().setAttributes(lp);


        final EditText editTextName = (EditText) view.findViewById(R.id.editTextName);
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);
        final Context context = getActivity();

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean checkName = editTextName.getText().toString().equals(""); // Make sure Name field is filled
                Boolean checkPlace = autoCompleteTextView.getText().toString().equals(""); // Make sure Place field is filled
                Log.d("checkName", checkName.toString());
                Log.d("checkPlace", checkPlace.toString());

                if (checkName || checkPlace) {
                    if (checkPlace) {
                        Toast.makeText(context, "Enter Place", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        Toast.makeText(context, "Enter Name", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    MainActivity.AddData(editTextName.getText().toString(), autoCompleteTextView.getText().toString());
                    MainActivity.Updated = true;
                    dismiss();
                }
            }

        });

        //Setup GoogleApiClient for getting autocomplete information
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();

        Log.d("adapter", "googleapiclient built and connected");


        // Create and attach adapter
        mAdapter = new PlaceAutocompleteAdapter(context, android.R.layout.simple_list_item_1,
                mGoogleApiClient, BOUNDS_GREATER_SYDNEY, null);
        autoCompleteTextView.setAdapter(mAdapter);


        return view;
    }

    //Disconnect from Google API Client if the interface if the view is destroyed
    @Override
    public void onDestroyView() {
        mGoogleApiClient.disconnect();
        super.onDestroyView();
    }
}
