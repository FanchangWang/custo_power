package com.guyuexuan.power;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {

    private static final String TAG = "main_activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            setTitle(getTitle() + " v" + versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, e.getMessage());
        }
        ConfigManager.init(this);

        Switch switchAutoOnBoot = (Switch) findViewById(R.id.switch_auto_on_boot);
        Switch switchAutoOnBT = (Switch) findViewById(R.id.switch_auto_on_bt);
        Switch switchAutoOnWifi = (Switch) findViewById(R.id.switch_auto_on_wifi);
        Switch switchCarlifeWifiMode = (Switch) findViewById(R.id.switch_carlife_wifi_mode);
        Switch switchUsbDeviceMode = (Switch) findViewById(R.id.switch_usb_device_mode);
        EditText editCarlifeBtName = (EditText) findViewById(R.id.edit_carlife_bt_name);
        EditText editCarlifeWifiName = (EditText) findViewById(R.id.edit_carlife_wifi_ssid);

        // 设置开关状态
        switchAutoOnBoot.setChecked(ConfigManager.isAutoOnBoot);
        switchAutoOnBT.setChecked(ConfigManager.isAutoOnBT);
        switchAutoOnWifi.setChecked(ConfigManager.isAutoOnWifi);
        switchCarlifeWifiMode.setChecked(ConfigManager.isCarlifeWifiMode);
        switchUsbDeviceMode.setChecked(ConfigManager.isUsbDeviceMode);
        editCarlifeBtName.setText(ConfigManager.strCarlifeBtName);
        editCarlifeWifiName.setText(ConfigManager.strCarlifeWifiName);

        // 设置开关监听
        switchAutoOnBoot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConfigManager.isAutoOnBoot = isChecked;
            ConfigManager.saveConfig();
            Log.d(TAG, "百度 CarLife 自启动方式：开机 => " + isChecked);
        });
        switchAutoOnBT.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConfigManager.isAutoOnBT = isChecked;
            ConfigManager.saveConfig();
            Log.d(TAG, "百度 CarLife 自启动方式：蓝牙 => " + isChecked);
        });
        switchAutoOnWifi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConfigManager.isAutoOnWifi = isChecked;
            ConfigManager.saveConfig();
            Log.d(TAG, "百度 CarLife 自启动方式：WIFI => " + isChecked);
        });
        findViewById(R.id.btn_carlife_bt_clean).setOnClickListener(v -> {
            editCarlifeBtName.setText("");
        });
        findViewById(R.id.btn_carlife_bt_save).setOnClickListener(v -> {
            ConfigManager.strCarlifeBtName = editCarlifeBtName.getText().toString();
            ConfigManager.saveConfig();
        });

        findViewById(R.id.btn_carlife_wifi_clean).setOnClickListener(v -> {
            editCarlifeWifiName.setText("");
        });
        findViewById(R.id.btn_carlife_wifi_save).setOnClickListener(v -> {
            ConfigManager.strCarlifeWifiName = editCarlifeWifiName.getText().toString();
            ConfigManager.saveConfig();
        });
        switchCarlifeWifiMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Settings.System.putInt(getContentResolver(), "CarLifeConnection", isChecked ? 1 : 0);

            ConfigManager.isCarlifeWifiMode = isChecked;
            ConfigManager.saveConfig();

            String msg = "切换 百度 CarLife 连接方式: " + (isChecked ? "WIFI" : "USB");
            Log.d(TAG, msg);
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        });
        switchUsbDeviceMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            File file = new File("/sys/class/gpio/gpio22/value");
            try {
                FileOutputStream fw = new FileOutputStream(file);
                byte[] mode = (isChecked ? "1" : "0").getBytes();
                fw.write(mode);
                fw.close();

                ConfigManager.isUsbDeviceMode = isChecked;
                ConfigManager.saveConfig();

                String msg = "切换 USB 模式: " + (isChecked ? "DEVICE" : "HOST");
                Log.d(TAG, msg);
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
            } catch (Throwable e) {
                Log.e(TAG, e.getMessage());
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });

        // 重启车机系统
        findViewById(R.id.btn_reboot_os).setOnClickListener(v -> {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null) {
                try {
                    pm.reboot(null);
                    Toast.makeText(MainActivity.this, "重启车机系统 按钮被按下!", Toast.LENGTH_SHORT).show();
                } catch (Throwable e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Error PowerManager 获取失败!", Toast.LENGTH_SHORT).show();
            }
        });

        // 重启到 recovery
        findViewById(R.id.btn_reboot_rec).setOnClickListener(v -> {
            PowerManager pm = (PowerManager) getSystemService(POWER_SERVICE);
            if (pm != null) {
                try {
                    pm.reboot(null);
                    Toast.makeText(MainActivity.this, "重启至 recovery 按钮被按下!", Toast.LENGTH_SHORT).show();
                } catch (Throwable e) {
                    Log.e(TAG, e.getMessage());
                    Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(MainActivity.this, "Error PowerManager 获取失败!", Toast.LENGTH_SHORT).show();
            }
        });

        // 打开原生系统设置
        findViewById(R.id.btn_open_setting).setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
        });

        // 退出软件
        findViewById(R.id.btn_finish).setOnClickListener(v -> finishAffinity());

    }
}
