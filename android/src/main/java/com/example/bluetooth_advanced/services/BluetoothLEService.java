package com.example.bluetooth_advanced.services;

import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;
import static androidx.core.app.NotificationCompat.PRIORITY_MIN;

import static com.example.bluetooth_advanced.config.Constants.ACTION_DATA_AVAILABLE;
import static com.example.bluetooth_advanced.config.Constants.ACTION_GATT_CONNECTED;
import static com.example.bluetooth_advanced.config.Constants.ACTION_GATT_DISCONNECTED;
import static com.example.bluetooth_advanced.config.Constants.ACTION_GATT_SERVICES_DISCOVERED;
import static com.example.bluetooth_advanced.config.Constants.EXTRA_DATA;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.bluetooth_advanced.R;
import com.example.bluetooth_advanced.config.UUIDs;
import com.example.bluetooth_advanced.config.Utils;
import com.example.bluetooth_advanced.streamhandlers.ConnectDeviceStreamHandler;

import java.util.List;
import java.util.UUID;

import io.flutter.Log;

public class BluetoothLEService extends Service {

    public final static UUID UUID_BATTERY_LEVEL =
            UUID.fromString(UUIDs.UUID_BLUETOOTH_DATA_UUID);

    private static final String TAG = "DEVICE_HANDSHAKE";
    private static final int STATE_DISCONNECT = 0;
    private static final int STATE_CONNECTING = 1;
    private static final int STATE_CONNECTED = 2;
    IBinder mBinder = new LocalBinder();
    private int mConnectionState = STATE_DISCONNECT;
    private BluetoothAdapter mBluetoothAdapter;

    private static BluetoothGatt mBluetoothGatt;
    private String bluetoothAddress;

    private NotificationManager service;
    private String channelId;

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelId = "MychannelID";
            channelId = createNotificationChannel("my_service", "My Background Service");

            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
                    getApplicationContext(), channelId);
            Notification notification = notificationBuilder
                    .setOngoing(true)
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.bluetooth)
                    .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                    .setContentText(UUIDs.getNotificationText())
                    .setContentTitle(UUIDs.getNotificationTitle())
                    .setPriority(PRIORITY_MIN)
                    .setCategory(Notification.CATEGORY_SERVICE)
                    .build();
            startForeground(189, notification);
        } else {
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String createNotificationChannel(String channelId, String channelName) {
        NotificationChannel chan = new NotificationChannel(channelId,
                channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        service = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        service.createNotificationChannel(chan);
        return channelId;
    }

    private BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            Log.d(TAG, "Connection State Changed");
            super.onConnectionStateChange(gatt, status, newState);
            String intentAction;
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                Log.d(TAG, "New State: " + BluetoothProfile.STATE_CONNECTED);
                intentAction = ACTION_GATT_CONNECTED;
                mConnectionState = STATE_CONNECTED;
                broadcastUpdate(intentAction);
                //only discover services when connected with device
                mBluetoothGatt.discoverServices();

            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                Log.d(TAG, "New State: " + STATE_DISCONNECTED);
                intentAction = ACTION_GATT_DISCONNECTED;
                mConnectionState = STATE_DISCONNECTED;
                broadcastUpdate(intentAction);

            }
        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
            } else {
            }
        }


        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if (status == BluetoothGatt.GATT_SUCCESS) {
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
            super.onCharacteristicChanged(gatt, characteristic);
            broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);
        }

        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
        }

        @Override
        public void onReliableWriteCompleted(BluetoothGatt gatt, int status) {
            super.onReliableWriteCompleted(gatt, status);
        }

        @Override
        public void onReadRemoteRssi(BluetoothGatt gatt, int rssi, int status) {
            super.onReadRemoteRssi(gatt, rssi, status);
        }

        @Override
        public void onMtuChanged(BluetoothGatt gatt, int mtu, int status) {
            super.onMtuChanged(gatt, mtu, status);
        }
    };

    public BluetoothLEService() {

    }

    public static List<BluetoothGattService> getSupportedGattServices() {
        if (mBluetoothGatt == null) return null;
        return mBluetoothGatt.getServices();
    }

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }


    public void broadcastUpdate(final String action, final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);
        if (UUID_BATTERY_LEVEL.equals(characteristic.getUuid())) {
            int format = BluetoothGattCharacteristic.FORMAT_UINT8;
            final int battery_level = characteristic.getIntValue(format, 0);
            intent.putExtra(EXTRA_DATA, battery_level + "%");
        } else {
            intent.putExtra(EXTRA_DATA, characteristic.getStringValue(0)).toString();
            //Log.d(TAG, intent.putExtra(EXTRA_DATA, characteristic.getStringValue(0)).toString());
            //Log.d(TAG, "no battery char");
            //Log.e(TAG, "data: " + characteristic.getStringValue(0));
            //Log.e(TAG, "uuid: " + String.valueOf(characteristic.getService().getUuid()));
            //Log.e(TAG, "uuid: " + String.valueOf(characteristic.getUuid()));
        }
        sendBroadcast(intent);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }


    public void close() {
        if (mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.close();
        mBluetoothGatt = null;
    }


    public void disconnect() {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            return;
        }
        mBluetoothGatt.disconnect();
    }

    public void dispose(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).cancelAll();
            ((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).deleteNotificationChannel(channelId);
            if(((NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE)).getActiveNotifications().length==0){
                //it stops the data
                context.stopService(new ConnectDeviceStreamHandler().forService);
                //it will trigger connection state change
                if(mBluetoothGatt!=null) mBluetoothGatt.disconnect();
                if(new ConnectDeviceStreamHandler().mServiceConnection!=null) context.unbindService(new ConnectDeviceStreamHandler().mServiceConnection);
                //BluetoothLEService.this.stopSelf();
                //this.stopForeground(true);
                //context.stopService(new Intent(context, BluetoothLEService.class));
            }else{
                //notif is still there
            }
        }
    }

    public boolean initialize() {
        mBluetoothAdapter = Utils.getBluetoothAdapter(this);
        return true;
    }

    public boolean connect(@NonNull String address) {
        if (mBluetoothAdapter != null && address.equals(bluetoothAddress) && mBluetoothGatt != null) {
            if (mBluetoothGatt.connect()) {
                mConnectionState = STATE_CONNECTING;
                return true;
            } else {
                return false;
            }
        }
        final BluetoothDevice bluetoothDevice = mBluetoothAdapter.getRemoteDevice(address);
        if (bluetoothDevice == null) {
            return false;
        }
        mBluetoothGatt = bluetoothDevice.connectGatt(this, false, bluetoothGattCallback);
        bluetoothAddress = address;
        mConnectionState = STATE_CONNECTING;
        return true;
    }


    public static void readCharacteristic(BluetoothGattCharacteristic bluetoothGattCharacteristic) {
        mBluetoothGatt.readCharacteristic(bluetoothGattCharacteristic);
    }


    public static void setCharacteristicNotification(BluetoothGattCharacteristic characteristic, boolean enabled) {
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);
    }

    public class LocalBinder extends Binder {
        public BluetoothLEService getService() {
            return BluetoothLEService.this;
        }
    }


}
