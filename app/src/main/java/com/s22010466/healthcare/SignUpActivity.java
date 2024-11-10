package com.s22010466.healthcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SignUpActivity extends AppCompatActivity {
    DatabaseHelper hCareDb;
    EditText name,username,dateOfBirth,height,weight,diseasesDiagnosed,password,confirmPassword;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        hCareDb = new DatabaseHelper(this);

        btnSignUp = findViewById(R.id.btnSignUp);

        name = findViewById(R.id.editTextName);
        username = findViewById(R.id.editTextSignUpUsername); //Added after first Progress Review
        dateOfBirth = findViewById(R.id.editTextDateOfBirth);
        height = findViewById(R.id.editTextHeight);
        weight = findViewById(R.id.editTextWeight);
        diseasesDiagnosed = findViewById(R.id.editTextDiseaseDiagnosed);
        password = findViewById(R.id.editTextSignUpPassword); //Added after first Progress Review
        confirmPassword = findViewById(R.id.editTextSignUpConfirmPassword); //Added after first Progress Review

        addAndValidateData();
    }

    public void addAndValidateData(){
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d_name = name.getText().toString();
                String d_username = username.getText().toString();
                String d_dob = dateOfBirth.getText().toString();
                String d_height = height.getText().toString();
                String d_weight = weight.getText().toString();
                String d_diseases = diseasesDiagnosed.getText().toString();
                String d_password = password.getText().toString();
                String d_confirm_password = confirmPassword.getText().toString();

                if(d_name.isEmpty() || d_username.isEmpty() || d_dob.isEmpty() || d_height.isEmpty() ||
                        d_weight.isEmpty() ||d_diseases.isEmpty() || d_password.isEmpty() || d_confirm_password.isEmpty()) {
                    Toast.makeText(SignUpActivity.this,"All fields are required",Toast.LENGTH_SHORT).show();
                } else {
                    //Checking if username already exists
                    if(hCareDb.checkUsername(d_username)){
                        Toast.makeText(SignUpActivity.this,"Username already exists",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        //Proceeding only if password is equal to confirm password
                        if(d_password.equals(d_confirm_password)) {
                            //Converting height and weight values to float
                            float height_float = Float.parseFloat(d_height);
                            float weight_float = Float.parseFloat(d_weight);

                            boolean isInserted = hCareDb.insertData(d_name,d_username,d_dob,height_float,weight_float,d_diseases,d_password);

                            if(isInserted) {
                                Toast.makeText(SignUpActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();

                                // Storing data into SharedPreferences
                                SharedPreferences preferences = getSharedPreferences("UserPreferences", MODE_PRIVATE);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("name",d_name);
                                editor.putString("username", d_username);
                                editor.putString("height",d_height);
                                editor.putString("weight",d_weight);
                                editor.commit();

                                //Moving to Login page after successful registration
                                Intent intent = new Intent(SignUpActivity.this,LoginActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                        else {
                            Toast.makeText(SignUpActivity.this,"Confirm Password and Password don't match",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
}
