package com.example.bluetooth_advanced.config;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.content.ContextCompat;


public class Utils {

    public static BluetoothAdapter getBluetoothAdapter(Context context) {
        BluetoothManager mBluetoothManager = (BluetoothManager) context.getSystemService(context.BLUETOOTH_SERVICE);
        return mBluetoothManager.getAdapter();
    }

    public static boolean checkBluetoothPermission(Context context) {
        return (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ||
                (ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED);
    }

}
