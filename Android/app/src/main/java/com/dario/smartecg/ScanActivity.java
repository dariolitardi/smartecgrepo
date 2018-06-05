package com.dario.smartecg;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleGattCallback;
import com.clj.fastble.callback.BleScanCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.scan.BleScanRuleConfig;
import com.dario.smartecg.adapter.DeviceListAdapter;
import com.dario.smartecg.rx.ObserverManager;

import java.util.List;

public class ScanActivity extends AppCompatActivity {

    public static final String BLE_DEVICE = "BLE_DEVICE";
    public static final String BLE_DEVICE_DISCONNECTED = "BLE_DEVICE_DISCONNECTED";
    private static final String LOG_TAG = ScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_ENABLE_BLUETOOTH = 1;
    private static final int REQUEST_CODE_PERMISSION_LOCATION = 2;
    private static final long SCAN_PERIOD = 10000;
    private Toolbar toolbar;
    private Button scanButton;
    private ProgressBar progressBar;
    private RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    private DeviceListAdapter adapter;

    private BleDevice bleDevice;

    private boolean deviceDisconnected;

    private boolean finished = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        toolbar = findViewById(R.id.toolbar);
        scanButton = findViewById(R.id.scan_button);
        progressBar = findViewById(R.id.progress_bar);
        recyclerView = findViewById(R.id.recycler_view);

        setupToolbar();
        setupAdapter();
        setupRecyclerView();
        setupProgressDialog();
        setupScanButton();

        BleManager.getInstance().init(getApplication());
        BleManager.getInstance()
                .enableLog(true)
                .setReConnectCount(1, 5000)
                .setConnectOverTime(20000)
                .setOperateTimeout(5000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        showConnectedDevice();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra(BLE_DEVICE_DISCONNECTED, deviceDisconnected);

        if (bleDevice == null) {
            setResult(Activity.RESULT_CANCELED, returnIntent);
        } else {
            returnIntent.putExtra(BLE_DEVICE, bleDevice);
            setResult(Activity.RESULT_OK, returnIntent);
        }

        finished = true;

        super.finish();
    }

    private void setupToolbar() {
        toolbar.setTitle("Device scanner");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    private void setupAdapter() {
        adapter = new DeviceListAdapter(this);
        adapter.setOnDeviceClickListener(new DeviceListAdapter.OnDeviceClickListener() {
            @Override
            public void onConnect(BleDevice bleDevice) {
                if (!BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().cancelScan();
                    connect(bleDevice);
                }
            }

            @Override
            public void onDisconnect(final BleDevice bleDevice) {
                if (BleManager.getInstance().isConnected(bleDevice)) {
                    BleManager.getInstance().disconnect(bleDevice);
                    adapter.removeDevice(bleDevice);
                    deviceDisconnected = true;
                }
            }
        });
    }

    private void setupRecyclerView() {
        recyclerView.setAdapter(adapter);
        recyclerView.setLongClickable(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
    }

    private void setupProgressDialog() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.please_wait));
        progressDialog.setCanceledOnTouchOutside(false);
    }

    private void setupScanButton() {
        scanButton.setOnClickListener(v -> {
            switch (v.getId()) {
                case R.id.scan_button:
                    if (scanButton.getText().equals(getString(R.string.start_scan))) {
                        if (!isLocationPermissionGranted()) {
                            askLocationPermission();
                        } else {
                            if (!isBluetoothEnabled()) {
                                enableBluetooth();
                            } else {
                                setScanRule();
                                startScan();
                            }
                        }

                    } else if (scanButton.getText().equals(getString(R.string.stop_scan))) {
                        BleManager.getInstance().cancelScan();
                    }
                    break;
            }
        });
    }

    private void showConnectedDevice() {
        List<BleDevice> deviceList = BleManager.getInstance().getAllConnectedDevice();
        adapter.replaceItems(deviceList);
    }

    private void setScanRule() {
        BleManager.getInstance().initScanRule(new BleScanRuleConfig.Builder()
                .setScanTimeOut(SCAN_PERIOD)
                .build());
    }

    private void startScan() {
        BleManager.getInstance().scan(new BleScanCallback() {
            @Override
            public void onScanStarted(boolean success) {
                adapter.removeAllDevices();
                progressBar.setVisibility(View.VISIBLE);
                scanButton.setText(getString(R.string.stop_scan));
            }

            @Override
            public void onLeScan(BleDevice bleDevice) {
                super.onLeScan(bleDevice);
            }

            @Override
            public void onScanning(BleDevice bleDevice) {
                adapter.addDevice(bleDevice);
            }

            @Override
            public void onScanFinished(List<BleDevice> scanResultList) {
                if (finished) {
                    return;
                }

                progressBar.setVisibility(View.INVISIBLE);
                scanButton.setText(getString(R.string.start_scan));
            }
        });
    }

    private void connect(final BleDevice bleDevice) {
        BleManager.getInstance().connect(bleDevice, new BleGattCallback() {
            @Override
            public void onStartConnect() {
                if (finished) {
                    return;
                }

                progressDialog.show();
            }

            @Override
            public void onConnectFail(BleDevice bleDevice, BleException exception) {
                if (finished) {
                    return;
                }

                progressBar.setVisibility(View.INVISIBLE);
                progressDialog.dismiss();

                scanButton.setText(getString(R.string.start_scan));

                Toast.makeText(ScanActivity.this, getString(R.string.connect_fail), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onConnectSuccess(BleDevice bleDevice, BluetoothGatt gatt, int status) {
                if (finished) {
                    return;
                }

                progressDialog.dismiss();
                adapter.updateDevice(bleDevice);
                ScanActivity.this.bleDevice = bleDevice;
            }

            @Override
            public void onDisConnected(boolean isActiveDisConnected, BleDevice bleDevice, BluetoothGatt gatt, int status) {
                if (finished) {
                    return;
                }

                ScanActivity.this.bleDevice = null;

                progressDialog.dismiss();

                if (!isActiveDisConnected) {
                    Toast.makeText(ScanActivity.this, getString(R.string.disconnected), Toast.LENGTH_LONG).show();
                    ObserverManager.getInstance().notifyObserver(bleDevice);
                }

            }
        });
    }

    @SuppressLint("ObsoleteSdkInt")
    private boolean isLocationPermissionGranted() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askLocationPermission() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("This app needs location access");
        builder.setMessage("Please grant location access so this app can detect peripherals.");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE_PERMISSION_LOCATION));
        builder.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v(LOG_TAG, "Coarse location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Warning");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover bluetooth devices.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                }
            }
        }
    }

    private boolean isBluetoothEnabled() {
        return BluetoothAdapter.getDefaultAdapter().isEnabled();
    }

    private void enableBluetooth() {
        if (!BluetoothAdapter.getDefaultAdapter().isEnabled()) {
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_CODE_ENABLE_BLUETOOTH);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_ENABLE_BLUETOOTH) {
            if (resultCode == -1) {//bluetooth has been enabled
                setScanRule();
                startScan();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
