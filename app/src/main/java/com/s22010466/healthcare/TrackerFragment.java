package com.s22010466.healthcare;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class TrackerFragment extends Fragment implements SensorEventListener {
    DatabaseHelper hCareDb;
    String user_id;
    private SensorManager sensorManager;
    private Sensor heartRateSensor, stepCounterSensor;
    private TextView heartRate, stepCounter, exerciseTime, restTime;
    private ImageButton viewMoreBtn;
    private Button btnExercise, btnStop;
    private float heartRateReading, stepCounterReading, distanceReading, caloriesReading;
    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1;

    // Number of steps walked the whole day
    private float total_steps = 0;
    // Number of steps walked at the beginning of the day
    private float initial_steps = 0;
    // The values step_length & calories_per_step are assumed based on data obtained by searching
    private static final float step_length = 0.78f;
    private static final float calories_per_step = 0.04f;
    private boolean isExerciseTimeStarted = false;
    private boolean isExerciseTimePaused = false;
    private boolean isRestTimeStarted = false;
    private boolean isRestTimePaused = false;
    private long exerciseTimeStarted;
    private long exerciseTimePaused = 0;
    private long restTimeStarted;
    private long restTimePaused = 0;
    private Handler exerciseTimeHandler = new Handler();
    private Handler restTimeHandler = new Handler();

    // Setting Exercise Timer
    private Runnable exerciseTimeRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - exerciseTimeStarted;
            int seconds = (int) (millis / 1000);
            int min = seconds / 60;
            seconds = seconds % 60;
            exerciseTime.setText(String.format(Locale.getDefault(), "Time Spent: %02d:%02d", min, seconds));
            exerciseTimeHandler.postDelayed(this, 1000);
        }
    };

    // Setting Rest Timer
    private Runnable restTimeRunnable = new Runnable() {
        @Override
        public void run() {
            long millis = System.currentTimeMillis() - restTimeStarted;
            int seconds = (int) (millis / 1000);
            int min = seconds / 60;
            seconds = seconds % 60;
            restTime.setText(String.format(Locale.getDefault(), "Time Rested: %02d:%02d", min, seconds));
            restTimeHandler.postDelayed(this, 1000);
        }
    };

    public TrackerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_tracker, container, false);

        hCareDb = new DatabaseHelper(getActivity());

        //Checking if permission to use sensors is granted
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            // Request Permission if not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);
        }

        // Retrieving user's username from SharedPreferences
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPreferences",
                Context.MODE_PRIVATE);
        String username = preferences.getString("username", "");

        // Getting all the data of the user
        Cursor results = hCareDb.displayData(username);

        // Getting the user id of the user
        if (results.moveToNext()) {
            user_id = results.getString(0);
        }

        //loadInitialSteps();

        //Text Views
        heartRate = view.findViewById(R.id.textViewHeartRate);
        stepCounter = view.findViewById(R.id.textViewStepsWalked);
        viewMoreBtn = view.findViewById(R.id.imageButtonVieMore);
        exerciseTime = view.findViewById(R.id.textViewExerciseTime);
        restTime = view.findViewById(R.id.textViewRestTime);
        btnExercise = view.findViewById(R.id.btnExercise);
        btnStop = view.findViewById(R.id.btnStopTimers);

        viewMoreBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(requireContext(), StepHistoryActivity.class);
                startActivity(intent);
            }
        });

        sensorManager = (SensorManager) requireContext().getSystemService(Context.SENSOR_SERVICE);

        PeriodicWorkRequest saveStepsWorkRequest = new PeriodicWorkRequest.Builder(StepCountWorker.class, 15, TimeUnit.MINUTES).build();
        WorkManager.getInstance(getContext()).enqueue(saveStepsWorkRequest);

        exerciseTimeStarted = System.currentTimeMillis();
        restTimeStarted = System.currentTimeMillis();

        btnExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Timer starts when button is clicked

                if (!isExerciseTimeStarted) {
                    //Starting the timer for exercise
                    exerciseTimeStarted = System.currentTimeMillis();
                    exerciseTimeHandler.postDelayed(exerciseTimeRunnable, 0);
                    isExerciseTimeStarted = true;
                    btnExercise.setText("PAUSE");
                } else {
                    if (isExerciseTimePaused) {
                        isExerciseTimePaused = false;
                        btnExercise.setText("PAUSE");
                        exerciseTimeStarted = System.currentTimeMillis() - exerciseTimePaused;
                        exerciseTimeHandler.postDelayed(exerciseTimeRunnable, 0);

                        // Pause rest time when exercise is resumed
                        if (isRestTimeStarted) {
                            restTimeHandler.removeCallbacks(restTimeRunnable);
                            restTimePaused = System.currentTimeMillis() - restTimeStarted;
                            isRestTimePaused = true;
                        }

                    } else {
                        // Pausing exercise timer
                        isExerciseTimePaused = true;
                        btnExercise.setText("RESUME");
                        exerciseTimeHandler.removeCallbacks(exerciseTimeRunnable);
                        exerciseTimePaused = System.currentTimeMillis() - exerciseTimeStarted;

                        // Start rest timer when exercise time is paused
                        if (!isRestTimeStarted) {
                            isRestTimeStarted = true;
                            restTimeStarted = System.currentTimeMillis();
                            restTimeHandler.postDelayed(restTimeRunnable, 0);
                        } else {
                            restTimeStarted = System.currentTimeMillis() - restTimePaused;
                            restTimeHandler.postDelayed(restTimeRunnable, 0);
                            isRestTimePaused = false;
                        }
                    }
                }
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Stopping exercise timer
                exerciseTimeHandler.removeCallbacks(exerciseTimeRunnable);

                // Stopping rest timer
                restTimeHandler.removeCallbacks(restTimeRunnable);

                // Reset timer variables
                isExerciseTimeStarted = false;
                isExerciseTimePaused = false;
                isRestTimeStarted = false;
                isRestTimePaused = false;
                exerciseTimeStarted = System.currentTimeMillis();
                exerciseTimePaused = 0;
                restTimeStarted = System.currentTimeMillis();
                restTimePaused = 0;

                // Update UI
                exerciseTime.setText("Time Spent: 00:00");
                restTime.setText("Time Rested: 00:00");
                btnExercise.setText("START");
            }
        });

        return view;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d("TrackerFragment", "onSensorChanged() called");
        //Getting Reading from heart rate sensor
        if (event.sensor.getType() == Sensor.TYPE_HEART_RATE) {
            heartRateReading = event.values[0];
            heartRate.setText("Heart Rate: " + heartRateReading);
        }

        //Getting Reading from step counter sensor
        if (event.sensor.getType() == Sensor.TYPE_STEP_COUNTER) {
            total_steps = event.values[0];
            stepCounterReading = total_steps - initial_steps;
            distanceReading = stepCounterReading * step_length;
            caloriesReading = stepCounterReading * calories_per_step;

            //Updating the readings in text view
            stepCounter.setText("Step Count: " + stepCounterReading + "\n\n" +
                    "Distance: " + distanceReading + "\n\n" +
                    "Calories Burnt: " + caloriesReading);

            saveSteps(total_steps, distanceReading, caloriesReading);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();

        heartRateSensor = sensorManager.getDefaultSensor(Sensor.TYPE_HEART_RATE);
        stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);

        if (heartRateSensor != null) {
            sensorManager.registerListener(this, heartRateSensor, SensorManager.SENSOR_DELAY_NORMAL);
        } else {
            heartRate.setText("Heart Rate Sensor is undetected");
            Toast.makeText(getContext(), "Heart Rate Sensor is Undetected", Toast.LENGTH_SHORT).show();
        }

        if (stepCounterSensor != null) {
            sensorManager.registerListener(this, stepCounterSensor, SensorManager.SENSOR_DELAY_GAME);

            stepCounter.setText("Step Count: " + stepCounterReading + "\n\n" +
                    "Distance: " + distanceReading + "\n\n" +
                    "Calories Burnt: " + caloriesReading);
        } else {
            stepCounter.setText("Step Counter Sensor is undetected");
            Toast.makeText(getContext(), "Step Counter Sensor is Undetected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        // Only heart rate sensor is unregistered because steps have to be detected even when app is closed
        sensorManager.unregisterListener(this, heartRateSensor);
        exerciseTimeHandler.removeCallbacks(exerciseTimeRunnable);
        restTimeHandler.removeCallbacks(restTimeRunnable);

        // Saving number of steps walked
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("STEPS_READING", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat("INITIAL_STEPS", 0);
        editor.commit();
    }

    //    private void loadInitialSteps() {
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("STEPS_READING", Context.MODE_PRIVATE);
//        String lastDate = sharedPreferences.getString("DATE", "");
//
//        if (lastDate.equals(getCurrentDate())) {
//            initial_steps = sharedPreferences.getFloat("INITIAL_STEPS", 0);
//        } else {
//            // New day, reset previous total steps
//            initial_steps = total_steps; // Assuming totalSteps is the steps walked today
//            saveSteps(total_steps,distanceReading,caloriesReading);
//        }
//    }
//
    private void saveSteps(float steps, float distance, float calories) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("STEPS_READING", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("DATE", getCurrentDate());
        editor.putFloat("STEPS", steps);
        editor.apply();

        boolean dataExists = hCareDb.checkStepForDateExists(user_id, getCurrentDate());

        if (dataExists) {
            hCareDb.updateStepCount(user_id, getCurrentDate(), getCurrentTime(), steps, distance, calories);
        } else {
            hCareDb.insertStepCount(user_id, getCurrentDate(), getCurrentTime(), steps, distance, calories);
        }
    }

    private String getCurrentDate() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    private String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return timeFormat.format(new Date());
    }
}