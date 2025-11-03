package com.guyuexuan.power;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Process;
import android.provider.Settings;
import android.util.Log;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

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
        Switch switchAutoOnWifi = (Switch) findViewById(R.id.switch_auto_on_wifi);
        Switch switchCarlifeWifiMode = (Switch) findViewById(R.id.switch_carlife_wifi_mode);
        Switch switchCarlifeHideStatusBar = (Switch) findViewById(R.id.switch_carlife_hide_status_bar);
        Switch switchUsbDeviceMode = (Switch) findViewById(R.id.switch_usb_device_mode);
        EditText editCarlifeWifiName = (EditText) findViewById(R.id.edit_carlife_wifi_ssid);

        // 设置开关状态
        switchAutoOnBoot.setChecked(ConfigManager.isAutoOnBoot);
        switchAutoOnWifi.setChecked(ConfigManager.isAutoOnWifi);
        switchCarlifeWifiMode.setChecked(Settings.System.getInt(getContentResolver(), "CarLifeConnection", 0) == 1);
        switchCarlifeHideStatusBar.setChecked(Settings.System.getInt(getContentResolver(), "CarLifeHideStatusBar", 0) == 1);
        switchUsbDeviceMode.setChecked(ConfigManager.isUsbDeviceMode);
        editCarlifeWifiName.setText(ConfigManager.strCarlifeWifiName);

        // 设置开关监听
        switchAutoOnBoot.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConfigManager.isAutoOnBoot = isChecked;
            ConfigManager.saveConfig();
            Log.d(TAG, "百度 CarLife 自启动方式：开机 => " + isChecked);
        });
        switchAutoOnWifi.setOnCheckedChangeListener((buttonView, isChecked) -> {
            ConfigManager.isAutoOnWifi = isChecked;
            ConfigManager.saveConfig();
            Log.d(TAG, "百度 CarLife 自启动方式：WIFI => " + isChecked);
        });

        findViewById(R.id.btn_carlife_wifi_clean).setOnClickListener(v -> {
            editCarlifeWifiName.setText("");
        });
        findViewById(R.id.btn_carlife_wifi_save).setOnClickListener(v -> {
            ConfigManager.strCarlifeWifiName = editCarlifeWifiName.getText().toString();
            ConfigManager.saveConfig();
            Toast.makeText(MainActivity.this, "WIFI名 已保存过滤值: " + ConfigManager.strCarlifeWifiName, Toast.LENGTH_SHORT).show();
        });
        switchCarlifeWifiMode.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Settings.System.putInt(getContentResolver(), "CarLifeConnection", isChecked ? 1 : 0);
            String msg = "切换 百度 CarLife 连接方式: " + (isChecked ? "WIFI" : "USB");
            Log.d(TAG, msg);
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        });
        switchCarlifeHideStatusBar.setOnCheckedChangeListener((buttonView, isChecked) -> {
            Settings.System.putInt(getContentResolver(), "CarLifeHideStatusBar", isChecked ? 1 : 0);
            String msg = "切换 百度 CarLife 系统状态栏: " + (isChecked ? "隐藏" : "显示");
            Log.d(TAG, msg);
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btn_carlife_kill_process).setOnClickListener(v -> {
            // to-do: android 4.4 系统中，拥有 uid system 权限，杀死包名 com.hsae.d531mc.carlife 的进程

            final String targetPackage = "com.hsae.d531mc.carlife";
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            if (am == null) {
                Log.e(TAG, "ActivityManager 获取失败!");
                Toast.makeText(MainActivity.this, "Error: ActivityManager 获取失败!", Toast.LENGTH_SHORT).show();
                return;
            }
            String msg = "杀死百度 CarLife 进程: ";
            /* 第一步：硬杀——Process.killProcess(pid) */
            List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
            if (list != null) {
                for (ActivityManager.RunningAppProcessInfo info : list) {
                    if (targetPackage.equals(info.processName)) {
                        int pid = info.pid;
                        Process.killProcess(pid);
                        msg = msg + "killProcess() PID=" + pid;
                        break;
                    }
                }
            }
            /* 第二步：补刀——killBackgroundProcesses */
            am.killBackgroundProcesses(targetPackage);
            msg = msg + " killBackgroundProcesses() 补刀";
            Log.d(TAG, msg);
            Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
        });
        findViewById(R.id.btn_carlife_start_process).setOnClickListener(v -> {
            try {
                Intent startIntent = new Intent();
                startIntent.setClassName("com.hsae.d531mc.carlife", "com.hsae.d531mc.carlife.CarLifeActivity");
                startIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startIntent);
                finish();
            } catch (ActivityNotFoundException e) {
                Log.e(TAG, "Carlife 应用未安装", e);
                Toast.makeText(this, "Carlife 应用未安装", Toast.LENGTH_SHORT).show();
            }
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

//    /**
//     * 隐藏系统状态栏
//     */
//    private void hideSystemStatusBar() {
//        if (Settings.System.getInt(getContentResolver(), "CarLifeHideStatusBar", 0) == 1) { // 隐藏系统状态栏
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        } else { // 显示系统状态栏
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//        }
//    }
}
