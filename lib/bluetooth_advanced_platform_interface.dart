import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'bluetooth_advanced_method_channel.dart';

abstract class BluetoothAdvancedPlatform extends PlatformInterface {
  /// Constructs a BluetoothAdvancedPlatform.
  BluetoothAdvancedPlatform() : super(token: _token);

  static final Object _token = Object();

  static BluetoothAdvancedPlatform _instance = MethodChannelBluetoothAdvanced();

  /// The default instance of [BluetoothAdvancedPlatform] to use.
  ///
  /// Defaults to [MethodChannelBluetoothAdvanced].
  static BluetoothAdvancedPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [BluetoothAdvancedPlatform] when
  /// they register themselves.
  static set instance(BluetoothAdvancedPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> dispose() {
    throw UnimplementedError('dispose() has not been implemented.');
  }

  /* UUID Stuff */
  Future<String?> setServiceCharactersticUUID(String url) {
    throw UnimplementedError(
        'setServiceCharactersticUUID(url) has not been implemented.');
  }

  Future<String?> setDataCharactersticUUID(String url) {
    throw UnimplementedError(
        'setDataCharactersticUUID(url) has not been implemented.');
  }

  Future<String?> getServiceCharactersticUUID() {
    throw UnimplementedError(
        'getServiceCharactersticUUID() has not been implemented.');
  }

  Future<String?> getDataCharactersticUUID() {
    throw UnimplementedError(
        'getDataCharactersticUUID() has not been implemented.');
  }

  /* */
  Future<String?> setBluetoothEnableCode(String input) async {
    throw UnimplementedError(
        'setBluetoothEnableCode(input) has not been implemented.');
  }

  Future<String?> setLocationEnableCode(String input) async {
    throw UnimplementedError(
        'setServiceCharactersticUUID(input) has not been implemented.');
  }

  Future<int?> setScanPeriod(int input) async {
    throw UnimplementedError('setScanPeriod(input) has not been implemented.');
  }

  Future<String?> getBluetoothEnableCode() async {
    throw UnimplementedError(
        'getBluetoothEnableCode() has not been implemented.');
  }

  Future<String?> getLocationEnableCode() async {
    throw UnimplementedError(
        'getLocationEnableCode() has not been implemented.');
  }

  Future<String?> getNotificationTitle() async {
    throw UnimplementedError(
        'getNotificationTitle() has not been implemented.');
  }

  Future<String?> setNotificationTitle(String title) async {
    throw UnimplementedError(
        'setNotificationTitle() has not been implemented.');
  }

  Future<String?> getNotificationText() async {
    throw UnimplementedError('getNotificationText() has not been implemented.');
  }

  Future<String?> setNotificationText(String text) async {
    throw UnimplementedError('setNotificationText() has not been implemented.');
  }

  Future<int?> getScanPeriod() async {
    throw UnimplementedError('getScanPeriod() has not been implemented.');
  }

  /* Bluetooth connections */

  Stream<String> instantiateLogger() {
    throw UnimplementedError('instantiateLogger() has not been implemented.');
  }

  Stream<String> initBluetooth() {
    throw UnimplementedError('initBluetooth() has not been implemented.');
  }

  Stream<Object> scanDevices() {
    throw UnimplementedError('scanDevices() has not been implemented.');
  }

  Stream<String> connectDevice() {
    throw UnimplementedError('connectDevice() has not been implemented.');
  }

  Stream<String> listenData() {
    throw UnimplementedError('listenData() has not been implemented.');
  }
}
