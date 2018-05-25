package com.dario.smartecg;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import android.os.Bundle;
import android.app.Activity;
import android.widget.Toast;

import com.dario.smartecg.knn.Knn;

import java.io.File;
import java.io.IOException;
import java.util.Random;

import static com.dario.smartecg.BluetoothLeService.EXTRA_DATA;

public class HomeActivity extends Activity implements View.OnClickListener {

    private final static String LOG_TAG = HomeActivity.class.getSimpleName();

    @BindView(R.id.connect_button)
    Button connectButton;

    private Unbinder unbinder;

    private static final int SCAN_ACTIVITY = 103;

    public final static int DEVICE_DISCONNECTED = 202;

    private final static String LIST_NAME = "NAME";

    private final static String LIST_UUID = "UUID";

    private final static String DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private BluetoothLeService bluetoothLeService;

    private Intent gattServiceIntent;

    private ArrayList<ArrayList<BluetoothGattCharacteristic>> gattCharacteristics = new ArrayList<>();

    private int[][] characteristics = new int[1][2];

    private boolean isBound;

    private String deviceAddress;

    private Knn KNN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        unbinder = ButterKnife.bind(this);

        connectButton.setOnClickListener(this);

        setupKNN();
    }

    private void setupKNN() {
        new Thread(() -> {
            try {
                if (KNN == null) {
                    KNN = new Knn(getAssets().open("Dati.txt"));
                }

                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "ciao", Toast.LENGTH_LONG).show());

                double heartbeat[] = {800.900000, 800.800000, 800.400000, 800.000000, 800.7};

                if (KNN.prediction(heartbeat) == 0) {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "No Fibrillation", Toast.LENGTH_LONG).show());
                } else {
                    runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Fibrillation", Toast.LENGTH_LONG).show());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        final Intent intent = new Intent(this, ScanActivity.class);
        startActivityForResult(intent, SCAN_ACTIVITY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SCAN_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                deviceAddress = data.getStringExtra(DEVICE_ADDRESS);
                setupService();
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void setupService() {
        gattServiceIntent = new Intent(this, BluetoothLeService.class);
        isBound = bindService(gattServiceIntent, serviceConnection, BIND_AUTO_CREATE);
        registerService();
    }

    private void registerService() {
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bluetoothLeService != null) {
            boolean result = bluetoothLeService.connect(deviceAddress);

            System.out.println("Connect request result=" + result);
        }

        new Thread(() -> {
            try {
                while (gattCharacteristics == null || gattCharacteristics.size() == 0) {
                    Thread.sleep(2000);
                    System.out.println("Waiting for data");
                }
                while (true) {
                    BluetoothGattCharacteristic characteristic1 = gattCharacteristics.get(characteristics[0][0]).get(characteristics[0][1]);
                    bluetoothLeService.readCharacteristic(characteristic1);
                    Thread.sleep(100);
                }
            } catch (Exception e) {
                e.getLocalizedMessage();
            }
        }).start();
    }

    private void displayGattServices(List<BluetoothGattService> gattServices) {
        if (gattServices == null) return;

        String uuid;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData = new ArrayList<>();
        gattCharacteristics = new ArrayList<>();

        for (BluetoothGattService gattService : gattServices) {

            HashMap<String, String> currentServiceData = new HashMap<>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(LIST_NAME, unknownServiceString);
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData = new ArrayList<>();
            List<BluetoothGattCharacteristic> gattCharacteristics = gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas = new ArrayList<>();

            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(LIST_NAME, unknownCharaString);
                currentCharaData.put(LIST_UUID, uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            this.gattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);
        }

        int i = 0, j;
        for (ArrayList<BluetoothGattCharacteristic> service : gattCharacteristics) {
            j = 0;

            for (BluetoothGattCharacteristic gatt : service) {
                UUID uid = gatt.getUuid();
                if (BluetoothLeService.UUID_HUMIDITY.equals(uid)) {
                    characteristics[0][0] = i;
                    characteristics[0][1] = j;
                }
                j++;
            }
            i++;
        }
    }

    public void disconnected() {
        bluetoothLeService.close();

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Warning");
        builder.setMessage("This bluetooth device has been disconnected");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.show();
    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action == null) return;

            switch (action) {
                case BluetoothLeService.ACTION_GATT_CONNECTED:
                    break;
                case BluetoothLeService.ACTION_GATT_DISCONNECTED:
                    disconnected();
                    break;
                case BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED:
                    displayGattServices(bluetoothLeService.getSupportedGattServices());
                    break;
                case BluetoothLeService.ACTION_DATA_AVAILABLE:
                    int IBI = intent.getIntExtra(EXTRA_DATA, 0);
                    if (IBI != previousIBI) {
                        previousIBI = IBI;
                        n++;
                        Log.i(LOG_TAG, n + ") Heartbeat detected: " + IBI);
                    }
                    break;
            }
        }
    };

    private int n = 0;
    private int previousIBI = 0;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            bluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!bluetoothLeService.initialize()) {
                Log.i(LOG_TAG, "Unable to initialize Bluetooth");
                finish();
            }
            bluetoothLeService.connect(deviceAddress);
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            bluetoothLeService = null;
        }
    };

    @Override
    public void onBackPressed() {
        finish();
    }
}
