package com.example.bluetooth_advanced;


import static com.example.bluetooth_advanced.config.Constants.ACTION_DATA_AVAILABLE;
import static com.example.bluetooth_advanced.config.Messages.DEVICE_GATT_AVAILABLE;
import static com.example.bluetooth_advanced.config.Messages.DEVICE_GATT_CONNECTED;
import static com.example.bluetooth_advanced.config.Messages.DEVICE_GATT_CONNECTING;
import static com.example.bluetooth_advanced.config.Messages.DEVICE_GATT_DISCONNECTED;
import static com.example.bluetooth_advanced.config.Messages.DEVICE_GATT_INITIATED;
import static com.example.bluetooth_advanced.config.Messages.PERMISSION_REQUIRED_LOCATION;
import static com.example.bluetooth_advanced.receivers.GattUpdateReceiver.GattUpdateIntentFilter;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.example.bluetooth_advanced.config.Constants;
import com.example.bluetooth_advanced.config.UUIDs;
import com.example.bluetooth_advanced.config.Utils;
import com.example.bluetooth_advanced.listeners.GattUpdateReceiverListener;
import com.example.bluetooth_advanced.listeners.LoggerUpdateReceiverListener;
import com.example.bluetooth_advanced.receivers.GattUpdateReceiver;
import com.example.bluetooth_advanced.receivers.LoggerUpdateReceiver;
import com.example.bluetooth_advanced.services.BluetoothLEService;
import com.example.bluetooth_advanced.streamhandlers.ConnectDeviceStreamHandler;
import com.example.bluetooth_advanced.streamhandlers.LoggerStreamHandler;
import com.example.bluetooth_advanced.streamhandlers.ScanDeviceStreamHandler;

import java.util.HashMap;
import java.util.List;

import io.flutter.Log;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

public class BluetoothAdvancedPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {
    private MethodChannel methodChannel;
    private BluetoothAdapter mBluetoothAdapter;
    private Context context;
    private Activity activity;

    //private static String TAG = "BluetoothAdvanced";
    private boolean mConnected = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;
    private BluetoothLEService mBluetoothLEService;
    private EventChannel btStatusEventChannel;
    private EventChannel loggerEventChannel;
    private EventChannel scanDevicesEventChannel;
    private EventChannel startServiceEventChannel;
    private EventChannel connectDeviceEventChannel;

