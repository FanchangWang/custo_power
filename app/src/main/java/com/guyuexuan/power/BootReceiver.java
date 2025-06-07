package com.guyuexuan.power;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Intent startIntent = new Intent();
            startIntent.setClassName("com.hsae.d531mc.carlife", "com.hsae.d531mc.carlife.CarLifeActivity");
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            android.content.SharedPreferences prefs = context.getSharedPreferences("CarLifeConfig",
                    android.content.Context.MODE_PRIVATE);
            String startTime = prefs.getString("startTime", "boot");

            if ("bluetooth".equals(startTime)) {
                long endTime = System.currentTimeMillis() + 10 * 60 * 1000;
                BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                while (System.currentTimeMillis() < endTime) {
                    if (bluetoothAdapter != null && bluetoothAdapter.isEnabled()) {
                        if (bluetoothAdapter.getProfileConnectionState(
                                BluetoothProfile.HEADSET) == BluetoothProfile.STATE_CONNECTED
                                || bluetoothAdapter.getProfileConnectionState(
                                BluetoothProfile.A2DP) == BluetoothProfile.STATE_CONNECTED) {
                            context.startActivity(startIntent);
                            break;
                        }
                    }
                    try {
                        Thread.sleep(5000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            } else {
                context.startActivity(startIntent);
            }
        }
    }
}