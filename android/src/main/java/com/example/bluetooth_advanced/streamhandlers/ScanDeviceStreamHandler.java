package com.example.bluetooth_advanced.streamhandlers;

import static com.example.bluetooth_advanced.config.Constants.ACTION_LOGGER_UPDATE;
import static com.example.bluetooth_advanced.config.Constants.getScanPeriod;
import static com.example.bluetooth_advanced.config.Messages.PERMISSION_REQUIRED_LOCATION;
import static com.example.bluetooth_advanced.config.Messages.SCANNING_FINISHED_WITH_DEVICE_FOUND;
import static com.example.bluetooth_advanced.config.Messages.SCANNING_FINISHED_WITH_NO_DEVICE;
import static com.example.bluetooth_advanced.config.Messages.SCANNING_REQUIRES_BLUETOOTH_ON;
import static com.example.bluetooth_advanced.config.Messages.SCANNING_STARTED;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanFilter;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;

import com.example.bluetooth_advanced.config.Constants;
import com.example.bluetooth_advanced.config.UUIDs;
import com.example.bluetooth_advanced.config.Utils;
import com.example.bluetooth_advanced.receivers.GattUpdateReceiver;
import com.example.bluetooth_advanced.receivers.LoggerUpdateReceiver;

import java.util.ArrayList;
import java.util.List;

import io.flutter.Log;
import io.flutter.plugin.common.EventChannel;

/*
    Scans devices with the set scan filter and returns one device in the duration of scan period
*/

public class ScanDeviceStreamHandler implements EventChannel.StreamHandler {
    private String TAG = "ScanDeviceStreamHandler";

    private BluetoothAdapter mBluetoothAdapter;
    private Context context;
    private Activity activity;

    public static BluetoothDevice bluetoothDevice;
    private boolean mScanning;
    private BluetoothLeScanner bluetoothLeScanner;
    private EventChannel.EventSink sink;
    private Handler handler;
    private ScanCallback scanCallback;
    private BroadcastReceiver mGattUpdateReceiver;
    private EventChannel loggerEventChannel;
    private LoggerUpdateReceiver mLoggerUpdateReceiver;

    public BluetoothDevice getBluetoothDevice() {
        return bluetoothDevice;
    }

    public void setBluetoothDevice(BluetoothDevice bluetoothDevice) {
        this.bluetoothDevice = bluetoothDevice;
    }

    public ScanDeviceStreamHandler() {
    }

    public ScanDeviceStreamHandler(Context context, Activity activity, BluetoothAdapter mBluetoothAdapter, GattUpdateReceiver mGattUpdateReceiver, EventChannel loggerEventChannel, LoggerUpdateReceiver mLoggerUpdateReceiver) {
        this.mBluetoothAdapter = mBluetoothAdapter;
        this.context = context;
        this.activity = activity;
        this.mGattUpdateReceiver = mGattUpdateReceiver;
        this.mLoggerUpdateReceiver = mLoggerUpdateReceiver;
        this.loggerEventChannel = loggerEventChannel;
    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            mScanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
            if(bluetoothDevice==null){
                startScanning(mScanning);
            }
        }
    };

    private void startScanning(boolean enable) {
        if (enable) {
            Log.d(TAG, "Starting Scan");
            List<ScanFilter> scanFilters = new ArrayList<>();
            final ScanSettings settings = new ScanSettings.Builder().build();
            ScanFilter scanFilter = new ScanFilter.Builder().setServiceUuid(ParcelUuid.fromString(UUIDs.getUuidBluetoothService())).build();
            scanFilters.add(scanFilter);
            handler.postDelayed(runnable, Constants.getScanPeriod());
            mScanning = true;
            bluetoothLeScanner.startScan(scanFilters, settings, scanCallback);
        } else {
            Log.d(TAG, "Stopping Scan");
            mScanning = false;
            bluetoothLeScanner.stopScan(scanCallback);
            if(loggerEventChannel!=null){
                Intent intent = new Intent(ACTION_LOGGER_UPDATE);
                intent.putExtra(ACTION_LOGGER_UPDATE, SCANNING_FINISHED_WITH_NO_DEVICE);
                mLoggerUpdateReceiver.sendABroadCast(intent);
            }
            sink.success(SCANNING_FINISHED_WITH_NO_DEVICE);
        }
    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {

        sink = eventSink;
        handler = new Handler();
        if(mBluetoothAdapter.isEnabled()){
            bluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            if (Utils.checkBluetoothPermission(context)) {
                scanCallback = new ScanCallback() {
                    @Override
                    public void onScanResult(int callbackType, ScanResult result) {
                        super.onScanResult(callbackType, result);
                        setBluetoothDevice(result.getDevice());
                        ScanDeviceStreamHandler.bluetoothDevice = result.getDevice();

                        if(bluetoothDevice!=null){
                            //Log.d(TAG, "Bluetooth device found: "+bluetoothDevice.getAddress()+" , "+bluetoothDevice.getName());
                            sink.success(bluetoothDevice.getAddress()  + "," + bluetoothDevice.getName());
                        }
                        if(loggerEventChannel!=null){
                            Intent intent = new Intent(ACTION_LOGGER_UPDATE);
                            intent.putExtra(ACTION_LOGGER_UPDATE, SCANNING_FINISHED_WITH_DEVICE_FOUND);
                            mLoggerUpdateReceiver.sendABroadCast(intent);
                        }
                    }

                    @Override
                    public void onBatchScanResults(List<ScanResult> results) {
                        super.onBatchScanResults(results);
                    }

                    @Override
                    public void onScanFailed(int errorCode) {

                        Log.d(TAG, "scan failed");
                        super.onScanFailed(errorCode);
                    }

                };
                startScanning(true);
                if(loggerEventChannel!=null){
                    Intent intent = new Intent(ACTION_LOGGER_UPDATE);
                    intent.putExtra(ACTION_LOGGER_UPDATE, SCANNING_STARTED);
                    mLoggerUpdateReceiver.sendABroadCast(intent);
                }
            }else{
                eventSink.error(PERMISSION_REQUIRED_LOCATION,null,null);
            }
        }else{
            eventSink.error(SCANNING_REQUIRES_BLUETOOTH_ON,null,null);
        }

    }

    @Override
    public void onCancel(Object o) {

    }
}
