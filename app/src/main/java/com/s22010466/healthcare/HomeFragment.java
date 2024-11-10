package com.s22010466.healthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class HomeFragment extends Fragment {
    DatabaseHelper hCareDb;
    TextView textHeight,textWeight,textBMI,textResult;

    public HomeFragment(){
        // require a empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        hCareDb = new DatabaseHelper(getActivity());

        textHeight = view.findViewById(R.id.textViewHeight);
        textWeight = view.findViewById(R.id.textViewWeight);
        textBMI = view.findViewById(R.id.textViewBMI);
        textResult = view.findViewById(R.id.textViewResult);

        VideoView videoView = view.findViewById(R.id.videoView);
        videoView.setVideoPath("android.resource://" + getActivity().getPackageName() +
                "/" + R.raw.bmi_calculation);

        //To start the video only when the user clicks
        videoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoView.start();
            }
        });

        MediaController mediaController = new MediaController(getActivity());
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);

        // Retrieving data from SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPreferences",
                Context.MODE_PRIVATE);
        String height = preferences.getString("height","");
        String weight = preferences.getString("weight","");

        textHeight.setText("Height(m): " + height);
        textWeight.setText("Weight(kg): " + weight);

        // Displaying the bmi value and result
        calculateBMI(height,weight);

        return view;
    }

    public void calculateBMI(String height, String weight) {
        float height_float = Float.parseFloat(height);
        float weight_float = Float.parseFloat(weight);

        float bmi = weight_float / (height_float * height_float);
        String result = "";

        String rounding_bmi = String.format("%.2f",bmi);
        textBMI.setText("BMI: " + rounding_bmi);

        //Updating the bmi result according to bmi value
        if(bmi < 18.5) {
            result = "Underweight";
        } else if (bmi >= 18.5 && bmi < 24.9) {
            result = "Healthy";
        } else if (bmi >= 24.9 && bmi < 30) {
            result = "Overweight";
        } else if (bmi >= 30) {
            result = "Suffering from obesity";
        }

        textResult.setText("Result: " + result);
    }
}
