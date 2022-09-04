import 'bluetooth_advanced_platform_interface.dart';

class BluetoothAdvanced {
  Future<String?> getPlatformVersion() {
    return BluetoothAdvancedPlatform.instance.getPlatformVersion();
  }

  /* UUID stuff */
  Future<String?> setServiceCharactersticUUID(String uuid) {
    return BluetoothAdvancedPlatform.instance.setServiceCharactersticUUID(uuid);
  }

  Future<String?> getServiceCharactersticUUID() {
    return BluetoothAdvancedPlatform.instance.getServiceCharactersticUUID();
  }

  Future<String?> getDataCharactersticUUID() {
    return BluetoothAdvancedPlatform.instance.getDataCharactersticUUID();
  }

  Future<String?> setDataCharactersticUUID(String uuid) {
    return BluetoothAdvancedPlatform.instance.setDataCharactersticUUID(uuid);
  }

  Future<String?> setBluetoothEnableCode(String code) {
    return BluetoothAdvancedPlatform.instance.setBluetoothEnableCode(code);
  }

  Future<String?> setLocationEnableCode(String code) {
    return BluetoothAdvancedPlatform.instance.setLocationEnableCode(code);
  }

  Future<String?> getBluetoothEnableCode() {
    return BluetoothAdvancedPlatform.instance.getBluetoothEnableCode();
  }

  Future<String?> getLocationEnableCode() {
    return BluetoothAdvancedPlatform.instance.getLocationEnableCode();
  }

  Future<int?> setScanPeriod(int input) {
    return BluetoothAdvancedPlatform.instance.setScanPeriod(input);
  }

  Future<int?> getScanPeriod() {
    return BluetoothAdvancedPlatform.instance.getScanPeriod();
  }

  Future<String?> getNotificationTitle() {
    return BluetoothAdvancedPlatform.instance.getNotificationTitle();
  }

  Future<String?> setNotificationTitle(String title) {
    return BluetoothAdvancedPlatform.instance.setNotificationTitle(title);
  }

  Future<String?> getNotificationText() {
    return BluetoothAdvancedPlatform.instance.getNotificationText();
  }

  Future<String?> setNotificationText(String text) {
    return BluetoothAdvancedPlatform.instance.setNotificationText(text);
  }

  /* bluetooth connection stuff */

  Stream<String> instantiateLogger() {
    return BluetoothAdvancedPlatform.instance.instantiateLogger();
  }

  Stream<String> initBluetooth() {
    return BluetoothAdvancedPlatform.instance.initBluetooth();
  }

  Stream<Object> scanDevices() {
    return BluetoothAdvancedPlatform.instance.scanDevices();
  }

  Stream<String> connectDevice() {
    return BluetoothAdvancedPlatform.instance.connectDevice();
  }

  Stream<String> listenData() {
    return BluetoothAdvancedPlatform.instance.listenData();
  }

  Future<String?> dispose() {
    return BluetoothAdvancedPlatform.instance.dispose();
  }
}
