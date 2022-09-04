package com.example.bluetooth_advanced.streamhandlers;


import static com.example.bluetooth_advanced.receivers.LoggerUpdateReceiver.LoggerUpdateIntentFilter;

import android.app.Activity;
import android.content.Context;

import com.example.bluetooth_advanced.receivers.LoggerUpdateReceiver;
import com.example.bluetooth_advanced.listeners.LoggerUpdateReceiverListener;

import io.flutter.plugin.common.EventChannel;

public class LoggerStreamHandler implements EventChannel.StreamHandler {

    private Context context;
    private Activity activity;
    private LoggerUpdateReceiver mLoggerUpdateReceiver;
    private EventChannel.EventSink sink;

    private String SYMBOL= "\u25BA ";



    public LoggerStreamHandler(Context context, Activity activity, LoggerUpdateReceiver mLoggerUpdateReceiver) {
        this.context = context;
        this.activity = activity;
        this.mLoggerUpdateReceiver = mLoggerUpdateReceiver;
    }

    @Override
    public void onListen(Object o, EventChannel.EventSink eventSink) {
        sink = eventSink;
        sink.success(SYMBOL+ "Console log is ready");
        context.registerReceiver(mLoggerUpdateReceiver, LoggerUpdateIntentFilter());

        mLoggerUpdateReceiver.setCallback(this.context,new  LoggerUpdateReceiverListener() {
            @Override
            public void onLoggerUpdateReceiverChange(String log) {
            //if(log==null || log.isEmpty()){
            //    sink.success(SYMBOL+ "Console log connection failed");
            //}else{
            //    sink.success(SYMBOL +log);
            //}
            }

        });
    }

    @Override
    public void onCancel(Object o) {

    }
}
