## 适用车型
北京现代 库斯途车机

## 安装说明
请自行搜索侧边栏破解教程或者使用 adb install 安装，[Releases](https://github.com/FanchangWang/custo_power/releases) 中的 apk 是已经公签好的。

## 功能列表
- 1、打开原生设置
- 2、切换 百度 CarLife USB/WIFI 连接模式。(切换之后，关闭 CarLife 重新打开即可生效)
- 3、切换 USB HOST/DEVICE 模式
- 4、重启车机系统
- 5、重启车机到 Recovery 模式(这个貌似没啥用)

## 其他原车应用修改版
- 1、百度 Carlife
    + 修改 竖屏全屏模式
    + 修改 版本号

## 青龙面板：北京现代 APP 自动任务脚本
原作者 https://github.com/xiaobu689/HhhhScripts 我修改了部分代码以适配青龙面板。
脚本源码 [bjxd.py](./ql_script/bjxd.py) 支持以下环境变量

```shell
BJXD="token1,token2,token3" // 北京现代 APP api token // 多个账号用英文 , 分割

HUNYUAN_API_KEY="sk-xxxx" // 腾讯混元大模型 APIKey
```

青龙导入方式

 ```shell
 ql raw https://raw.githubusercontent.com/FanchangWang/custo_power/refs/heads/main/ql_script/bjxd.py
 ```
