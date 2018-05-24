package com.dario.smartecg;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import com.dario.smartecg.knn.Knn;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class HomeActivity extends Activity {
    public File path;

    private Knn knn;
    private String filename = "Dati.txt";

    double heartbeat[] = new double[5];

    private Random r = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        try {
            Knn KNN = new Knn(getAssets().open("Dati.txt"));
            Toast.makeText(getApplicationContext(), "ciao", Toast.LENGTH_LONG).show();

            double heartbeat[] = {800.900000, 800.800000, 800.400000, 800.000000, 800.7};

            if (KNN.prediction(heartbeat) == 0) {
                Toast.makeText(getApplicationContext(), "No Fibrillation", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Fibrillation", Toast.LENGTH_LONG).show();

            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }
    @Override
    public void onBackPressed() {
            finish();


    }
}
