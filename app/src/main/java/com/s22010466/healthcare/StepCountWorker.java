package com.s22010466.healthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepCountWorker extends Worker {
    private DatabaseHelper hCareDb;
    String user_id,date;
    float steps, distanceReading, caloriesReading;
    float stepLength = 0.762f; // example step length in meters
    float caloriesPerStep = 0.05f; // example calories burnt per step

    public StepCountWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        hCareDb = new DatabaseHelper(getApplicationContext());
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("STEPS_READING", Context.MODE_PRIVATE);
        date = sharedPreferences.getString("DATE", "");
        steps = sharedPreferences.getFloat("STEPS", 0f);

        // Getting the username of the user logged in
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("UserPreferences", Context.MODE_PRIVATE);
        String username = preferences.getString("username", "");

        // Getting the details of the user
        Cursor results = hCareDb.displayData(username);

        // Getting the user id
        if (results.moveToNext()) {
            user_id = results.getString(0);
        }

        results.close();

        // Getting the date
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String currentDate = dateFormat.format(new Date());

        distanceReading = steps * stepLength;
        caloriesReading = steps * caloriesPerStep;

        // If the date already exists for the step count it is updated else its added
        if (date.equals(currentDate)) {
            boolean dataExists = hCareDb.checkStepForDateExists(user_id, date);

            if (dataExists) {
                hCareDb.updateStepCount(user_id, date, getCurrentTime(), steps, distanceReading, caloriesReading);
            } else {
                hCareDb.insertStepCount(user_id, date, getCurrentTime(), steps, distanceReading, caloriesReading);
            }
        }

        return Result.success();
    }

    private String getCurrentTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        return timeFormat.format(new Date());
    }
}
