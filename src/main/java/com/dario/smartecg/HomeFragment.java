package com.dario.smartecg;


import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.dario.smartecg.knn.Knn;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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

import android.support.v4.app.Fragment;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.dario.smartecg.ScanActivity.BLE_DEVICE;
import static com.dario.smartecg.ScanActivity.BLE_DEVICE_DISCONNECTED;

public class HomeFragment extends Fragment implements View.OnClickListener, ServiceConnection, HeartbeatService.OnHeartbeatListener {
    private final static String LOG_TAG = HomeActivity.class.getSimpleName();

    Button connectButton;
    Button startButton;

    TextView textView;

    private static final int OPEN_SCAN_ACTIVITY = 1;

    private HeartbeatService service;

    private boolean isBound;

    private BleDevice bleDevice;

    private Knn KNN;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        connectButton = (Button) rootView.findViewById(R.id.connect_button);
        startButton = (Button) rootView.findViewById(R.id.start_button);
        textView = (TextView) rootView.findViewById(R.id.text_view);

        connectButton.setOnClickListener(this);
        startButton.setOnClickListener(this);

        setupKNN();
        return rootView;
    }


    private void setupKNN() {
        new Thread(() -> {
            try {
                if (KNN == null) {
                    KNN = new Knn(getActivity().getAssets().open("Dati.txt"));
                }

                getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(), "ciao", Toast.LENGTH_LONG).show());

                double heartbeat[] = {800.900000, 800.800000, 800.400000, 800.000000, 800.7};

                if (KNN.prediction(heartbeat) == 0) {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(), "No Fibrillation", Toast.LENGTH_LONG).show());
                } else {
                    getActivity().runOnUiThread(() -> Toast.makeText(getActivity().getApplicationContext(), "Fibrillation", Toast.LENGTH_LONG).show());
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error", e);
            }
        }).start();
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
    public void onResume() {
        super.onResume();
        if (service != null && !isBound) {
            Intent intent = new Intent(getActivity(), HeartbeatService.class);
            isBound = getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);

            Log.v(LOG_TAG, "Bind service");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (service != null && isBound) {
            getActivity().unbindService(this);
            isBound = false;

            Log.v(LOG_TAG, "Unbind service");
        }
    }

    @Override
    public void onDestroy() {
        bleDevice = null;
        BleManager.getInstance().disconnectAllDevice();
        BleManager.getInstance().destroy();
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
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
                Toast.makeText(getActivity(), R.string.unable_to_start_service, Toast.LENGTH_SHORT).show();
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
        startActivityForResult(new Intent(getActivity(), ScanActivity.class), OPEN_SCAN_ACTIVITY);
    }

    private boolean startupService() {
        if (service == null && !isBound && bleDevice != null && BleManager.getInstance().isConnected(bleDevice)) {
            Intent intent = new Intent(getActivity(), HeartbeatService.class);
            intent.putExtra(BLE_DEVICE, bleDevice);
            isBound = getActivity().bindService(intent, this, Context.BIND_AUTO_CREATE);
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
            getActivity().unbindService(this);
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
