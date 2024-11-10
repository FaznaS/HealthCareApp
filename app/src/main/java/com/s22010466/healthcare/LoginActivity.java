package com.s22010466.healthcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {
    private DatabaseHelper hCareDb;
    TextView textForgot,textSignUp;
    private EditText editUsername,editPassword;
    private Button btnLogin;
    private String height_given,weight_given;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        hCareDb = new DatabaseHelper(this);

        editUsername = findViewById(R.id.editTextUsername);
        editPassword = findViewById(R.id.editTextPassword);
        textForgot = findViewById(R.id.textForgot);
        textSignUp = findViewById(R.id.textSignUp);
        btnLogin = findViewById(R.id.btnLogin);

        textForgot.setOnClickListener(v -> {
                    Intent intent = new Intent(LoginActivity.this,
                            ForgotPasswordActivity.class);
                    startActivity(intent);
                }
        );

        textSignUp.setOnClickListener(v -> {
                    Intent intent = new Intent(LoginActivity.this,SignUpActivity.class);
                    startActivity(intent);
                }
        );

        checkCredentials();
    }

    public void checkCredentials(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String d_username = editUsername.getText().toString();
                String d_password = editPassword.getText().toString();

                if(d_username.isEmpty() || d_password.isEmpty()) {
                    Toast.makeText(LoginActivity.this,"Both fields are required",
                            Toast.LENGTH_SHORT).show();
                } else {
                    boolean checkSuccess = hCareDb.checkEmailPassword(d_username,d_password);

                    if(checkSuccess) {
                        // Getting data based on the data given by the user
                        Cursor results = hCareDb.displayData(d_username);

                        if (results.moveToNext()) {
                            height_given = results.getString(4);
                            weight_given = results.getString(5);
                        }

                        // Storing username,height and weight into SharedPreferences to use in other UIs
                        SharedPreferences preferences = getSharedPreferences("UserPreferences",
                                MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("username", d_username);
                        editor.putString("height", height_given);
                        editor.putString("weight", weight_given);
                        editor.commit();

                        Intent intent = new Intent(LoginActivity.this, NavigationActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(LoginActivity.this,"Invalid Credentials",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
