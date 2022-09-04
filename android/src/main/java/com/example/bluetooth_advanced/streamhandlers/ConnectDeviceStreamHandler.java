package com.example.bluetooth_advanced.streamhandlers;

import static com.example.bluetooth_advanced.config.Messages.STATE_CONNECTED;
import static com.example.bluetooth_advanced.config.Messages.STATE_CONNECTING;
import static com.example.bluetooth_advanced.config.Messages.STATE_CONNECTING_FAILED;
import static com.example.bluetooth_advanced.config.Messages.STATE_DISCONNECTED;
import static com.example.bluetooth_advanced.config.Messages.STATE_RECOGNIZING;

import android.app.Activity;
import android.app.ActivityManager;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.IBinder;

import com.example.bluetooth_advanced.services.BluetoothLEService;

import io.flutter.Log;
import io.flutter.plugin.common.EventChannel;


public class ConnectDeviceStreamHandler implements EventChannel.StreamHandler {
    private String TAG = "ConnectDeviceStreamHandler " + "caller: ";
    private EventChannel.EventSink sink;
    private BluetoothDevice bluetoothDevice;
    private Context context;
    private Activity activity;
    public ServiceConnection mServiceConnection;
    private BluetoothLEService mBluetoothLEService;
    private String caller;
    public static Intent forService;

    public ConnectDeviceStreamHandler(Context context, Activity activity, BluetoothLEService bluetoothLEService, String caller) {
        this.context = context;
        this.activity = activity;
        this.mBluetoothLEService = bluetoothLEService;
        this.caller = caller;
    }

    public ConnectDeviceStreamHandler() {

    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        eventSink.success(STATE_RECOGNIZING);
        sink = eventSink;
        bluetoothDevice = new ScanDeviceStreamHandler().getBluetoothDevice();
        eventSink.success(bluetoothDevice.getAddress().toString());
        if (bluetoothDevice != null) {
            try {
                Intent gattServiceIntent = new Intent(context, BluetoothLEService.class);
                eventSink.success(STATE_CONNECTING);
                mServiceConnection = new ServiceConnection() {
                    @Override
                    public void onServiceConnected(ComponentName componentName, IBinder service) {
                        eventSink.success(STATE_CONNECTED);
                        mBluetoothLEService = ((BluetoothLEService.LocalBinder) service).getService();
                        if (!mBluetoothLEService.initialize()) {
                            //Unable to initialize Bluetooth
                        } else {
                            //Initialized Bluetooth
                        }
                        mBluetoothLEService.connect(bluetoothDevice.getAddress());
                    }
                    @Override
                    public void onServiceDisconnected(ComponentName componentName) {
                        Log.d(TAG, STATE_DISCONNECTED);
                        eventSink.success(STATE_DISCONNECTED);
                        mBluetoothLEService = null;
                    }
                };
                startBluetoothService();
            } catch (Error error) {
                eventSink.success(STATE_CONNECTING_FAILED);
            }
        } else {
            eventSink.success(STATE_CONNECTING_FAILED);
        }
    }


    private void startBluetoothService() {
        forService = new Intent(context, BluetoothLEService.class);
        context.bindService(new Intent(context, BluetoothLEService.class), mServiceConnection,
                Context.BIND_AUTO_CREATE | Context.BIND_ADJUST_WITH_ACTIVITY);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(forService);
        } else {
            context.startService(forService);
        }

    }

    @Override
    public void onCancel(Object o) {
        sink = null;
    }
}
