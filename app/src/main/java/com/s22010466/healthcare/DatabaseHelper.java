package com.s22010466.healthcare;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "HealthCare.db";

    //User Table
    public static final String USER_TABLE_NAME = "user_table";
    public static final String COL1 = "ID";
    public static final String COL2 = "NAME";
    public static final String COL3 = "USERNAME";
    public static final String COL4 = "DATE_OF_BIRTH";
    public static final String COL5 = "HEIGHT";
    public static final String COL6 = "WEIGHT";
    public static final String COL7 = "DISEASES_DIAGNOSED";
    public static final String COL8 = "PASSWORD";

    // Medicine Table
    public static final String MED_TABLE_NAME = "medicine_table";
    public static final String MED_COL1 = "MED_ID";
    public static final String MED_COL2 = "USER_ID";
    public static final String MED_COL3 = "NAME";
    public static final String MED_COL4 = "DOSAGE";
    public static final String MED_COL5 = "TIME";

    // Steps Tracker Table
    public static final String TRACK_TABLE_NAME = "step_tracker_table";
    public static final String TRACK_COL1 = "TRACK_ID";
    public static final String TRACK_COL2 = "USER_ID";
    public static final String TRACK_COL3 = "DATE";
    public static final String TRACK_COL4 = "Time";
    public static final String TRACK_COL5 = "STEP_COUNT";
    public static final String TRACK_COL6 = "DISTANCE_TRAVELLED";
    public static final String TRACK_COL7 = "CALORIES_BURNT";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
        SQLiteDatabase db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Log.d("DatabaseHelper", "onCreate() called");
        db.execSQL("create table " + USER_TABLE_NAME + "(ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                " NAME TEXT, USERNAME TEXT, DATE_OF_BIRTH TEXT, HEIGHT FLOAT, WEIGHT FLOAT, " +
                "DISEASES_DIAGNOSED TEXT, PASSWORD TEXT)");

        db.execSQL("create table " + MED_TABLE_NAME + "(MED_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USER_ID INTEGER, NAME TEXT, DOSAGE TEXT, TIME TEXT, FOREIGN KEY(USER_ID) REFERENCES "+ USER_TABLE_NAME + "(ID))");

        db.execSQL("create table " + TRACK_TABLE_NAME + "(TRACK_ID INTEGER PRIMARY KEY AUTOINCREMENT," +
                "USER_ID INTEGER, DATE TEXT, TIME TEXT, STEP_COUNT FLOAT, DISTANCE_TRAVELLED FLOAT," +
                "CALORIES_BURNT FLOAT, FOREIGN KEY(USER_ID) REFERENCES "+ USER_TABLE_NAME + "(ID))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ MED_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ TRACK_TABLE_NAME);
        onCreate(db);
    }

    //Used to add data taken from the user when signing up
    public boolean insertData(String name, String username, String dob, float height, float weight,
                              String diseases, String password){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2,name);
        contentValues.put(COL3,username);
        contentValues.put(COL4,dob);
        contentValues.put(COL5,height);
        contentValues.put(COL6,weight);
        contentValues.put(COL7,diseases);
        contentValues.put(COL8,password);
        
        long result = db.insert(USER_TABLE_NAME,null,contentValues);
        return result != -1;
    }

    //For user validation and identification
    public boolean checkUsername(String username){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + USER_TABLE_NAME + " where " + COL3
                + "= ?", new String[]{username});

        if(cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean checkEmailPassword(String username, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + USER_TABLE_NAME + " where " + COL3 + " = ?"
                + " and " + COL8 + " = ?", new String[]{username,password});

        if(cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    public boolean updatePassword(String username, String confirmPassword) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL8,confirmPassword);

        db.update(USER_TABLE_NAME,contentValues,COL3 + " = ?", new String[] {username});
        return true;
    }

    // Used to display all the details related to a user
    public Cursor displayData(String username) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + USER_TABLE_NAME + " where " + COL3 + " = ?",
                new String[]{username});
        return result;
    }

    // Used in settings fragment to update height and weight if new provided
    public boolean updateHeightAndWeight(String username,String height,String weight) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL5,height);
        contentValues.put(COL6,weight);

        db.update(USER_TABLE_NAME,contentValues,COL3 + " = ?", new String[] {username});
        return true;
    }

    // Used to add details of medicines taken from the user
    public boolean insertMedicineData(String user_id,String med_name, String dosage, String time){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(MED_COL2,user_id);
        contentValues.put(MED_COL3,med_name);
        contentValues.put(MED_COL4,dosage);
        contentValues.put(MED_COL5,time);

        long result = db.insert(MED_TABLE_NAME,null,contentValues);
        return result != -1;
    }

    // Used to display all the details related to medicines taken by user
    public Cursor displayMedicineData(String user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + MED_TABLE_NAME + " where " + MED_COL2 + " = ?",
                new String[]{user_id});
        return result;
    }

    // Used to delete details of medicine
    public void deleteMedicineData(String user_id, String medName, String medDosage, String medTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + MED_TABLE_NAME + " WHERE " + MED_COL2 + " = ? AND " + MED_COL3 + " = ? AND " +
                MED_COL4 + " = ? AND " + MED_COL5 + " = ?",
                new String[]{user_id, medName, medDosage, medTime});
    }

    // Used to add the step counts
    public boolean insertStepCount(String user_id,String date, String time, float stepCount, float distance, float calories){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(TRACK_COL2,user_id);
        contentValues.put(TRACK_COL3,date);
        contentValues.put(TRACK_COL4,time);
        contentValues.put(TRACK_COL5,stepCount);
        contentValues.put(TRACK_COL6,distance);
        contentValues.put(TRACK_COL7,calories);

        long result = db.insert(TRACK_TABLE_NAME,null,contentValues);
        return result != -1;
    }

    // Checks if the step count for date exists
    public boolean checkStepForDateExists(String user_id, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TRACK_TABLE_NAME + " where " + TRACK_COL1 + " = ? and "
                + TRACK_COL3 + " = ?", new String[]{user_id,date});

        if(cursor.getCount() > 0) {
            return true;
        } else {
            return false;
        }
    }

    //Used to update step count if date already exists
    public boolean updateStepCount(String userId, String date, String currentTime, float stepCount, float distance, float calories) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TRACK_COL5, stepCount);
        contentValues.put(TRACK_COL4, currentTime);
        contentValues.put(TRACK_COL6, distance);
        contentValues.put(TRACK_COL7, calories);

        db.update(TRACK_TABLE_NAME, contentValues, TRACK_COL1 + " = ? AND " + TRACK_COL3 + " = ?", new String[]{userId, date});
        return true;
    }

    // Used to display all the step count
    public Cursor displayStepCount(String user_id) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor result = db.rawQuery("select * from " + TRACK_TABLE_NAME + " where " + TRACK_COL2 + " = ?",
                new String[]{user_id});
        return result;
    }
}
