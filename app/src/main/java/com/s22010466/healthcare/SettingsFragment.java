package com.s22010466.healthcare;

import static android.content.Context.MODE_PRIVATE;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.picasso.Picasso;

import java.io.IOException;


public class SettingsFragment extends Fragment {
    DatabaseHelper hCareDb;
    TextView textUsername;
    EditText editHeight,editWeight,editNewPassword,editConfirmPassword;
    Button btnUpdateSettings;
    ImageView profilePicture;
    FloatingActionButton cameraBtn;
    Bitmap selectedImageBitmap;
    Uri selectedImageUri;
    private static final int MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES = 1;

    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        hCareDb = new DatabaseHelper(getActivity());

        textUsername = view.findViewById(R.id.textViewSettingsUsername);
        editHeight = view.findViewById(R.id.editTextSettingsHeight);
        editWeight = view.findViewById(R.id.editTextSettingsWeight);
        editNewPassword = view.findViewById(R.id.editTextSettingsNewPw);
        editConfirmPassword = view.findViewById(R.id.editTextSettingsConfirmPw);
        btnUpdateSettings = view.findViewById(R.id.btnUpdate);
        profilePicture = view.findViewById(R.id.imageViewProfilePicture);
        cameraBtn = view.findViewById(R.id.floatingActionButton);

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            // Request Permission if not granted
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                    MY_PERMISSIONS_REQUEST_READ_MEDIA_IMAGES);
        }

        // Retrieving data from SharedPreferences to set details in settings
        SharedPreferences preferences = getActivity().getSharedPreferences("UserPreferences",
                MODE_PRIVATE);
        String username = preferences.getString("username", "");
        String height = preferences.getString("height","");
        String weight = preferences.getString("weight","");
        String savedImageUri = preferences.getString("profile_picture_uri","");
        Log.d("SettingsFragment", "savedImageUri: " + savedImageUri);

        textUsername.setText(username);
        editHeight.setText(height);
        editWeight.setText(weight);

        if (!savedImageUri.isEmpty()) {
            selectedImageUri = Uri.parse(savedImageUri);
            // Loading the image
            Picasso.get().load(selectedImageUri).error(R.drawable.baseline_person_24).into(profilePicture);
        } else {
            profilePicture.setImageResource(R.drawable.baseline_person_24);
        }

        updateProfile(editHeight.getText().toString(),editWeight.getText().toString());

        //Uploading Profile Picture
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectProfilePicture();
            }
        });

        return view;
    }

    public void selectProfilePicture() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        launchSomeActivity.launch(gallery);
    }

    ActivityResultLauncher<Intent> launchSomeActivity = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == Activity.RESULT_OK) {
                    Intent data = result.getData();

                    if(data != null && data.getData() != null) {
                        selectedImageUri = data.getData();
                        Picasso.get().load(selectedImageUri).into(profilePicture);

                        try {
                            selectedImageBitmap = MediaStore.Images.Media.getBitmap(
                                    this.getActivity().getContentResolver(), selectedImageUri);
                        }
                        catch (IOException e) {
                            e.printStackTrace();
                        }
                        profilePicture.setImageBitmap(selectedImageBitmap);
                    }
                }
            }
    );

    public void updateProfile(String currentHeight, String currentWeight) {
        btnUpdateSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = textUsername.getText().toString();
                String new_height = editHeight.getText().toString();
                String new_weight = editWeight.getText().toString();
                String newPassword = editNewPassword.getText().toString();
                String confirmPassword = editConfirmPassword.getText().toString();
                //Tells if there is any change that has to be done
                boolean isUpdateNeeded = false;

                if (selectedImageUri != null) {
                    Log.d("SettingsFragment", "selectedImageUri: " + selectedImageUri);
                    // Save the selected image URI to SharedPreferences
                    SharedPreferences preferences = getActivity().getSharedPreferences("UserPreferences", MODE_PRIVATE);
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("profile_picture_uri", selectedImageUri.toString());
                    editor.apply();
                    isUpdateNeeded = true;
                }

                //Updating height and weight if changed
                if(!new_height.equals(currentHeight) || !new_weight.equals(currentWeight)) {
                    boolean isHeightWeightUpdated = hCareDb.updateHeightAndWeight(username,new_height,new_weight);

                    if(isHeightWeightUpdated) {
                        if (isHeightWeightUpdated) {
                            SharedPreferences preferences = getActivity().
                                    getSharedPreferences("UserPreferences", MODE_PRIVATE);
                            SharedPreferences.Editor editor = preferences.edit();
                            editor.putString("height", new_height);
                            editor.putString("weight", new_weight);
                            editor.commit();
                            isUpdateNeeded = true;
                        }
                    }
                }

                //Updating Password
                if(!newPassword.isEmpty() && !confirmPassword.isEmpty() && newPassword.equals(confirmPassword)) {
                    boolean isPasswordUpdated = hCareDb.updatePassword(username,confirmPassword);

                    if(isPasswordUpdated) {
                        if (isPasswordUpdated) {
                            isUpdateNeeded = true;
                        }
                    }
                }

                if (isUpdateNeeded) {
                    Toast.makeText(requireContext(), "Settings Updated Successfully",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
