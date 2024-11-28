# Beijing Hyundai Custo

北京现代库斯途车机相关 APK 软件及 Python 自动任务脚本。

## APK 软件说明

### 1. Power.apk 功能说明
- 打开原生设置
- 切换百度 CarLife 连接模式 (USB/WIFI)
- 切换 USB 模式 (HOST/DEVICE)
- 系统重启
  - 普通重启
  - Recovery 模式重启

### 2. 原车应用 百度CarLife 修改版
- 竖屏全屏显示

## APK 安装教程
从 [Releases](https://github.com/FanchangWang/custo_power/releases) 下载已公签的 APK，通过侧边栏破解安装，[侧边栏破解教程(ix35跟库斯途通用)](https://www.dongchedi.com/ugc/article/7230446621241344524)


## Python 自动任务脚本
基于 [@xiaobu689/HhhhScripts](https://github.com/xiaobu689/HhhhScripts) 修改，适配青龙面板。

**功能：**
- 日常签到
- 浏览文章
- 每日答题
- 答题转发（配置多个账号时，多个账号之间互相转发）

#### Token 获取教程
- 方式 1. 浏览器打开 https://bm2-wx.bluemembers.com.cn/browser/login 登录账号后，在开发人员工具的控制台执行 `console.log(localStorage.getItem('token'))` 获取。
- 方式 2. 通过 [ql_script/gui.py](./ql_script/gui.py) 脚本工具获取。
    + 运行方式：`python3 gui.py`
    + 依赖要求：`Python3` `PyQt5` `PyQtWebEngine`

#### 环境变量配置
```shell
# 北京现代 APP token (多账号用英文逗号分隔)
export BJXD="token1,token2,token3"
# 腾讯混元AI APIKey (可选，用于获取每日答题答案，不配置则随机答题)
export HUNYUAN_API_KEY="sk-xxxx"
```

#### 运行方式

方式 1. 下载 [ql_script/bjxd.py](./ql_script/bjxd.py) 文件，本地运行
```shell
python3 bjxd.py
```

方式 2. 青龙面板单文件导入
```shell
ql raw https://raw.githubusercontent.com/FanchangWang/custo_power/refs/heads/main/ql_script/bjxd.py
```

#### 依赖要求
- Python3
- requests

## 致谢
感谢 [@xiaobu689](https://github.com/xiaobu689) 提供的原始脚本。
