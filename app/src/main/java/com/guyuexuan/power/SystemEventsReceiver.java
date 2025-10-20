package com.guyuexuan.power;

import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

public class SystemEventsReceiver extends BroadcastReceiver {
    private static final String TAG = "SystemEventsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        String action = intent.getAction();
        if (action == null) return;

        ConfigManager.init(context);

        String msg;

        switch (action) {
            case Intent.ACTION_BOOT_COMPLETED:  // 处理开机
                msg = "开机 ACTION_BOOT_COMPLETED! Switch: " + ConfigManager.isAutoOnBoot;
                Log.d(TAG, msg);
                if (ConfigManager.isAutoOnBoot) {
                    startCarlife(context);
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                break;
            case BluetoothDevice.ACTION_ACL_CONNECTED:  // 处理蓝牙连接成功事件
                msg = "蓝牙 ACTION_ACL_CONNECTED! Switch: " + ConfigManager.isAutoOnBT;
                Log.d(TAG, msg);
                // 从Intent中获取连接的蓝牙设备对象
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device != null) {
                    String bluetoothName = device.getName();
                    // 注意：部分设备可能未设置名称，此时getName()返回null
                    if (bluetoothName != null) {
                        Log.d(TAG, "蓝牙连接成功，设备名称：" + bluetoothName);
                        msg = msg + " name: " + bluetoothName;
                        if (ConfigManager.isAutoOnBT) {
                            if (ConfigManager.strCarlifeBtName.isEmpty()
                                    || bluetoothName.toLowerCase().contains(ConfigManager.strCarlifeBtName.toLowerCase())) {
                                startCarlife(context);
                            }
                        }
                    }
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:  // 处理WiFi连接成功事件
                Log.d(TAG, "NETWORK_STATE_CHANGED_ACTION");
                // 从Intent中获取网络状态信息
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.isConnected()
                        && networkInfo.getType() == ConnectivityManager.TYPE_WIFI
                        && networkInfo.getDetailedState() == NetworkInfo.DetailedState.CONNECTED) {
                    msg = "WIFI NETWORK_STATE_CHANGED_ACTION! Switch: " + ConfigManager.isAutoOnWifi;
                    Log.d(TAG, msg);
                    // 通过WifiManager获取当前连接的WiFi信息
                    WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                    if (wifiManager != null) {
                        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                        if (wifiInfo != null) {
                            String wifiSsid = wifiInfo.getSSID();
                            if (wifiSsid != null) {
                                // 注意：SSID可能包含双引号（如"我的WiFi"），需要去除
                                if (wifiSsid.startsWith("\"") && wifiSsid.endsWith("\"")) {
                                    wifiSsid = wifiSsid.substring(1, wifiSsid.length() - 1);
                                }
                                if (!wifiSsid.isEmpty()) {
                                    Log.d(TAG, "WiFi连接成功，名称（SSID）：" + wifiSsid);
                                    msg = msg + " ssid: " + wifiSsid;
                                    if (ConfigManager.isAutoOnWifi) {
                                        if (ConfigManager.strCarlifeWifiName.isEmpty()
                                                || wifiSsid.toLowerCase().contains(ConfigManager.strCarlifeWifiName.toLowerCase())) {
                                            startCarlife(context);
                                        }
                                    }
                                }
                            }
                        }
                    }
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 启动 carlife
     *
     * @param context Context
     */
    private void startCarlife(Context context) {
        try {
            Intent startIntent = new Intent();
            startIntent.setClassName("com.hsae.d531mc.carlife", "com.hsae.d531mc.carlife.CarLifeActivity");
            startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(startIntent);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Carlife 应用未安装", e);
            Toast.makeText(context, "Carlife 应用未安装", Toast.LENGTH_SHORT).show();
        }
    }
}
