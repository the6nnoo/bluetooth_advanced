import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'bluetooth_advanced_platform_interface.dart';

/// An implementation of [BluetoothAdvancedPlatform] that uses method channels.
class MethodChannelBluetoothAdvanced extends BluetoothAdvancedPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('bluetooth_advanced');

  @visibleForTesting
  final loggerChannel = const EventChannel('logger');

  @visibleForTesting
  final btStatusEventChannel = const EventChannel('bluetooth_status');

  @visibleForTesting
  final scanDevicesEventChannel = const EventChannel('bluetooth_scan');

  @visibleForTesting
  final connectDevicesEventChannel = const EventChannel('bluetooth_connect');

  @visibleForTesting
  final listenDataEventChannel = const EventChannel('bluetooth_data');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> dispose() async {
    final version = await methodChannel.invokeMethod<String>('dispose');
    return version;
  }

  @override
  Future<String?> setDataCharactersticUUID(String uuid) async {
    final version = await methodChannel
        .invokeMethod<String>('setDataCharactersticUUID', {"uuid": uuid});
    return version;
  }

  @override
  Future<String?> getDataCharactersticUUID() async {
    final version =
        await methodChannel.invokeMethod<String>('getDataCharactersticUUID');
    return version;
  }

  @override
  Future<String?> getServiceCharactersticUUID() async {
    final version =
        await methodChannel.invokeMethod<String>('getServiceCharactersticUUID');
    return version;
  }

  @override
  Future<String?> setServiceCharactersticUUID(String uuid) async {
    final version = await methodChannel
        .invokeMethod<String>('setServiceCharactersticUUID', {"uuid": uuid});
    return version;
  }

  @override
  Future<String?> setBluetoothEnableCode(String input) async {
    final version = await methodChannel
        .invokeMethod<String>('setBluetoothEnableCode', {"code": input});
    return version;
  }

  @override
  Future<String?> setLocationEnableCode(String input) async {
    final version = await methodChannel
        .invokeMethod<String>('setLocationEnableCode', {"code": input});
    return version;
  }

  @override
  Future<int?> setScanPeriod(int input) async {
    final version = await methodChannel
        .invokeMethod<int>('setScanPeriod', {"period": input});
    return version;
  }

  @override
  Future<int?> getScanPeriod() async {
    final version = await methodChannel.invokeMethod<int>('getScanPeriod');
    return version;
  }

  @override
  Future<String?> setNotificationTitle(String title) async {
    final version = await methodChannel
        .invokeMethod<String>('setNotificationTitle', {"title": title});
    return version;
  }

  @override
  Future<String?> setNotificationText(String text) async {
    final version = await methodChannel
        .invokeMethod<String>('setNotificationText', {"text": text});
    return version;
  }

  @override
  Future<String?> getNotificationText() async {
    final version =
        await methodChannel.invokeMethod<String>('getNotificationText');
    return version;
  }

  @override
  Future<String?> getNotificationTitle() async {
    final version =
        await methodChannel.invokeMethod<String>('getNotificationTitle');
    return version;
  }

  @override
  Future<String?> getBluetoothEnableCode() async {
    final version =
        await methodChannel.invokeMethod<String>('getBluetoothEnableCode');
    return version;
  }

  @override
  Future<String?> getLocationEnableCode() async {
    final version =
        await methodChannel.invokeMethod<String>('getLocationEnableCode');
    return version;
  }

  @override
  Stream<String> instantiateLogger() {
    return loggerChannel.receiveBroadcastStream().cast();
  }

  @override
  Stream<String> initBluetooth() {
    return btStatusEventChannel.receiveBroadcastStream().cast();
  }

  @override
  Stream<Object> scanDevices() {
    return scanDevicesEventChannel.receiveBroadcastStream().cast();
  }

  @override
  Stream<String> connectDevice() {
    return connectDevicesEventChannel.receiveBroadcastStream().cast();
  }

  @override
  Stream<String> listenData() {
    Stream<dynamic> broadcast = listenDataEventChannel.receiveBroadcastStream();
    return broadcast.cast();
  }
}
