package com.fastmaps.andrew.fastmaps;

import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Andrew on 6/16/2015.
 */
public class SearchDialog extends DialogFragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search_dialog, container);
        getDialog().setTitle("Add Shortcut");


        final EditText editTextName = (EditText) view.findViewById(R.id.editTextName);
        final EditText editTextPlace = (EditText) view.findViewById(R.id.editTextPlace);
        final Context context = getActivity();

        Button button = (Button) view.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean checkName = editTextName.getText().toString().equals(""); // Make sure Name field is filled
                Boolean checkPlace = editTextPlace.getText().toString().equals(""); // Make sure Place field is filled
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
                    MainActivity.AddData(editTextName.getText().toString(), editTextPlace.getText().toString());
                    MainActivity.Updated = true;
                    dismiss();
                }
            }

        });
        return view;
    }


}
