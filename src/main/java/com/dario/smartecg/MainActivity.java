package com.dario.smartecg;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import com.dario.smartecg.knn.*;
public class MainActivity extends Activity {
    private String filename = "Dati.txt";

    public File path;

    private Knn knn;

    double heartbeat[] = new double[5];

    private Random r = new Random();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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



}
