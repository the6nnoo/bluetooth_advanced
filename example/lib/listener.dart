import 'package:bluetooth_advanced/bluetooth_advanced.dart';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

class Scanner extends ChangeNotifier {
  late String data = 'no-data';

  Scanner() {
    _handleCityChanges();
  }

  void _handleCityChanges() {
    final _bluetoothAdvancedPlugin = BluetoothAdvanced();
    _bluetoothAdvancedPlugin.instantiateLogger().listen(
      (d) {
        data = "***" + d.toString();
        print("**#**" + data.toString());
        notifyListeners();
      },
    );
  }
}
