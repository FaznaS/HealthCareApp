package com.s22010466.healthcare;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MapFragment extends Fragment {
    private EditText editTextStart,editTextDestination;
    private Button btnGetDirection;


    public MapFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map, container, false);

        editTextStart = view.findViewById(R.id.editTextStartingLocation);
        editTextDestination = view.findViewById(R.id.editTextDestination);
        btnGetDirection = view.findViewById(R.id.btnGetDirection);

        btnGetDirection.setOnClickListener(v -> {
            String startingPoint = editTextStart.getText().toString();
            String destination = editTextDestination.getText().toString();

            if(startingPoint.equals("") || destination.equals("")){
                Toast.makeText(requireContext(),"Starting Location & Destination are Required",
                        Toast.LENGTH_SHORT).show();
            } else {
                getDirection(startingPoint,destination);
            }
        });

        return view;
    }

    public void getDirection(String start, String end) {
        try {
            Uri uri = Uri.parse("https://www.google.com/maps/dir/" + start + "/" + end);
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.setPackage("com.google.android.apps.maps");
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        catch (ActivityNotFoundException mapNotFound){
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps&hl=en&gl=US");
            Intent intent = new Intent(Intent.ACTION_VIEW,uri);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
}
