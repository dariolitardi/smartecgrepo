package com.dario.smartecg;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;
import com.dario.smartecg.knn.Knn;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.dario.smartecg.ScanActivity.BLE_DEVICE;
import static com.dario.smartecg.ScanActivity.BLE_DEVICE_DISCONNECTED;

public class HomeActivity extends Activity implements View.OnClickListener, ServiceConnection, HeartbeatService.OnHeartbeatListener {

    private final static String LOG_TAG = HomeActivity.class.getSimpleName();

    @BindView(R.id.connect_button)
    Button connectButton;
    @BindView(R.id.start_button)
    Button startButton;
    @BindView(R.id.text_view)
    TextView textView;

    private static final int OPEN_SCAN_ACTIVITY = 1;

    private Unbinder unbinder;

    private HeartbeatService service;

    private boolean isBound;

    private BleDevice bleDevice;

    private Knn KNN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_home);

        unbinder = ButterKnife.bind(this);

        connectButton.setOnClickListener(this);
        startButton.setOnClickListener(this);

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
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_button:
                openScanActivity();
                break;
            case R.id.start_button:
                if (startButton.getText().equals(getString(R.string.start_service))) {
                    startHeartbeatService();
                } else if (startButton.getText().equals(getString(R.string.stop_service))) {
                    stopHeartbeatService(false);
                }
                break;
        }
    }

    private void startHeartbeatService() {
        if (startupService()) {
            startButton.setText(R.string.stop_service);
            textView.setText("Service started");
        }
    }

    private void stopHeartbeatService(boolean isDisconnection) {
        shutdownService(isDisconnection);

        startButton.setText(R.string.start_service);
        textView.setText("Service stopped");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (service != null && !isBound) {
            Intent intent = new Intent(this, HeartbeatService.class);
            isBound = bindService(intent, this, Context.BIND_AUTO_CREATE);

            Log.v(LOG_TAG, "Bind service");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (service != null && isBound) {
            unbindService(this);
            isBound = false;

            Log.v(LOG_TAG, "Unbind service");
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        bleDevice = null;
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == OPEN_SCAN_ACTIVITY) {
            if (data.getBooleanExtra(BLE_DEVICE_DISCONNECTED, false)) {
                stopHeartbeatService(true);
            }
            if (resultCode == Activity.RESULT_OK) {
                bleDevice = data.getParcelableExtra(BLE_DEVICE);
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder binder) {
        HeartbeatService.MyBinder b = (HeartbeatService.MyBinder) binder;

        if (service == null) {
            service = b.getService();
            service.setOnHeartbeatListener(this);

            if (service.setup()) {
                service.startHeartbeatNotifications();
                Log.v(LOG_TAG, "Service started");
            } else {
                Toast.makeText(this, R.string.unable_to_start_service, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        /*if (service != null && isBound) {
            unbindService(this);
            isBound = false;
        }*/
    }

    private void openScanActivity() {
        startActivityForResult(new Intent(this, ScanActivity.class), OPEN_SCAN_ACTIVITY);
    }

    private boolean startupService() {
        if (service == null && !isBound && bleDevice != null && BleManager.getInstance().isConnected(bleDevice)) {
            Intent intent = new Intent(this, HeartbeatService.class);
            intent.putExtra(BLE_DEVICE, bleDevice);
            isBound = bindService(intent, this, Context.BIND_AUTO_CREATE);
            Log.v(LOG_TAG, "Service created");
            return true;
        }
        return false;
    }

    private void shutdownService(boolean isDisconnection) {
        if (service != null) {
            service.stopHeartbeatNotifications();
            service.stopSelf();
            service = null;
            Log.v(LOG_TAG, "Service stopped");
        }

        if (isBound) {
            unbindService(this);
            isBound = false;
        }

        if (isDisconnection) {
            service = null;
            isBound = false;
        }
    }

    int n = 0;

    @Override
    public void heartbeat(short BPM) {
        if (textView != null) {
            textView.setText(String.valueOf(n++) + ") BPM:" + BPM);
        }
    }

    @Override
    public void sensorError() {
        if (textView != null) {
            textView.setText(String.valueOf(n++) + ") Sensor error");
        }
    }

    @Override
    public void deviceDisconnected() {
        stopHeartbeatService(true);
    }
}
