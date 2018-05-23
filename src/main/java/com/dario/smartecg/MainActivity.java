package com.dario.smartecg;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.dario.smartecg.knn.Knn;

import java.io.IOException;

public class MainActivity extends Activity {

    private Knn KNN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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