package com.example.bluetooth_advanced.receivers;
import static com.example.bluetooth_advanced.config.Constants.ACTION_LOGGER_CONNECTED;
import static com.example.bluetooth_advanced.config.Constants.ACTION_LOGGER_DISCONNECTED;
import static com.example.bluetooth_advanced.config.Constants.ACTION_LOGGER_UPDATE;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import com.example.bluetooth_advanced.listeners.LoggerUpdateReceiverListener;


public class LoggerUpdateReceiver extends BroadcastReceiver {

    //private String TAG = "LoggerUpdateReceiver";
    LoggerUpdateReceiverListener callback;
    Activity activity;
    Context context;


    public LoggerUpdateReceiver() {
    }

    public LoggerUpdateReceiver(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }


    public void setCallback(Context context, LoggerUpdateReceiverListener callback) {
        this.context = context;
        this.callback = callback;
    }

    public static IntentFilter LoggerUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_LOGGER_CONNECTED);
        intentFilter.addAction(ACTION_LOGGER_DISCONNECTED);
        intentFilter.addAction(ACTION_LOGGER_UPDATE);
        return intentFilter;
    }


    public void sendABroadCast(Intent intent) {
        context.registerReceiver(LoggerUpdateReceiver.this, LoggerUpdateIntentFilter());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.sendBroadcast(intent);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent == null && callback == null) {
            this.callback.onLoggerUpdateReceiverChange("UNKNOWN ACTION: null");
        } else {
            if(ACTION_LOGGER_UPDATE.equals(intent.getAction())){
                final String action = intent.getStringExtra(ACTION_LOGGER_UPDATE);
                if(action!=null) this.callback.onLoggerUpdateReceiverChange(action);
            }
            else if(ACTION_LOGGER_CONNECTED.equals(intent.getAction())) {
                final String action = intent.getStringExtra(ACTION_LOGGER_CONNECTED);
                if(action!=null) this.callback.onLoggerUpdateReceiverChange(action);
            }
            else  if(ACTION_LOGGER_DISCONNECTED.equals(intent.getAction())){
                final String action = intent.getStringExtra(ACTION_LOGGER_DISCONNECTED);
                if(action!=null) this.callback.onLoggerUpdateReceiverChange(action);
            } else {
                if(ACTION_LOGGER_UPDATE.equals(intent.getAction())){
                   //action
                }
                this.callback.onLoggerUpdateReceiverChange("UNKNOWN ACTION TAKEN" + intent.getAction());
            }
        }
    }
}

