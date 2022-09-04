package com.example.bluetooth_advanced.receivers;

import static com.example.bluetooth_advanced.config.Constants.ACTION_DATA_AVAILABLE;
import static com.example.bluetooth_advanced.config.Constants.ACTION_GATT_CONNECTED;
import static com.example.bluetooth_advanced.config.Constants.ACTION_GATT_DISCONNECTED;
import static com.example.bluetooth_advanced.config.Constants.ACTION_GATT_INITIATED;
import static com.example.bluetooth_advanced.config.Constants.ACTION_GATT_SERVICES_DISCOVERED;
import static com.example.bluetooth_advanced.config.Constants.EXTRA_DATA;
import static com.example.bluetooth_advanced.config.Messages.DEVICE_GATT_AVAILABLE;
import static com.example.bluetooth_advanced.config.Messages.DEVICE_GATT_CONNECTED;
import static com.example.bluetooth_advanced.config.Messages.DEVICE_GATT_CONNECTING;
import static com.example.bluetooth_advanced.config.Messages.DEVICE_GATT_DISCONNECTED;
import static com.example.bluetooth_advanced.config.Messages.DEVICE_GATT_INITIATED;

import android.bluetooth.BluetoothGattCharacteristic;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.bluetooth_advanced.listeners.GattUpdateReceiverListener;

import io.flutter.Log;

public class GattUpdateReceiver extends BroadcastReceiver {

    private String TAG = "UPDATE: ";
    GattUpdateReceiverListener callback;
    BluetoothGattCharacteristic mNotifyCharacteristic;


    public void setCallback(GattUpdateReceiverListener callback, BluetoothGattCharacteristic mNotifyCharacteristic) {
        this.callback = callback;
        this.mNotifyCharacteristic = mNotifyCharacteristic;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();

        if (ACTION_GATT_INITIATED.equals(action)) {
            Log.d(TAG, DEVICE_GATT_INITIATED);
            this.callback.onGattUpdateReceiverChange(DEVICE_GATT_INITIATED);
        } else if (ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
            Log.d(TAG, DEVICE_GATT_CONNECTING);
            this.callback.onGattUpdateReceiverChange(DEVICE_GATT_CONNECTING);
        } else if (ACTION_GATT_CONNECTED.equals(action)) {
            Log.d(TAG, DEVICE_GATT_CONNECTED);
            this.callback.onGattUpdateReceiverChange(DEVICE_GATT_CONNECTED);
        } else if (ACTION_DATA_AVAILABLE.equals(action)) {
            this.callback.onGattUpdateReceiverChange(DEVICE_GATT_AVAILABLE + ":" + intent.getStringExtra(EXTRA_DATA));
        } else if (ACTION_GATT_DISCONNECTED.equals(action)) {
            Log.d(TAG, DEVICE_GATT_DISCONNECTED);
            this.callback.onGattUpdateReceiverChange(DEVICE_GATT_DISCONNECTED);
        } else {
            Log.d(TAG, "ACTION_UNKNOWN");
        }
    }

    public static IntentFilter GattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_GATT_CONNECTED);
        intentFilter.addAction(ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

}