    private EventChannel.EventSink initBTSink;
    GattUpdateReceiver mGattUpdateReceiver;
    LoggerUpdateReceiver mLoggerUpdateReceiver;

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        methodChannel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "bluetooth_advanced");
        loggerEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "logger");
        btStatusEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "bluetooth_status");
        scanDevicesEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "bluetooth_scan");
        connectDeviceEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "bluetooth_connect");
        startServiceEventChannel = new EventChannel(flutterPluginBinding.getBinaryMessenger(), "bluetooth_data");

        methodChannel.setMethodCallHandler(this);
        context = flutterPluginBinding.getApplicationContext();
        mBluetoothAdapter = Utils.getBluetoothAdapter(context);
        mGattUpdateReceiver = new GattUpdateReceiver();


        initLogger();
        initBluetooth();
        scanDevicesEventChannel.setStreamHandler(new ScanDeviceStreamHandler(context, activity, mBluetoothAdapter, mGattUpdateReceiver, loggerEventChannel, mLoggerUpdateReceiver));
        connectDeviceEventChannel.setStreamHandler(new ConnectDeviceStreamHandler(context, activity, mBluetoothLEService, "main"));
        connectService();
    }

    private void initLogger() {
        mLoggerUpdateReceiver = new LoggerUpdateReceiver(activity, context);
        mLoggerUpdateReceiver.setCallback(context, new LoggerUpdateReceiverListener() {
            @Override
            public void onLoggerUpdateReceiverChange(String log) {
            }
        });
        loggerEventChannel.setStreamHandler(new LoggerStreamHandler(context, activity, mLoggerUpdateReceiver));
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {

        if (call.method.equals("getPlatformVersion")) {
            result.success(android.os.Build.VERSION.RELEASE.toString());
        }

        /* Getters and Setters UUID */
        else if (call.method.equals("setServiceCharactersticUUID")) {
            final String uuid = call.argument("uuid"); //"0000baad-0000-1000-8000-00805f9b34fb"
            if (!validateUUID(uuid)) {
                result.error(null, "The service uuid is invalid! Excepted format: 123e4567-e89b-12d3-a456-426614174000 ", null);
            } else {
                UUIDs.setUUID_BLUETOOTH_SERVICE(uuid);
                result.success(UUIDs.getUuidBluetoothService().toString());
            }
        } else if (call.method.equals("setDataCharactersticUUID")) {
            final String uuid = call.argument("uuid"); //"0000baad-0000-1000-8000-00805f9b34fb"
            if (!validateUUID(uuid)) {
                result.error(null, "The data uuid is invalid! Excepted format: 123e4567-e89b-12d3-a456-426614174000 ", null);
            } else {
                UUIDs.setUUID_BLUETOOTH_DATA_UUID(uuid);
                result.success(UUIDs.getUUID_BLUETOOTH_DATA_UUID());
            }
        } else if (call.method.equals("getDataCharactersticUUID")) {
            result.success(UUIDs.getUUID_BLUETOOTH_DATA_UUID());
        } else if (call.method.equals("getServiceCharactersticUUID")) {
            result.success(UUIDs.getUuidBluetoothService());
        }

        /* Getter and Setter Constants*/
        else if (call.method.equals("setBluetoothEnableCode")) {
            final int code = call.argument("code"); //"0000baad-0000-1000-8000-00805f9b34fb"
            Constants.setRequestBluetoothEnableCode(code);
            result.success(Constants.getRequestBluetoothEnableCode() + "");
        } else if (call.method.equals("setLocationEnableCode")) {
            final int code = call.argument("code"); //"0000baad-0000-1000-8000-00805f9b34fb"
            Constants.setRequestLocationEnableCode(code);
            result.success(Constants.getRequestLocationEnableCode());
        } else if (call.method.equals("setScanPeriod")) {
            final int period = call.argument("period"); //"0000baad-0000-1000-8000-00805f9b34fb"
            Constants.setScanPeriod(period);
            result.success(Constants.getScanPeriod());
        } else if (call.method.equals("getBluetoothEnableCode")) {
            result.success(Constants.getRequestBluetoothEnableCode());
        } else if (call.method.equals("getLocationEnableCode")) {
            result.success(Constants.getRequestLocationEnableCode());
        } else if (call.method.equals("getScanPeriod")) {
            result.success(Constants.getScanPeriod());
        } else if (call.method.equals("setNotificationText")) {
            final String text = call.argument("text");
            if (text != null) {
                UUIDs.setNotificationText(text);
                result.success(UUIDs.getNotificationText());
            } else
                result.error("need some valid text", null, null);
        } else if (call.method.equals("setNotificationTitle")) {
            final String text = call.argument("title");
            if (text != null) {
                UUIDs.setNotificationTitle(text);
                result.success(UUIDs.getNotificationTitle());
            } else
                result.error("need some valid title", null, null);
        } else if (call.method.equals("getNotificationTitle")) {
            result.success(UUIDs.getNotificationTitle());
        } else if (call.method.equals("getNotificationText")) {
            result.success(UUIDs.getNotificationText());
        } else if (call.method.equals("dispose")) {
            Intent serviceIntent = new Intent(context, BluetoothLEService.class);
            context.stopService(serviceIntent);
            new BluetoothLEService().dispose(context);
            result.success("true");
        } else {
            result.notImplemented();
        }
    }


    private void initBluetooth() {
        btStatusEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                initBTSink = eventSink;
                //activity.registerReceiver(mGattUpdateReceiver, GattUpdateIntentFilter());
                //if (Utils.checkBluetoothPermission(context)) {
                //    //mGattUpdateReceiver.setCallback(new GattUpdateReceiverListener() {
                //    //    @Override
                //    //    public void onGattUpdateReceiverChange(String status) {
                //    //        Log.d(TAG, "Reciver Status Updated to " + status + mNotifyCharacteristic);
                //    //        initBTSink.success(status);
                //    //    }
                //    //}, mNotifyCharacteristic);
                //} else {
                //    final Intent intent = new Intent(ACTION_LOGGER_UPDATE);

                //    if (loggerEventChannel != null) {
                //        intent.putExtra(ACTION_LOGGER_UPDATE, "PERMISSION_REQUIRED_LOCATION");
                //        mLoggerUpdateReceiver.sendABroadCast(intent);
                //    }
                //    initBTSink.success("PERMISSION_REQUIRED_LOCATION");
                //}
            }


            @Override
            public void onCancel(Object o) {
                initBTSink = null;
            }
        });
    }

    private void connectService() {

        startServiceEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, EventChannel.EventSink eventSink) {
                activity.registerReceiver(mGattUpdateReceiver, GattUpdateIntentFilter());
                if (Utils.checkBluetoothPermission(context)) {
                    try {
                        mGattUpdateReceiver.setCallback(new GattUpdateReceiverListener() {
                            @Override
                            public void onGattUpdateReceiverChange(String status) {
                                if (status != null && status.startsWith(DEVICE_GATT_INITIATED)) {
                                    eventSink.success(DEVICE_GATT_INITIATED);
                                }
                                /* Services are Discovered */
                                if (status != null && status.startsWith(DEVICE_GATT_CONNECTING)) {
                                    List<BluetoothGattService> gattServices = BluetoothLEService.getSupportedGattServices();
                                    if (gattServices == null)
                                        return;
                                    String uuid = null;
                                    String serviceString = "unknown service";
                                    String charaString = "unknown characteristic";

                                    for (BluetoothGattService gattService : gattServices) {
                                        uuid = gattService.getUuid().toString();
                                        serviceString = UUIDs.lookup(uuid);
                                        if (serviceString != null) {
                                            List<BluetoothGattCharacteristic> gattCharacteristics =
                                                    gattService.getCharacteristics();
                                            for (BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics) {
                                                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                                                uuid = gattCharacteristic.getUuid().toString();
                                                charaString = UUIDs.lookup(uuid);
                                                if (charaString != null) {
                                                    //Set Service name to charaString
                                                }
                                                mNotifyCharacteristic = gattCharacteristic;
                                                mNotifyCharacteristic = gattCharacteristic;
                                            }
                                        }
                                    }
                                }
                                if (status != null && status.startsWith(DEVICE_GATT_CONNECTED)) {
                                    eventSink.success(DEVICE_GATT_CONNECTED);
                                }
                                /* Data is available*/
                                if (status != null && (status.startsWith(DEVICE_GATT_AVAILABLE) || status.startsWith(ACTION_DATA_AVAILABLE))) {
                                    eventSink.success(status);
                                    //Log.d("SENDING DATA", status);
                                }
                                if (status != null && status.startsWith(DEVICE_GATT_DISCONNECTED)) {
                                    eventSink.success(DEVICE_GATT_DISCONNECTED);
                                }
                                if (mNotifyCharacteristic != null) {
                                    final int charaProp = mNotifyCharacteristic.getProperties();
                                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                                        BluetoothLEService.readCharacteristic(mNotifyCharacteristic);
                                    }
                                    if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                                        BluetoothLEService.setCharacteristicNotification(mNotifyCharacteristic, true);
                                    }
                                } else {
                                    //DEVICE DATA NOT FOUND
                                }
                            }
                        }, mNotifyCharacteristic);
                    } catch (Error e) {
                        eventSink.error(e.getMessage(), null, null);
                    }
                } else {
                    eventSink.error(PERMISSION_REQUIRED_LOCATION, null, null);
                }
            }

            @Override
            public void onCancel(Object o) {

            }
        });

    }


    boolean validateUUID(String uuid) {
        if (uuid == null || uuid.length() < 35) {
            return false;
        } else return true;
    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding binding) {
        methodChannel.setMethodCallHandler(null);

    }

    @Override
    public void onAttachedToActivity(@NonNull ActivityPluginBinding activityPluginBinding) {
        activity = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding activityPluginBinding) {
        activity = activityPluginBinding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {
    }

}
