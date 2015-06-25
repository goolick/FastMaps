package com.fastmaps.andrew.fastmaps;


import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

public class SearchDialog extends DialogFragment{

    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;
    public PlaceAutocompleteAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_dialog, container);

        // Add title to Dialog
        getDialog().setTitle("Add Shortcut");

/*        // Maximize the width of the dialog box
        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(getDialog().getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height= WindowManager.LayoutParams.WRAP_CONTENT;
        getDialog().show();

        getDialog().getWindow().setAttributes(lp);
*/
        // Get layout components
        final EditText editTextName = (EditText) view.findViewById(R.id.editTextName);
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);

        // Get context from activity that called the dialog box
        final Context context = getActivity();

        // Action for 'Add' button
        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If both fields are filled, call the AddData method in MainActivity on the entries
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
                    // Call AddData and set the Updated flag as true
                    MainActivity.AddData(editTextName.getText().toString(), autoCompleteTextView.getText().toString());
                    MainActivity.mAdapter.notifyItemInserted(MainActivity.mAdapter.getItemCount());
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

        /* As on 6/24/2015, Google Places API is not supporting the ADDRESS place type for
        Autocomplete filter (there is a mistake in documentation suggesting otherwise)
        Until then, the PlaceAutocompleteAdapter will not return results. Unfiltered results
        should not be given, because several cases have been observed where a non-navigable location
        is supplied. This could lead to the user mistakenly starting a navigation to the incorrect
        location. For now, Autocomplete will not be used  The bug is being tracked at
        https://code.google.com/p/gmaps-api-issues/issues/detail?id=7933
        */

        // Create filter to ensure places supplied by Google Place API are navigable
        List<Integer> filterTypes = new ArrayList<>();
        filterTypes.add(Place.TYPE_STREET_ADDRESS);
        AutocompleteFilter filter = AutocompleteFilter.create(filterTypes);

        // Create and attach adapter
        mAdapter = new PlaceAutocompleteAdapter(context, android.R.layout.simple_list_item_1,
                mGoogleApiClient,MainActivity.CURRENT_BOUNDS , filter);
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