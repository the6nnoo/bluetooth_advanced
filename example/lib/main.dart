import 'package:bluetooth_advanced_example/constants.dart';
import 'package:flutter/material.dart';
import 'dart:async';
import 'package:flutter/services.dart';
import 'package:bluetooth_advanced/bluetooth_advanced.dart';
import 'constants.dart';

void main() {
  WidgetsFlutterBinding.ensureInitialized();
  runApp(const MyApp());
}

class MyApp extends StatefulWidget {
  const MyApp({Key? key}) : super(key: key);

  @override
  State<MyApp> createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  List<Widget> list = [const Text('')];

  final _bluetoothAdvanced = BluetoothAdvanced();
  late StreamSubscription streamSubscription;
  late StreamBuilder streamBuilder;

  bool isConnected = false;
  String? data;
  bool isData = false;
  @override
  void initState() {
    super.initState();

    initBluetoothConfig();
    listentoDeviceData();
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      home: Scaffold(
        appBar: AppBar(
          backgroundColor: Colors.cyan.shade900,
          title: const Text('bluetooth_advanced'),
        ),
        body: Padding(
          padding: const EdgeInsets.symmetric(horizontal: 20, vertical: 10),
          child: Column(
            mainAxisAlignment: MainAxisAlignment.start,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Expanded(
                  flex: 2,
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.start,
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      StreamBuilder<Object>(
                        stream: _bluetoothAdvanced.scanDevices(),
                        builder: (BuildContext context,
                            AsyncSnapshot<Object> snapshot) {
                          if (snapshot.hasError) {
                            return showError(snapshot.error);
                          } else if (snapshot.hasData) {
                            if (snapshot.data.toString() ==
                                SCANNING_FINISHED_WITH_NO_DEVICE) {
                              return deviceNotFound();
                            } else {
                              List<String> device =
                                  snapshot.data.toString().split(",");
                              return deviceFound(device[0], device[1]);
                            }
                          } else {
                            return deviceScanning();
                          }
                        },
                      ),
                      StreamBuilder<String>(
                        stream: _bluetoothAdvanced.initBluetooth(),
                        builder: (BuildContext context,
                            AsyncSnapshot<String> snapshot) {
                          if (snapshot.hasError) {
                            return const Text("Error Occured!");
                          } else if (snapshot.hasData) {
                            return Text(
                                "Starting Connection: ${snapshot.data}");
                          } else {
                            return const Text(
                                "Waiting for connection to start...");
                          }
                        },
                      ),
                      isConnected && isData
                          ? Text(data!)
                          : const Text('Waiting to be connected'),
                    ],
                  )),
              const Spacer(),
              bottomBar()
            ],
          ),
        ),
      ),
    );
  }

  /* utilities functions */
  void printWrapped(String text) {
    final pattern = RegExp('.{1,300}');
    pattern.allMatches(text).forEach((match) => print(match.group(0)));
  }

  Future<void> initBluetoothConfig() async {
    try {
      print(await _bluetoothAdvanced
          .setServiceCharactersticUUID("0000baad-0000-1000-8000-00805f9b34fb"));
      print(await _bluetoothAdvanced
          .setDataCharactersticUUID("0000BEEF-0000-1000-8000-00805F9B34FB"));
      print(await _bluetoothAdvanced.getDataCharactersticUUID());
      print(await _bluetoothAdvanced.getServiceCharactersticUUID());
      print(await (_bluetoothAdvanced.setScanPeriod(10000)));
      await _bluetoothAdvanced.setNotificationText("new text");
      await _bluetoothAdvanced.setNotificationTitle("new title");
    } catch (e) {
      print(e.toString());
    }

    if (!mounted) return;
  }

  listentoDeviceData() {
    _bluetoothAdvanced.listenData().listen((event) {
      if (event.startsWith(DEVICE_GATT_AVAILABLE)) {
        setState(() {
          isData = true;
          data = event.toString();
        });
      }
      switch (event.toString()) {
        case DEVICE_GATT_INITIATED:
          printWrapped(DEVICE_GATT_INITIATED);
          break;
        case DEVICE_GATT_CONNECTING:
          printWrapped(DEVICE_GATT_CONNECTING);
          break;
        case DEVICE_GATT_CONNECTED:
          printWrapped(DEVICE_GATT_CONNECTED);
          break;
        case DEVICE_GATT_AVAILABLE:
          printWrapped(DEVICE_GATT_CONNECTED);
          break;
        case DEVICE_GATT_DISCONNECTED:
          setState(() {
            isData = false;
            isConnected = false;
          });
          break;
        default:
      }
    });
  }

  /* widget function */
  showError(Object? error) {
    String errorMessage = 'Some Error Encountered';
    if (error.runtimeType == PlatformException) {
      PlatformException? platformException = error as PlatformException?;
      errorMessage = platformException!.code;
    } else {
      errorMessage = error.toString();
    }
    return Card(
        child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('Error:',
                    style:
                        TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(height: 8),
                Text(errorMessage,
                    style: const TextStyle(
                        fontSize: 14, fontStyle: FontStyle.italic)),
              ],
            )));
  }

  deviceScanning() {
    return Card(
        child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 20),
            child: Row(
              children: const [
                Text("scanning devices",
                    style:
                        TextStyle(fontSize: 14, fontStyle: FontStyle.italic)),
                SizedBox(width: 12),
                SizedBox(
                    height: 22, width: 22, child: CircularProgressIndicator())
              ],
            )));
  }

  deviceNotFound() {
    return Card(
        child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 20),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text("No Device found, Retry",
                    style:
                        TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(height: 8),
                const Text(
                    """Troubleshoot: \n ► Try increasing the scan period.\n ► Check if the device is paired in bluetooth settings.\n ► Check if paired device has correct configurations like uuids""",
                    style:
                        TextStyle(fontSize: 14, fontStyle: FontStyle.italic)),
              ],
            )));
  }

  deviceFound(String address, String name) {
    return Card(
        child: Padding(
      padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 10),
      child: Row(
        children: [
          isConnected
              ? const Icon(Icons.bluetooth_connected_rounded,
                  color: Colors.green)
              : const Icon(Icons.bluetooth_rounded, color: Colors.grey),
          const SizedBox(width: 10),
          Column(
              mainAxisAlignment: MainAxisAlignment.start,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(name,
                    style: const TextStyle(
                        fontSize: 20, fontWeight: FontWeight.bold)),
                const SizedBox(height: 6),
                Text(address,
                    style: const TextStyle(
                        fontSize: 14, fontStyle: FontStyle.italic))
              ]),
          const Spacer(),
          ElevatedButton(
              style: ButtonStyle(
                backgroundColor: MaterialStateProperty.all<Color>(
                    isConnected ? Colors.green : Colors.lightBlue),
              ),
              onPressed: () {
                _bluetoothAdvanced.connectDevice().listen((event) {
                  switch (event.toString()) {
                    case STATE_RECOGNIZING:
                      break;
                    case STATE_CONNECTING:
                      break;
                    case STATE_CONNECTED:
                      setState(() {
                        isConnected = true;
                      });
                      break;
                    case STATE_DISCONNECTED:
                      setState(() {
                        isConnected = false;
                      });
                      break;
                    case STATE_CONNECTING_FAILED:
                      break;
                    default:
                  }
                });
              },
              child: Text(
                isConnected ? 'Connected' : 'Connect',
                style:
                    const TextStyle(fontSize: 14, fontStyle: FontStyle.italic),
              ))
        ],
      ),
    ));
  }

  bottomBar() {
    return Row(
      crossAxisAlignment: CrossAxisAlignment.end,
      children: [
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: const [
            Icon(Icons.info_outline_rounded, color: Colors.grey),
            SizedBox(width: 10),
            Text(
              'Remember to turn on bluetooth\nand GPS first',
              style: TextStyle(
                  fontStyle: FontStyle.italic, color: Colors.blueGrey),
            ),
          ],
        ),
        const Spacer(),
        ElevatedButton(
            style: ButtonStyle(
              padding: MaterialStateProperty.all<EdgeInsetsGeometry>(
                  const EdgeInsets.symmetric(vertical: 20)),
              backgroundColor: MaterialStateProperty.all<Color>(
                  isConnected && isData ? Colors.cyan.shade900 : Colors.grey),
            ),
            onPressed: () async {
              await _bluetoothAdvanced.dispose();
            },
            child: const Icon(Icons.stop_circle)),
      ],
    );
  }
}
