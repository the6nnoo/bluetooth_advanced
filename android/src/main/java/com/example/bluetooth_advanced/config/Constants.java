package com.example.bluetooth_advanced.config;

public class Constants {

    /*Logger Intents*/
    public final static String ACTION_LOGGER_CONNECTED = "com.example.andy.ACTION_LOGGER_CONNECTED";
    public final static String ACTION_LOGGER_DISCONNECTED = "com.example.andy.ACTION_LOGGER_DISCONNECTED";
    public final static String ACTION_LOGGER_UPDATE = "com.example.andy.ACTION_LOGGER_UPDATE";

    /*Gatt Intents*/
    public final static String ACTION_GATT_INITIATED =
            "com.app.androidkt.heartratemonitor.le.ACTION_GATT_INITIATED";
    public final static String ACTION_GATT_CONNECTED =
            "com.app.androidkt.heartratemonitor.le.ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED =
            "com.app.androidkt.heartratemonitor.ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED =
            "com.app.androidkt.heartratemonitor.le.ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE =
            "com.app.androidkt.heartratemonitor.ACTION_DATA_AVAILABLE";
    public final static String EXTRA_DATA =
            "com.app.androidkt.heartratemonitor.le.EXTRA_DATA";


    public static int REQUEST_BLUETOOTH_ENABLE_CODE = 101;
    public static int REQUEST_LOCATION_ENABLE_CODE = 100;
    public static int REQUEST_LOCATION_COARSE_ENABLE_CODE = 103;
    public static int SCAN_PERIOD = 15000;




    public static int getRequestBluetoothEnableCode() {
        return REQUEST_BLUETOOTH_ENABLE_CODE;
    }
    public static void setRequestBluetoothEnableCode(int requestBluetoothEnableCode) {
        REQUEST_BLUETOOTH_ENABLE_CODE = requestBluetoothEnableCode;
    }
    public static int getRequestLocationEnableCode() {
        return REQUEST_LOCATION_ENABLE_CODE;
    }
    public static void setRequestLocationEnableCode(int requestLocationEnableCode) {
        REQUEST_LOCATION_ENABLE_CODE = requestLocationEnableCode;
    }
    public static int getRequestLocationCoarseEnableCode() {
        return REQUEST_LOCATION_COARSE_ENABLE_CODE;
    }
    public static void setRequestLocationCoarseEnableCode(int requestLocationCoarseEnableCode) {
        REQUEST_LOCATION_COARSE_ENABLE_CODE = requestLocationCoarseEnableCode;
    }
    public static int getScanPeriod() {
        return SCAN_PERIOD;
    }
    public static void setScanPeriod(int scanPeriod) {
        SCAN_PERIOD = scanPeriod;
    }
}
