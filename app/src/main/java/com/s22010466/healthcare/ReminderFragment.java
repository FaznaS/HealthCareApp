package com.s22010466.healthcare;

import android.Manifest;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;

public class ReminderFragment extends Fragment {
    private static final int MY_PERMISSIONS_REQUEST_POST_NOTIFICATION = 1;
    DatabaseHelper hCareDb;
    private EditText medicineName,dosage,time;
    private TableLayout med_table;
    private Button btnAddToList;
    private ArrayList<String> data1 = new ArrayList<String>();
    private ArrayList<String> data2 = new ArrayList<String>();
    private ArrayList<String> data3 = new ArrayList<String>();
    String user_id, medName, medDosage, medTime, amPm;
    Integer hour;
    TimePickerDialog timePickerDialog;

    public ReminderFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminder, container, false);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
            // Request Permission if not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    MY_PERMISSIONS_REQUEST_POST_NOTIFICATION);
        }

        hCareDb = new DatabaseHelper(getActivity());

        medicineName = view.findViewById(R.id.editTextMedicineName);
        dosage = view.findViewById(R.id.editTextDosage);
        time = view.findViewById(R.id.editTextTime);
        btnAddToList = view.findViewById(R.id.btnAddMedicine);
        med_table = view.findViewById(R.id.medicine_table);

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

        // To get already existing medicine details
        Cursor medicine_details = hCareDb.displayMedicineData(user_id);

        // Getting existing medicine details to display in table view
        while (medicine_details.moveToNext()) {
            medName = medicine_details.getString(2);
            medDosage = medicine_details.getString(3);
            medTime = medicine_details.getString(4);

            addToList(medName,medDosage,medTime);
        }

        // To use time picker
        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog = new TimePickerDialog(requireContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if (hourOfDay >= 12) {
                            amPm = " PM";
                            if (hourOfDay > 12) {
                                hour = hourOfDay - 12;
                            } else {
                                hour = 12;
                            }
                        } else {
                            amPm = " AM";
                            if (hourOfDay == 0) {
                                hour = 12;
                            }
                        }
                        time.setText(String.format("%02d:%02d", hour, minute) + amPm);
                    }
                },0,0,false);

                timePickerDialog.show();
            }
        });

        // To add new medicine details
        btnAddToList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String medName = medicineName.getText().toString();
                String medDosage = dosage.getText().toString();
                String medTime = time.getText().toString();

                if (medName.isEmpty() || medDosage.isEmpty() || medTime.isEmpty()) {
                    Toast.makeText(getContext(),"All fields are required",Toast.LENGTH_SHORT).show();
                } else {
                    addToList(medName,medDosage,medTime);

                    //Adding the details to the database
                    boolean isInserted = hCareDb.insertMedicineData(user_id,medName,medDosage,medTime);

                    if (isInserted) {
                        Toast.makeText(getContext(),"Added Successfully",Toast.LENGTH_SHORT).show();
                        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS)
                                != PackageManager.PERMISSION_GRANTED) {
                            // Request Permission if not granted
                            ActivityCompat.requestPermissions(getActivity(),
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                    MY_PERMISSIONS_REQUEST_POST_NOTIFICATION);
                        }
                        // Set the alarm
                        setAlarm(medTime);
                    }
                }
            }
        });

        return view;
    }

    // Setting the alarm
    public void setAlarm(String medTime) {
        // Converting the time string to a Calendar object
        String[] timeParts = medTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1].split(" ")[0]);
        String amPm = timeParts[1].split(" ")[1];
        if (amPm.equals("PM")) {
            if (hour != 12) {
                hour += 12;
            }
        } else if (amPm.equals("AM")) {
            if (hour == 12) {
                hour = 0;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Ensuring the alarm is set for the future
        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }

        // Create a pending intent to trigger the BroadcastReceiver
        Intent intent = new Intent(getContext(), BroadcastForReminder.class);
        intent.putExtra("rMsg", "Take your medicines");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Set the alarm
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

        // setAndAllowWhileIdle requires API 23
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        }
    }

    // Cancelling the alarm
    public void cancelAlarm(String medTime) {
        // Converting the time string to a Calendar object
        String[] timeParts = medTime.split(":");
        int hour = Integer.parseInt(timeParts[0]);
        int minute = Integer.parseInt(timeParts[1].split(" ")[0]);
        String amPm = timeParts[1].split(" ")[1];
        if (amPm.equals("PM")) {
            if (hour != 12) {
                hour += 12;
            }
        } else if (amPm.equals("AM")) {
            if (hour == 12) {
                hour = 0;
            }
        }

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);

        // Create the same PendingIntent used to set the alarm
        Intent intent = new Intent(getContext(), BroadcastForReminder.class);
        intent.putExtra("rMsg", "Take your medicines");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT | PendingIntent.FLAG_IMMUTABLE);

        // Cancel the alarm
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(pendingIntent);
    }


    public void addToList(String medName,String medDosage,String medTime){
        data1.add(medName);
        data2.add(medDosage);
        data3.add(medTime);

        TableRow row = new TableRow(requireContext());

        // Detail Views
        TextView t1 = new TextView(requireContext());
        TextView t2 = new TextView(requireContext());
        TextView t3 = new TextView(requireContext());

        // Delete button
        ImageButton deleteBtn = new ImageButton(requireContext());

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Remove the row from the table
                med_table.removeView(row);

                // Remove the data from the database
                hCareDb.deleteMedicineData(user_id, medName, medDosage, medTime);

                // Remove the data from the ArrayLists
                data1.remove(medName);
                data2.remove(medDosage);
                data3.remove(medTime);

                // Cancel the alarm
                cancelAlarm(medTime);
            }
        });

        for(int i = 0; i < data1.size(); i++) {
            String mname = data1.get(i);
            String mdose = data2.get(i);
            String mtime = data3.get(i);

            t1.setText(mname);
            t2.setText(mdose);
            t3.setText(mtime);

            t1.setTextSize(16);
            t2.setTextSize(16);
            t3.setTextSize(16);
        }

        row.addView(t1);
        row.addView(t2);
        row.addView(t3);
        row.addView(deleteBtn);
        med_table.addView(row);

        // UI for the table rows
        // For text view
        t1.setBackground(getResources().getDrawable(R.drawable.table_view));
        t2.setBackground(getResources().getDrawable(R.drawable.table_view));
        t3.setBackground(getResources().getDrawable(R.drawable.table_view));

        t1.setTextColor(getResources().getColor(R.color.dark_grey));
        t2.setTextColor(getResources().getColor(R.color.dark_grey));
        t3.setTextColor(getResources().getColor(R.color.dark_grey));

        // For delete button
        deleteBtn.setImageResource(R.drawable.baseline_delete_forever_24);
        deleteBtn.setBackground(getResources().getDrawable(R.drawable.table_view));
        // Setting the width and height to match parent
        deleteBtn.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
    }
}