package com.guyuexuan.power;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 读取数据
        // findViewById(R.id.btn_read).setOnClickListener(v -> initRead());

        // 开机启动 CarLife
        findViewById(R.id.btn_auto_enabled).setOnClickListener(v -> {
            ComponentName receiver = new ComponentName(this, BootReceiver.class);
            getPackageManager().setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
            Toast.makeText(MainActivity.this, "开机启动 Carlife！", Toast.LENGTH_SHORT).show();
        });

        // 停止开机启动 CarLife
        findViewById(R.id.btn_auto_disabled).setOnClickListener(v -> {
            ComponentName receiver = new ComponentName(this, BootReceiver.class);
            getPackageManager().setComponentEnabledSetting(receiver,
                    PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
            Toast.makeText(MainActivity.this, "开机启动 已停止！", Toast.LENGTH_SHORT).show();
        });

        // 切换 CarLife USB
        findViewById(R.id.btn_carlife_usb).setOnClickListener(v -> {
            Settings.System.putInt(getContentResolver(), "CarLifeConnection", 0);
            Toast.makeText(MainActivity.this, "切换 CarLife 连接方式！USB", Toast.LENGTH_SHORT).show();
        });

        // 切换 CarLife WIFI
        findViewById(R.id.btn_carlife_wifi).setOnClickListener(v -> {
            Settings.System.putInt(getContentResolver(), "CarLifeConnection", 1);
            Toast.makeText(MainActivity.this, "切换 CarLife 连接方式！WIFI", Toast.LENGTH_SHORT).show();
        });

        // 切换 USB HOST MODE
        findViewById(R.id.btn_usb_host).setOnClickListener(v -> {
            File file = new File("/sys/class/gpio/gpio22/value");
            try {
                FileOutputStream fw = new FileOutputStream(file);
                byte[] host = "0".getBytes();
                fw.write(host);
                fw.close();
                Toast.makeText(MainActivity.this, "切换 USB 为 HOST 模式！", Toast.LENGTH_SHORT).show();
            } catch (Throwable e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // 切换 USB DEVICE MODE
        findViewById(R.id.btn_usb_device).setOnClickListener(v -> {
            File file = new File("/sys/class/gpio/gpio22/value");
            try {
                FileOutputStream fw = new FileOutputStream(file);
                byte[] host = "1".getBytes();
                fw.write(host);
                fw.close();
                SystemProperties.set("ctl.start", "console");
                Toast.makeText(MainActivity.this, "切换 USB 为 DEVICE 模式！", Toast.LENGTH_SHORT).show();
            } catch (Throwable e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
                    e.printStackTrace();
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
                    e.printStackTrace();
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

    // @SuppressLint("SetTextI18n")
    // private void initRead() {
    //
    // // 读取屏幕分辨率
    // DisplayMetrics displayMetrics = new DisplayMetrics();
    // getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
    // int screenWidth = displayMetrics.widthPixels;
    // int screenHeight = displayMetrics.heightPixels;
    //
    // System.out.println("屏幕分辨率: " + screenWidth + " x " + screenHeight);
    //
    // // 读取带标题栏的应用分辨率
    // int appWithTitleBarWidth = getWindow().getDecorView().getWidth();
    // int appWithTitleBarHeight = getWindow().getDecorView().getHeight();
    //
    // System.out.println("应用带标题栏分辨率: " + appWithTitleBarWidth + " x " +
    // appWithTitleBarHeight);
    //
    // // 读取不带标题栏的应用分辨率
    // int appNoTitleBarWidth =
    // getWindow().findViewById(Window.ID_ANDROID_CONTENT).getWidth();
    // int appNoTitleBarHeight =
    // getWindow().findViewById(Window.ID_ANDROID_CONTENT).getHeight();
    //
    // System.out.println("应用不带标题栏分辨率: " + appNoTitleBarWidth + " x " +
    // appNoTitleBarHeight);
    //
    // String filePath = "/sys/class/gpio/gpio22/value";
    // BufferedReader reader = null;
    // String line = "读取失败";
    //
    // try {
    // reader = new BufferedReader(new FileReader(filePath));
    // line = reader.readLine();
    // while (line != null) {
    // System.out.println(line);
    // line = reader.readLine();
    // }
    // } catch (Throwable e) {
    // e.printStackTrace();
    // } finally {
    // if (reader != null) {
    // try {
    // reader.close();
    // } catch (Throwable e) {
    // e.printStackTrace();
    // }
    // }
    // }
    //
    // ((TextView) findViewById(R.id.textView)).setText("屏幕宽高：" + screenWidth + " x
    // " + screenHeight
    // + "\n应用宽高：" + appWithTitleBarWidth + " x " + appWithTitleBarHeight
    // + "\n正文宽高：" + appNoTitleBarWidth + " x " + appNoTitleBarHeight
    // + "\nUSB 模式：" + line);
    // }

    public static class SystemProperties {
        private static final Class<?> SP = getSystemPropertiesClass();

        public static void set(String key, String val) {
            if (SP == null) {
                throw new IllegalStateException("SystemProperties class 反射错误.");
            }

            try {
                SP.getMethod("set", String.class, String.class).invoke(null, key, val);
            } catch (Throwable e) {
                e.printStackTrace();
                throw new RuntimeException("SystemProperties set Error: " + e.getMessage(), e);
            }
        }

        private static Class<?> getSystemPropertiesClass() {
            try {
                return Class.forName("android.os.SystemProperties");
            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }

        private SystemProperties() {
            throw new AssertionError("No instances");
        }
    }

}