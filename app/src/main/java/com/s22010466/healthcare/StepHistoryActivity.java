package com.s22010466.healthcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

public class StepHistoryActivity extends AppCompatActivity {
    DatabaseHelper hCareDb;
    TableLayout tableLayout;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_history);

        hCareDb = new DatabaseHelper(this);
        tableLayout = findViewById(R.id.tableLayoutStepData);

        // Retrieving user's username from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("UserPreferences",
                Context.MODE_PRIVATE);
        String username = preferences.getString("username", "");

        // Getting all the data of the user
        Cursor results = hCareDb.displayData(username);

        // Getting the user id of the user
        if (results.moveToNext()) {
            user_id = results.getString(0);
        }

        // To get existing step details
        Cursor cursor = hCareDb.displayStepCount(user_id);

        if (cursor != null && cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                TableRow row = new TableRow(this);

                TextView dateTextView = new TextView(this);
                dateTextView.setText(cursor.getString(2));
                row.addView(dateTextView);

                TextView timeTextView = new TextView(this);
                timeTextView.setText(cursor.getString(3));
                row.addView(timeTextView);

                TextView stepCountTextView = new TextView(this);
                stepCountTextView.setText(cursor.getString(4));
                row.addView(stepCountTextView);

                TextView distanceTextView = new TextView(this);
                distanceTextView.setText(cursor.getString(5));
                row.addView(distanceTextView);

                TextView caloriesTextView = new TextView(this);
                caloriesTextView.setText(cursor.getString(6));
                row.addView(caloriesTextView);

                tableLayout.addView(row);

                // UI for the table rows
                // For text view
                dateTextView.setBackground(getResources().getDrawable(R.drawable.table_view));
                timeTextView.setBackground(getResources().getDrawable(R.drawable.table_view));
                stepCountTextView.setBackground(getResources().getDrawable(R.drawable.table_view));
                distanceTextView.setBackground(getResources().getDrawable(R.drawable.table_view));
                caloriesTextView.setBackground(getResources().getDrawable(R.drawable.table_view));

                dateTextView.setTextColor(getResources().getColor(R.color.dark_grey));
                timeTextView.setTextColor(getResources().getColor(R.color.dark_grey));
                stepCountTextView.setTextColor(getResources().getColor(R.color.dark_grey));
                distanceTextView.setTextColor(getResources().getColor(R.color.dark_grey));
                caloriesTextView.setTextColor(getResources().getColor(R.color.dark_grey));
            }
        }

        if (cursor != null) {
            cursor.close();
        }

    }
}