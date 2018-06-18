package com.dario.smartecg;

import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.clj.fastble.BleManager;
import com.clj.fastble.callback.BleNotifyCallback;
import com.clj.fastble.data.BleDevice;
import com.clj.fastble.exception.BleException;
import com.dario.smartecg.rx.Observer;
import com.dario.smartecg.rx.ObserverManager;

import java.nio.ByteBuffer;
import java.util.Random;

import static android.bluetooth.BluetoothGattCharacteristic.PROPERTY_NOTIFY;
import static com.dario.smartecg.ScanActivity.BLE_DEVICE;
import static java.nio.ByteOrder.LITTLE_ENDIAN;

public class HeartbeatService extends Service implements Observer {

    private static final String LOG_TAG = HeartbeatService.class.getSimpleName();

    private final static String HEARTBEAT_SERVICE = "11111180-cf3a-11e1-9ab4-0002a5d5c51b";
    private final static String BPM_IBI_CHARACTERISTIC = "33333380-cf4b-11e1-ac36-0002a5d5c51b";
    private final IBinder binder = new MyBinder();
    private BluetoothGattCharacteristic characteristic;
    private BleDevice bleDevice;
    private OnHeartbeatListener onHeartbeatListener;

    private boolean setupCompleted = false;

    private short bpm;

    private boolean fibrillation;

    private Random random = new Random();

    public void setOnHeartbeatListener(@NonNull OnHeartbeatListener onHeartbeatListener) {
        this.onHeartbeatListener = onHeartbeatListener;
    }

    @Override
    public IBinder onBind(@NonNull Intent intent) {
        this.bleDevice = intent.getParcelableExtra(BLE_DEVICE);

        return binder;
    }

    public boolean setup() {
        if (setupCompleted) {
            Log.e(LOG_TAG, "The heartbeat service is already setup");
            return setupCompleted;
        }

        ObserverManager.getInstance().addObserver(this);

        characteristic = getCharacteristic(HEARTBEAT_SERVICE, BPM_IBI_CHARACTERISTIC, PROPERTY_NOTIFY);
        if (characteristic == null) {
            Log.e(LOG_TAG, "No characteristic found");
            disConnected(bleDevice);
            return false;
        }

        return setupCompleted = true;
    }

    private BluetoothGattCharacteristic getCharacteristic(String uuid, String chara, int property) {
        BluetoothGatt gatt = BleManager.getInstance().getBluetoothGatt(bleDevice);

        if (gatt == null) {
            return null;
        }

        for (BluetoothGattService service : gatt.getServices()) {
            if (service.getUuid().toString().equals(uuid)) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    if (characteristic.getUuid().toString().equals(chara))
                        if ((characteristic.getProperties() & property) > 0)
                            return characteristic;
                }
            }
        }
        return null;
    }

    public void startHeartbeatNotifications() {
        if (!setupCompleted) {
            Log.e(LOG_TAG, "The heartbeat service is not setup");
            return;
        }

        BleManager.getInstance().notify(
                bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString(),
                new BleNotifyCallback() {
                    @Override
                    public void onNotifySuccess() {
                        Log.e(LOG_TAG, "Notify success");
                    }

                    @Override
                    public void onNotifyFailure(final BleException exception) {
                        Log.e(LOG_TAG, exception.toString());
                    }

                    @Override
                    public void onCharacteristicChanged(byte[] data) {
                        short ERR = ByteBuffer.wrap(data, 0, 2).order(LITTLE_ENDIAN).getShort();

                        if (ERR == 1) {
                            Log.e(LOG_TAG, "Error");
                            sensorError();
                            return;
                        }

                        bpm = ByteBuffer.wrap(data, 2, 2).order(LITTLE_ENDIAN).getShort();//BPM
                        fibrillation = ByteBuffer.wrap(data, 4, 2).order(LITTLE_ENDIAN).getShort() == 1;

                        heartbeatDetected(bpm, fibrillation);
                    }
                });
    }

    public void stopHeartbeatNotifications() {

        if (!setupCompleted) {
            Log.e(LOG_TAG, "The service is not setup");
            return;
        }

        BleManager.getInstance().stopNotify(bleDevice,
                characteristic.getService().getUuid().toString(),
                characteristic.getUuid().toString());

        onHeartbeatListener = null;
    }

    @Override
    public void disConnected(BleDevice device) {
        if (device != null && bleDevice != null && device.getKey().equals(bleDevice.getKey())) {
            BleManager.getInstance().clearCharacterCallback(bleDevice);
            ObserverManager.getInstance().deleteObserver(this);

            deviceDisconnected();
        }
    }

    private void sendNotification() {
        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.logo)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText("Fibrillation detected");

        // Gets an instance of the NotificationManager service//
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // When you issue multiple notifications about the same type of event,
        // it’s best practice for your app to try to update an existing notification
        // with this new information, rather than immediately creating a new notification.
        // If you want to update this notification at a later date, you need to assign it an ID.
        // You can then use this ID whenever you issue a subsequent notification.
        // If the previous notification is still visible, the system will update this existing notification,
        // rather than create a new one. In this example, the notification’s ID is 001//
        int m = random.nextInt(9999 - 1000) + 1000;

        if (notificationManager != null) {
            notificationManager.notify(m, mBuilder.build());
        }
    }

    private void heartbeatDetected(short bpm, boolean fibrillation) {
        Log.i(LOG_TAG, "BPM: " + bpm + " FIBRILLATION:" + fibrillation);

        if (fibrillation) {
            sendNotification();
        }

        if (onHeartbeatListener != null) {
            onHeartbeatListener.heartbeat(bpm);
        }
    }

    private void sensorError() {
        Log.i(LOG_TAG, "Sensor error");

        if (onHeartbeatListener != null) {
            onHeartbeatListener.sensorError();
        }
    }

    private void deviceDisconnected() {
        if (onHeartbeatListener != null) {
            onHeartbeatListener.deviceDisconnected();
        }
    }

    public interface OnHeartbeatListener {
        void heartbeat(short BPM);

        void sensorError();

        void deviceDisconnected();
    }

    public class MyBinder extends Binder {
        HeartbeatService getService() {
            return HeartbeatService.this;
        }
    }
}