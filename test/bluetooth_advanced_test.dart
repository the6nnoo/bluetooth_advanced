// import 'package:flutter_test/flutter_test.dart';
// import 'package:bluetooth_advanced/bluetooth_advanced.dart';
// import 'package:bluetooth_advanced/bluetooth_advanced_platform_interface.dart';
// import 'package:bluetooth_advanced/bluetooth_advanced_method_channel.dart';
// import 'package:plugin_platform_interface/plugin_platform_interface.dart';

// class MockBluetoothAdvancedPlatform 

//     with MockPlatformInterfaceMixin
//     implements BluetoothAdvancedPlatform {

//   @override
//   Future<String?> getPlatformVersion() => Future.value('42');
  
  
// }

// void main() {
//   final BluetoothAdvancedPlatform initialPlatform = BluetoothAdvancedPlatform.instance;

//   test('$MethodChannelBluetoothAdvanced is the default instance', () {
//     expect(initialPlatform, isInstanceOf<MethodChannelBluetoothAdvanced>());
//   });

//   test('getPlatformVersion', () async {
//     BluetoothAdvanced bluetoothAdvancedPlugin = BluetoothAdvanced();
//     MockBluetoothAdvancedPlatform fakePlatform = MockBluetoothAdvancedPlatform();
//     BluetoothAdvancedPlatform.instance = fakePlatform;
  
//     expect(await bluetoothAdvancedPlugin.getPlatformVersion(), '42');
//   });
// }
