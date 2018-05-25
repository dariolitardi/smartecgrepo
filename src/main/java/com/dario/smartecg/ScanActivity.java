package com.dario.smartecg;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.util.SortedList;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.util.SortedListAdapterCallback;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.dario.smartecg.adapter.DeviceListAdapter;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class ScanActivity extends AppCompatActivity implements DeviceListAdapter.OnItemClickListener, View.OnClickListener {

    private final static String LOG_TAG = ScanActivity.class.getSimpleName();

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.scan_button)
    Button scanButton;
    @BindView(R.id.scan_info)
    TextView scanInfo;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    private final static String DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private static final int REQUEST_ENABLE_BT = 101;

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    private static final long SCAN_PERIOD = 10000;

    private Unbinder unbinder;

    private Handler handler;

    private BluetoothAdapter bluetoothAdapter;

    private DeviceListAdapter adapter;

    private SortedList<BluetoothDevice> items;

    private boolean scanRunning = false;

    private boolean scanAfterBluetoothEnabled = false;

    private BluetoothDevice device;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        unbinder = ButterKnife.bind(this);
        handler = new Handler();

        setupToolbar();
        setupAdapter();
        setupScanButton();
        setupRecyclerView();

        if (!isLocationPermissionGranted()) {
            askLocationPermission();
        } else {
            if (isBluetoothDisabled()) {
                enableBluetooth(true);
            } else {
                startScanning();
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) unbinder.unbind();
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

    private void setupToolbar() {
        toolbar.setTitle("Device scanner");
        toolbar.setTitleTextColor(Color.WHITE);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupAdapter() {
        adapter = new DeviceListAdapter(this);
        adapter.setOnItemClickListener(this);
        adapter.setItems(items = new SortedList<>(BluetoothDevice.class, new SortedListAdapterCallback<BluetoothDevice>(adapter) {
            @Override
            public int compare(BluetoothDevice n1, BluetoothDevice n2) {
                return n1.getName().compareToIgnoreCase(n2.getName());
            }

            @Override
            public boolean areContentsTheSame(BluetoothDevice oldItem, BluetoothDevice newItem) {
                return oldItem.getName().equals(newItem.getName()) && oldItem.getAddress().equals(newItem.getAddress());
            }

            @Override
            public boolean areItemsTheSame(BluetoothDevice item1, BluetoothDevice item2) {
                return item1.getName().equals(item2.getName()) && item1.getAddress().equals(item2.getAddress());
            }
        }));
    }

    private void setupRecyclerView() {
        recyclerView.setAdapter(adapter);
        recyclerView.setLongClickable(false);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void setupScanButton() {
        scanButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (!scanRunning) {
            if (isBluetoothDisabled()) {
                enableBluetooth(true);
            } else {
                startScanning();
            }
        } else {
            stopScanning();
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        if (isBluetoothDisabled()) {
            enableBluetooth(false);
            return;
        }

        device = items.get(position);

        if (device.getName() != null && device.getName().contains("BlueNRG")) {
            stopScanning();
            finish();
        }
    }

    private final BluetoothAdapter.LeScanCallback bluetoothLeScanCallback = new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            runOnUiThread(() -> items.add(device));
        }
    };

    public void startScanning() {
        if (scanRunning) return;

        Log.i(LOG_TAG, "Start scanning");

        items.clear();

        scanRunning = true;

        scanButton.setText(R.string.stop_scanning);
        scanInfo.setText(R.string.scan_running);
        scanInfo.setVisibility(View.VISIBLE);

        AsyncTask.execute(() -> {
            handler.postDelayed(this::stopScanning, SCAN_PERIOD);
            bluetoothAdapter.startLeScan(bluetoothLeScanCallback);
        });
    }

    public void stopScanning() {
        if (!scanRunning) return;

        Log.i(LOG_TAG, "Stopping scanning");

        scanRunning = false;

        if (scanButton != null) {
            scanButton.setText(R.string.scan_devices);
        }
        if (scanInfo != null) {
            scanInfo.setText(getResources().getQuantityString(R.plurals.devices_found, items.size(), items.size()));
        }

        AsyncTask.execute(() -> bluetoothAdapter.stopLeScan(bluetoothLeScanCallback));
    }

    private boolean isBluetoothDisabled() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);

        if (bluetoothManager == null || bluetoothManager.getAdapter() == null) {
            Toast.makeText(this, R.string.error_bluetooth_not_supported, Toast.LENGTH_SHORT).show();
            finish();
            return true;
        }

        bluetoothAdapter = bluetoothManager.getAdapter();

        return bluetoothAdapter == null || !bluetoothAdapter.isEnabled();
    }

    private void enableBluetooth(boolean startScanning) {
        if (!bluetoothAdapter.isEnabled()) {
            scanAfterBluetoothEnabled = startScanning;
            startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), REQUEST_ENABLE_BT);
        }
    }

    private boolean isLocationPermissionGranted() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.M || checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void askLocationPermission() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("This app needs location access");
        builder.setMessage("Please grant location access so this app can detect peripherals.");
        builder.setPositiveButton(android.R.string.ok, null);
        builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION));
        builder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == -1) {//enabled
                if (scanAfterBluetoothEnabled) {
                    startScanning();
                }
            } else {//not enabled

            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("coarse location permission granted");
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

    @Override
    public void finish() {
        if (device == null) {
            setResult(Activity.RESULT_CANCELED);
        } else {
            Intent returnIntent = new Intent();
            returnIntent.putExtra(DEVICE_ADDRESS, device.getAddress());
            setResult(Activity.RESULT_OK, returnIntent);
        }

        super.finish();
    }
}
