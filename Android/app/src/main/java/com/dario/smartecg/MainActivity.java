package com.dario.smartecg;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private static final int NEW_ACTIVITY_ON_TOP = Intent.FLAG_ACTIVITY_SINGLE_TOP
            | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK;

    private ImageButton goButton;
    private EditText name;
    private EditText age;
    private String username;
    private String genderUser;
    private String ageUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (UserSession.isActiveSession(this)) {
            startActivity(new Intent(this,
                    HomeActivity.class));
            finish();
            return;
        }
        setContentView(R.layout.activity_main);
        goButton = (ImageButton) findViewById(R.id.goButton);
        name = (EditText) findViewById(R.id.name);
        age = (EditText) findViewById(R.id.age);

        // Spinner element
        Spinner spinner = (Spinner) findViewById(R.id.gender);

        // Spinner click listener

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Gender");
        categories.add("Male");
        categories.add("Female");


        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);


        AdapterView.OnItemSelectedListener OnCatSpinnerCL = new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if (pos == 0)
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
                else
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        };
        spinner.setOnItemSelectedListener(OnCatSpinnerCL);

        goButton.setOnClickListener(v -> {
            username = name.getText().toString();
            ageUser = age.getText().toString();
            genderUser = spinner.getSelectedItem().toString();
            if (username.isEmpty() || username.length() <= 2) {
                Toast.makeText(getApplicationContext(), "Invalid name", Toast.LENGTH_LONG).show();
                return;
            } else if (genderUser.equals("Gender")) {
                Toast.makeText(getApplicationContext(), "Gender not selected", Toast.LENGTH_LONG).show();
                return;

            } else if (ageUser.isEmpty() || ageUser.equals("0")) {
                Toast.makeText(getApplicationContext(), "Age not valid", Toast.LENGTH_LONG).show();
                return;

            }
            UserSession.setSession(getApplicationContext(), username, ageUser, genderUser);
            Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

            startActivity(intent.addFlags(NEW_ACTIVITY_ON_TOP));
        });

    }

}
