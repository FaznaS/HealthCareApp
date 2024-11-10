package com.s22010466.healthcare;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;
import android.widget.Toast;

public class PreferencesFragment extends Fragment {
    RadioGroup radioGroup;
    RadioButton radioBtnDefaultTheme, radioBtnLightTheme, radioBtnDarkTheme;
    String theme;
    Switch switchDefault,switchCool,switchChaos;
    Button btnApplyChanges;
    SharedPreferences sharedPreferences;

    public PreferencesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_preferences, container, false);

        radioGroup = view.findViewById(R.id.radioGroup);
        radioBtnDefaultTheme = view.findViewById(R.id.radioBtnDefaultTheme);
        radioBtnLightTheme = view.findViewById(R.id.radioBtnLightTheme);
        radioBtnDarkTheme = view.findViewById(R.id.radioBtnDarkTheme);
        switchDefault = view.findViewById(R.id.switchDefault);
        switchCool = view.findViewById(R.id.switchCool);
        switchChaos = view.findViewById(R.id.switchChaos);
        btnApplyChanges = view.findViewById(R.id.btnApplyChanges);

        // Setting the Theme
        sharedPreferences = requireContext().getSharedPreferences("MODE", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        theme = sharedPreferences.getString("selectedTheme","default");

        if (theme.equals("default")) {
            radioBtnDefaultTheme.setChecked(true);
        } else if (theme.equals("night")) {
            radioBtnDarkTheme.setChecked(true);
        } else {
            radioBtnLightTheme.setChecked(true);
        }

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.radioBtnLightTheme) {
                    theme = "light";
                }
                else if (checkedId == R.id.radioBtnDarkTheme){
                    theme = "night";
                } else {
                    theme = "default";
                }
            }
        });

        // Restore the state of the switches
        boolean defaultSound = sharedPreferences.getBoolean("defaultSound", true);
        boolean coolSound = sharedPreferences.getBoolean("coolSound", false);
        boolean chaosSound = sharedPreferences.getBoolean("chaosSound", false);

        switchDefault.setChecked(defaultSound);
        switchCool.setChecked(coolSound);
        switchChaos.setChecked(chaosSound);

        switchDefault.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferences.edit().putBoolean("defaultSound", true).apply();
                    sharedPreferences.edit().putBoolean("coolSound", false).apply();
                    sharedPreferences.edit().putBoolean("chaosSound", false).apply();
                    switchCool.setChecked(false);
                    switchChaos.setChecked(false);
                }
            }
        });

        switchCool.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferences.edit().putBoolean("defaultSound", false).apply();
                    sharedPreferences.edit().putBoolean("coolSound", true).apply();
                    sharedPreferences.edit().putBoolean("chaosSound", false).apply();
                    switchDefault.setChecked(false);
                    switchChaos.setChecked(false);
                }
            }
        });

        switchChaos.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sharedPreferences.edit().putBoolean("defaultSound", false).apply();
                    sharedPreferences.edit().putBoolean("coolSound", false).apply();
                    sharedPreferences.edit().putBoolean("chaosSound", true).apply();
                    switchDefault.setChecked(false);
                    switchCool.setChecked(false);
                }
            }
        });

        btnApplyChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putString("selectedTheme",theme);
                editor.apply();
                applyTheme(theme);
                Toast.makeText(requireContext(),"Changes Saved",Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void applyTheme(String theme) {
        if (theme.equals("default")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else if (theme.equals("night")) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }

}