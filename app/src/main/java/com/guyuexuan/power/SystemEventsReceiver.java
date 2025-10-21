package com.guyuexuan.power;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
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
            case "android.bluetooth.anw.action.CONNECT_STATUS":  // 处理蓝牙连接成功事件
                Bundle bundle = intent.getExtras();
                if (bundle == null) return;
                int nProfile = bundle.getInt("Profile"); // 蓝牙通道 0=HFP 通话 2=A2DP 媒体音频
                if (nProfile != 0) return; // 只保留 HFP 通话通道
                int nConnectStatus = bundle.getInt("Value"); // 蓝牙状态 0=已断开 1=已连接
                if (nConnectStatus != 1) return; // 只保留连接成功
                msg = "蓝牙 CONNECT_STATUS! Switch: " + ConfigManager.isAutoOnBT;
                Log.d(TAG, msg);
                if (ConfigManager.isAutoOnBT) {
                    if (ConfigManager.strCarlifeBtName.isEmpty()) {
                        startCarlife(context);
                    } else {
                        String mAddress = bundle.getString("Address"); // 这个参数不确定是否存在
                        if (mAddress == null || mAddress.isEmpty()) { // 判断一下防止出错
                            msg = msg + " 无 Address";
                            Log.d(TAG, "蓝牙连接成功，无 Address");
                            startCarlife(context);
                        } else {
                            msg = msg + "Addr:" + mAddress;
                            BluetoothDevice device = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(mAddress);
                            String bluetoothName = device.getName();   //  蓝牙名字
                            if (bluetoothName != null) {
                                Log.d(TAG, "蓝牙连接成功，设备名称：" + bluetoothName);
                                msg = msg + " name: " + bluetoothName;
                                if (bluetoothName.toLowerCase().contains(ConfigManager.strCarlifeBtName.toLowerCase())) {
                                    startCarlife(context);
                                }
                            }
                        }
                    }
                }
                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
                break;
            case WifiManager.NETWORK_STATE_CHANGED_ACTION:  // 处理WiFi连接成功事件
                // 从Intent中获取网络状态信息
                NetworkInfo networkInfo = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                if (networkInfo != null && networkInfo.isConnected()) {
                    msg = "WIFI NETWORK_STATE_CHANGED_ACTION! Switch: " + ConfigManager.isAutoOnWifi;
                    Log.d(TAG, msg);
                    if (ConfigManager.isAutoOnWifi) {
                        if (ConfigManager.strCarlifeWifiName.isEmpty()) {
                            startCarlife(context);
                        } else {
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
                                            if (wifiSsid.toLowerCase().contains(ConfigManager.strCarlifeWifiName.toLowerCase())) {
                                                startCarlife(context);
                                            }
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
