package com.s22010466.healthcare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends AppCompatActivity {
    private DatabaseHelper hCareDb;
    private EditText editUsername,editNewPassword,editConfirmPassword;
    private Button btnForgotPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        hCareDb = new DatabaseHelper(this);

        editUsername = findViewById(R.id.editTextUsername);
        editNewPassword = findViewById(R.id.editTextNewPassword);
        editConfirmPassword = findViewById(R.id.editTextConfirmPassword);

        //Opening LoginActivity when clicking on the btn
        btnForgotPassword = findViewById(R.id.btnForgotPassword);

        updatePassword();
    }

    public void updatePassword(){
        btnForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = editUsername.getText().toString();
                String newPassword = editNewPassword.getText().toString();
                String confirmPassword = editConfirmPassword.getText().toString();

                if(username.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(ForgotPasswordActivity.this,
                            "All fields are required", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Checking if username is already available in the system
                    if (hCareDb.checkUsername(username)) {
                        //Updating Password
                        if(newPassword.equals(confirmPassword)) {
                            boolean isUpdated = hCareDb.updatePassword(username,confirmPassword);

                            if(isUpdated) {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Password Updated Successfully", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(ForgotPasswordActivity.this,
                                        LoginActivity.class);
                                startActivity(intent);
                            }
                            else {
                                Toast.makeText(ForgotPasswordActivity.this,
                                        "Password not updated", Toast.LENGTH_SHORT).show();
                            }
                        }
                        else {
                            Toast.makeText(ForgotPasswordActivity.this,
                                    "New Password doesn't match with the Confirm Password",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(ForgotPasswordActivity.this, "Invalid username",
                                Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });
    }
}
