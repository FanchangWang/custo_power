package com.guyuexuan.power;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.DisplayMetrics;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.widget.ListView;
import android.widget.BaseAdapter;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import java.util.ArrayList;
import java.util.List;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class MainActivity extends Activity {
    private AppListAdapter userAppAdapter;
    private AppListAdapter systemAppAdapter;
    private List<ApplicationInfo> userApps = new ArrayList<>();
    private List<ApplicationInfo> systemApps = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 获取 ScrollView 并滚动到顶部
        final ScrollView scrollView = (ScrollView) findViewById(R.id.main_scroll_view);
        scrollView.post(new Runnable() {
            @Override
            public void run() {
                scrollView.fullScroll(ScrollView.FOCUS_UP);
            }
        });

        // 打开原生系统设置
        findViewById(R.id.btn_open_setting).setOnClickListener(v -> {
            Intent intent = new Intent(Settings.ACTION_SETTINGS);
            startActivity(intent);
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

    private void loadApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);

        userApps.clear();
        systemApps.clear();

        for (ApplicationInfo app : apps) {
            if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                userApps.add(app);
            } else {
                systemApps.add(app);
            }
        }

        userAppAdapter.notifyDataSetChanged();
        systemAppAdapter.notifyDataSetChanged();
    }

    private void launchApp(ApplicationInfo app) {
        Intent launchIntent = getPackageManager().getLaunchIntentForPackage(app.packageName);
        if (launchIntent != null) {
            startActivity(launchIntent);
        } else {
            Toast.makeText(MainActivity.this, "无法启动该应用", Toast.LENGTH_SHORT).show();
        }
    }

    private class AppListAdapter extends BaseAdapter {
        private boolean isUserApp;
        private LayoutInflater inflater;
        private static final int APPS_PER_ROW = 5;

        AppListAdapter(boolean isUserApp) {
            this.isUserApp = isUserApp;
            this.inflater = LayoutInflater.from(MainActivity.this);
        }

        @Override
        public int getCount() {
            int totalApps = isUserApp ? userApps.size() : systemApps.size();
            return (totalApps + APPS_PER_ROW - 1) / APPS_PER_ROW; // 向上取整得到行数
        }

        @Override
        public Object getItem(int position) {
            return null; // 不需要使用
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LinearLayout rowView;
            if (convertView == null) {
                rowView = (LinearLayout) inflater.inflate(R.layout.item_app_row, parent, false);
            } else {
                rowView = (LinearLayout) convertView;
            }

            // 清除现有的子视图
            rowView.removeAllViews();

            // 计算这一行的起始索引
            int startIndex = position * APPS_PER_ROW;
            List<ApplicationInfo> apps = isUserApp ? userApps : systemApps;

            // 添加这一行的应用图标
            for (int i = 0; i < APPS_PER_ROW; i++) {
                int appIndex = startIndex + i;
                View appView = inflater.inflate(R.layout.item_app, rowView, false);

                if (appIndex < apps.size()) {
                    ApplicationInfo app = apps.get(appIndex);
                    ImageView appIcon = (ImageView) appView.findViewById(R.id.iv_app_icon);
                    TextView appName = (TextView) appView.findViewById(R.id.tv_app_name);

                    appIcon.setImageDrawable(app.loadIcon(getPackageManager()));
                    appName.setText(app.loadLabel(getPackageManager()));

                    // 设置点击事件
                    final int finalAppIndex = appIndex;
                    appView.setOnClickListener(v -> launchApp(apps.get(finalAppIndex)));

                    // 设置长按事件（仅用户应用）
                    if (isUserApp) {
                        appView.setOnLongClickListener(v -> {
                            ApplicationInfo appInfo = apps.get(finalAppIndex);
                            new AlertDialog.Builder(MainActivity.this)
                                    .setTitle("卸载应用")
                                    .setMessage("确定要卸载 " + appInfo.loadLabel(getPackageManager()) + " 吗？")
                                    .setPositiveButton("确定", (dialog, which) -> {
                                        try {
                                            // 使用系统权限卸载应用
                                            String packageName = appInfo.packageName;
                                            Runtime.getRuntime().exec("pm uninstall " + packageName);
                                            Toast.makeText(MainActivity.this, "正在卸载应用...", Toast.LENGTH_SHORT).show();
                                            // 刷新应用列表
                                            loadApps();
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                            Toast.makeText(MainActivity.this, "卸载失败: " + e.getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .setNegativeButton("取消", null)
                                    .show();
                            return true;
                        });
                    }
                } else {
                    // 如果这个位置没有应用，保持空白
                    appView.setVisibility(View.INVISIBLE);
                }

                rowView.addView(appView);
            }

            return rowView;
        }
    }

}