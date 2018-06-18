package com.dario.smartecg;


import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.data.BleDevice;

import static com.dario.smartecg.ScanActivity.BLE_DEVICE;
import static com.dario.smartecg.ScanActivity.BLE_DEVICE_DISCONNECTED;

public class HomeFragment extends Fragment implements View.OnClickListener, ServiceConnection, HeartbeatService.OnHeartbeatListener {
    private final static String LOG_TAG = HomeActivity.class.getSimpleName();
    private static final int OPEN_SCAN_ACTIVITY = 1;

    TextView textView;
    TextView textViewBPM;
    ImageView heartbeatImage;
    ImageButton startButton;

    private HeartbeatService service;
    private boolean isBound;
    private BleDevice bleDevice;
    private boolean started = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        textView = rootView.findViewById(R.id.text_view);
        textViewBPM = rootView.findViewById(R.id.text_view_bpm);
        heartbeatImage = rootView.findViewById(R.id.heartbeat_image);

        startButton = rootView.findViewById(R.id.start_button);
        startButton.setOnClickListener(this);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_connect:
                openScanActivity();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_button:
                if (!started) {
                    startHeartbeatService();
                } else {
                    stopHeartbeatService(false);
                }
                break;
        }
    }

    private void startHeartbeatService() {
        if (startupService()) {
            startButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.stopimage));

            Toast.makeText(getActivity(), "Session started", Toast.LENGTH_SHORT).show();
            started = true;
        } else {
            Toast.makeText(getActivity(), "You are not connected to a bluetooth device", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopHeartbeatService(boolean isDisconnection) {
        shutdownService(isDisconnection);

        started = false;
        startButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.startimage));
        textView.setText("0");

        Toast.makeText(getActivity(), "Session stopped", Toast.LENGTH_SHORT).show();
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

    @Override
    public void heartbeat(short BPM) {
        if (textView != null) {
            textView.setText(String.valueOf(BPM));
        }

        if (heartbeatImage != null) {
            heartbeatImage.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.flash));
        }
    }

    @Override
    public void sensorError() {
        /*if (textView != null) {
            textView.setText("Sensor error");
        }*/
    }

    @Override
    public void deviceDisconnected() {
        stopHeartbeatService(true);
    }
}
