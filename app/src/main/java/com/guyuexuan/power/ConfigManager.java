package com.guyuexuan.power;

import android.content.Context;
import android.content.SharedPreferences;

public class ConfigManager {
    // SharedPreferences相关（私有，仅内部使用）
    private static final String SP_NAME = "power_config";
    // 静态成员：配置项
    public static boolean isAutoOnBoot = false; // 是否开机自动启动
    public static boolean isAutoOnWifi = false; // 是否开机自动连接Wifi设备
    public static boolean isCarlifeWifiMode = false; // carlife 连接方式：true=wifi模式 false=usb模式
    public static boolean isUsbDeviceMode = false; // usb设备模式：true=Device模式 false=Host模式
    public static String strCarlifeWifiName = "";

    // 私有静态成员
    private static SharedPreferences sSp;
    private static SharedPreferences.Editor sEditor;

    /**
     * 初始化配置管理器（内部自动判断是否已初始化，重复调用无害）
     *
     * @param context 上下文（会自动转为Application Context）
     */
    public static void init(Context context) {
        // 核心：如果已初始化（sSp不为null），直接返回，避免重复初始化
        if (sSp != null) {
            return;
        }
        // 未初始化则执行初始化逻辑
        sSp = context.getApplicationContext().getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
        sEditor = sSp.edit();
        // 加载配置到静态成员
        loadConfig();
    }

    private static void loadConfig() {
        isAutoOnBoot = sSp.getBoolean("is_auto_on_boot", isAutoOnBoot);
        isAutoOnWifi = sSp.getBoolean("is_auto_on_wifi", isAutoOnWifi);
        isCarlifeWifiMode = sSp.getBoolean("is_carlife_wifi_mode", isCarlifeWifiMode);
        isUsbDeviceMode = sSp.getBoolean("is_usb_device_mode", isUsbDeviceMode);
        strCarlifeWifiName = sSp.getString("str_carlief_wifi_name", strCarlifeWifiName);
    }

    public static void saveConfig() {
        sEditor.putBoolean("is_auto_on_boot", isAutoOnBoot);
        sEditor.putBoolean("is_auto_on_wifi", isAutoOnWifi);
        sEditor.putBoolean("is_carlife_wifi_mode", isCarlifeWifiMode);
        sEditor.putBoolean("is_usb_device_mode", isUsbDeviceMode);
        sEditor.putString("str_carlief_wifi_name", strCarlifeWifiName);
        sEditor.apply();
    }
}
