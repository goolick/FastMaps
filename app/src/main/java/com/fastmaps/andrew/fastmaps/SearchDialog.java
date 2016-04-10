package com.fastmaps.andrew.fastmaps;


import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.fastmaps.andrew.fastmaps.activities.MainActivity;
import com.fastmaps.andrew.fastmaps.adapters.PlaceAutocompleteAdapter;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.Places;

import java.util.ArrayList;
import java.util.List;

public class SearchDialog extends DialogFragment{

    public static final String SEARCH_ARGS_NAME = "ARG_NAME";
    public static final String SEARCH_ARGS_ADDRESS = "ARG_ADDRESS";
    public static final String SEARCH_ARGS_POSITION = "ARG_POSITION";
    private String name = "";
    private String address = "";
    private int position = -1;
    private MainActivity activity;
    /**
     * GoogleApiClient wraps our service connection to Google Play Services and provides access
     * to the user's sign in state as well as the Google's APIs.
     */
    protected GoogleApiClient mGoogleApiClient;
    public PlaceAutocompleteAdapter mAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.activity = (MainActivity) getActivity();
        if (getArguments() != null){
            name = getArguments().getString(SEARCH_ARGS_NAME);
            address = getArguments().getString(SEARCH_ARGS_ADDRESS);
            position = getArguments().getInt(SEARCH_ARGS_POSITION);
        }
    }

    private boolean isEditing(){
        return position != -1;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_dialog, container);

        getDialog().setTitle("Add Shortcut");
        final EditText editTextName = (EditText) view.findViewById(R.id.editTextName);
        final AutoCompleteTextView autoCompleteTextView = (AutoCompleteTextView) view.findViewById(R.id.autoCompleteTextView);

        // Initialize, in case something was supplied to fragment
        editTextName.setText(name);
        autoCompleteTextView.setText(address);

        // Get context from activity that called the dialog box
        final Context context = getActivity();

        // Action for 'Add' button
        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // If both fields are filled, call the addData method in MainActivity on the entries
                boolean nameIsMissing = editTextName.getText().toString().equals(""); // Make sure Name field is filled
                boolean placeIsMissing = autoCompleteTextView.getText().toString().equals(""); // Make sure Place field is filled
                //Log.d("nameIsMissing", nameIsMissing.toString());
                //Log.d("checkPlace", checkPlace.toString());
                if (nameIsMissing || placeIsMissing) {
                    if (placeIsMissing) {
                        Toast.makeText(context, "Enter Place", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Enter Name", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    if (isEditing()){
                        if (activity != null){
                            activity.mAdapter.deleteRow(position);
                            activity.addData(editTextName.getText().toString(), autoCompleteTextView.getText().toString());
                            activity.mAdapter.notifyItemChanged(position);
                        }
                    } else {
                        if (activity != null){
                            activity.addData(editTextName.getText().toString(), autoCompleteTextView.getText().toString());
                            activity.mAdapter.notifyItemInserted(activity.mAdapter.getItemCount());
                            activity.showButtons();
                        }
                    }
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
        location. For now, filter will not be used  The bug is being tracked at
        https://code.google.com/p/gmaps-api-issues/issues/detail?id=7933
        */

        /* 6/25, instead of the result from Autocomplete being used directly, the result is
        geocoded and used for the target of the intent
        */
        // Create filter to ensure places supplied by Google Place API are navigable
        List<Integer> filterTypes = new ArrayList<>();
   //     filterTypes.add(Place.TYPE_STREET_ADDRESS);
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