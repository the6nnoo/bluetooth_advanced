package com.example.bluetooth_advanced.config;
import java.util.HashMap;


public class UUIDs {
    public static String UUID_BLUETOOTH_SERVICE = "0000baad-0000-1000-8000-00805f9b34fb";
    public static String UUID_BLUETOOTH_DATA_UUID = "0000BEEF-0000-1000-8000-00805F9B34FB";

    public static String NOTIFICATION_TEXT = "Bluetooth Connection Established";
    public static String NOTIFICATION_TITLE = "Bluetooth";


    public static String getNotificationText() {
        return NOTIFICATION_TEXT;
    }

    public static void setNotificationText(String notificationText) {
        NOTIFICATION_TEXT = notificationText;
    }

    public static String getNotificationTitle() {
        return NOTIFICATION_TITLE;
    }

    public static void setNotificationTitle(String notificationTitle) {
        NOTIFICATION_TITLE = notificationTitle;
    }

    public static String getUuidBluetoothService() {return UUID_BLUETOOTH_SERVICE;}
    public static void setUUID_BLUETOOTH_SERVICE(String uUID_BLUETOOTH_SERVICE) {
        UUID_BLUETOOTH_SERVICE = uUID_BLUETOOTH_SERVICE;
    }
    public static void setUUID_BLUETOOTH_DATA_UUID(String uUID_BLUETOOTH_DATA_UUID) {
        UUID_BLUETOOTH_DATA_UUID = uUID_BLUETOOTH_DATA_UUID;
    }
    public static String getUUID_BLUETOOTH_DATA_UUID() {
        return UUID_BLUETOOTH_DATA_UUID;
    }

    private static HashMap<String, String> attributes = new HashMap();

    static {
        attributes.put(UUID_BLUETOOTH_DATA_UUID, "BLUETOOTH_DATA");
        attributes.put(UUID_BLUETOOTH_SERVICE, "BLUETOOTH_SERVICE");
    }

    public static String lookup(String uuid) {
        String name = attributes.get(uuid);
        if(name==null){
            //null
        }else{
            //name
        }
        return name;
    }

}
