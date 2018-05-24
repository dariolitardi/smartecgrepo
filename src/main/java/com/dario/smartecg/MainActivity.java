package com.dario.smartecg;

import android.app.Activity;
<<<<<<< HEAD
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
=======
import android.os.Bundle;
>>>>>>> 837250f231d5d15a55f74110728dc6e31532f1ce
import android.widget.Toast;

import com.dario.smartecg.knn.Knn;

import java.io.IOException;
<<<<<<< HEAD
import java.util.List;
import java.util.Objects;
import java.util.Random;
=======
>>>>>>> 837250f231d5d15a55f74110728dc6e31532f1ce

public class MainActivity extends Activity {
<<<<<<< HEAD
    private static final int NEW_ACTIVITY_ON_TOP = Intent.FLAG_ACTIVITY_SINGLE_TOP
            | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK;

private ImageButton goButton;
    private EditText name;
=======

    private Knn KNN;
>>>>>>> 837250f231d5d15a55f74110728dc6e31532f1ce

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
<<<<<<< HEAD
goButton=(ImageButton) findViewById(R.id.goButton);
        name=(EditText)findViewById(R.id.name);

goButton.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent= new Intent(getApplicationContext(), HomeActivity.class);
        intent.putExtra("name", name.getText());

        startActivity(intent.addFlags(NEW_ACTIVITY_ON_TOP));
    }
});




    }

    @Override
    public void onBackPressed() {
    }


}
=======
        try {
            if (KNN == null) {
                KNN = new Knn(getAssets().open("Dati.txt"));
            }

            Toast.makeText(getApplicationContext(), "ciao", Toast.LENGTH_LONG).show();

            double heartbeat[] = {800.900000, 800.800000, 800.400000, 800.000000, 800.7};

            if (KNN.prediction(heartbeat) == 0) {
                Toast.makeText(getApplicationContext(), "No Fibrillation", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Fibrillation", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
>>>>>>> 837250f231d5d15a55f74110728dc6e31532f1ce
