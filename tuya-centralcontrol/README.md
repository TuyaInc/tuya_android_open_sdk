# 垂直品类标准控制SDK

本SDK封装了照明类通用的控制功能，将通用的控制功能点封装成了方法。

为了便于业务方对控制功能使用，简化调用方式，提供本SDK。

## 标准指令说明

### 使用标准指令之前

未使用标准控制指令时，[设备控制](https://tuyainc.github.io/tuyasmart_home_android_sdk_doc/zh-hans/resource/Device.html#%E8%AE%BE%E5%A4%87%E6%8E%A7%E5%88%B6) 一般使用这种方式：

```java
ITuyaDevice mDevice = TuyaHomeSdk.newDeviceInstance(String devId);
// 监听控制结果
mDevice.registerDevListener(new IDevListener() {
    @Override
    public void onDpUpdate(String devId, String dpStr) {

    }
    @Override
    public void onRemoved(String devId) {

    }
    @Override
    public void onStatusChanged(String devId, boolean online) {

    }
    @Override
    public void onNetworkStatusChanged(String devId, boolean status) {

    }
    @Override
    public void onDevInfoUpdate(String devId) {

    }
});
mDevice.publishDps("{\"101\": true}", new IResultCallback() {
    @Override
    public void onError(String code, String error) {
        Toast.makeText(mContext, "开灯失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        Toast.makeText(mContext, "开灯成功", Toast.LENGTH_SHORT).show();
    }
});
```

这种方式控制时，会发送dpId，如101、102之类的给设备来控制。其中101就是这个设备定义的开关dpId。

这么做的缺点是，如果另一个设备也有开关功能，但是不是101控制开关，你就需要传入不同的参数来控制。而当n个设备都有开关功能，但是却dpId都不同，就要写非常多的适配逻辑。

为了解决同一个功能定义的id不同的问题，引入了标准指令的概念。

### 使用标准指令之后

什么是[标准指令](https://developer.tuya.com/cn/docs/iot/open-api/standard-function/standard/standarddescription?id=K9i5ql6waswzq)？

标准指令就是特定功能的标准编号。如照明类设备的开灯功能，其标准指令一定是"switch_led"。发送控制指令switch_led，一定可以控制照明设备的开关。

在集成了此SDK之后，调用方式变化如下：

```java
ITuyaDevice mDevice = TuyaHomeSdk.newDeviceInstance(String devId);
// 注意：这里方法是registerDeviceListener，注册的 Listener 是 IDeviceListener
tuyaDevice.registerDeviceListener(new IDeviceListener() {
    @Override
    public void onDpUpdate(String devId, Map<String, Object> dpCodeMap) {

    }

    @Override
    public void onRemoved(String devId) {

    }

    @Override
    public void onStatusChanged(String devId, boolean online) {

    }

    @Override
    public void onNetworkStatusChanged(String devId, boolean status) {

    }

    @Override
    public void onDevInfoUpdate(String devId) {

    }
});
HashMap<String, Object> dpCodeMap = new HashMap<>();
dpCodeMap.put("switch_led", true);
// 发送标准指令
tuyaDevice.publishCommands(dpCodeMap, new IResultCallback() {
    @Override
    public void onError(String code, String error) {
        Toast.makeText(mContext, "开灯失败", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSuccess() {
        Toast.makeText(mContext, "开灯成功", Toast.LENGTH_SHORT).show();
    }
});
```

<font color=red>**注意：标准指令使用方法`registerDeviceListener`注册监听， 非标准是registerDevListener**</font>

值得注意的是，目前不是所有设备都支持标准指令控制，后文会说明如何判断该设备是否支持标准指令控制。

如果不支持的设备，而又必须使用标准控制，需要联系涂鸦适配。

### 标准指令文档

所有标准指令都可以在涂鸦智能平台查找到：

[灯具(dj) 标准指令集](https://developer.tuya.com/cn/docs/iot/open-api/standard-function/lighting/categorydj/f?id=K9i5ql3v98hn3)

[开关-插座-排插(kg,cz,pc) 标准指令集](https://developer.tuya.com/cn/docs/iot/open-api/standard-function/electrician-category/categorykgczpc/fkg?id=K9gf7o3qbbklt)

[场景开关(cjkg) 标准指令集](https://developer.tuya.com/cn/docs/iot/open-api/standard-function/electrician-category/categorycjkg/f?id=K9gf7nx6jelo8)

等等。

有了`tuyaDevice.publishCommands`方法和上面的指令，就可以发送标准指令来控制设备。

## 照明设备控制

照明设备较为复杂，同时存在v1和v2新旧两种固件，即使使用了标准指令，也需要开发两套控制逻辑。

因此对照明设备功能进行封装，封装了灯具设备的开关、工作模式切换、亮度控制、冷暖控制、彩光控制和四种情景模式的控制。

### 添加依赖

```groovy
// homesdk 依赖，注意，必须使用大于等于此版本的SDK
implementation 'com.tuya.smart:tuyasmart:3.17.9'
// 控制SDK依赖
implementation 'com.tuya.smart:tuyasmart-centralcontrol:1.0.0beta2'
```

需要注意的是，tuyasmart-centralcontrol使用了kotlin编译，需要引入kotlin库确保其正常使用。

> 项目中已引入kotlin的可忽略下面的配置。

**kotlin接入**

在根目录的build.gradle中引入kotlin插件的依赖：

```groovy
buildscript {
    ext.kotlin_version = '1.3.72'
    dependencies {
    	...
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
    }
}
```

在app的build.gradle中引入kotlin插件和kotlin包：

```groovy
apply plugin: 'kotlin-android'
dependencies {
	implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"
}
```

### 快速使用

首先，创建ITuyaLightDevice对象，灯相关的方法均封装在此方法中。

```java
ITuyaLightDevice lightDevice = new TuyaLightDevice(String devId);
```

该对象封装了灯的所有dp点，包括控制指令的下发和上报。

这里提供几个简单的调用示例：
```java
// 创建lightDevice
ITuyaLightDevice lightDevice = new TuyaLightDevice("vdevo159793004250542");

// 注册监听
lightDevice.registerLightListener(new ILightListener() {
    @Override
    public void onDpUpdate(LightDataPoint dataPoint) { // 返回LightDataPoint，包含灯所有功能点的值
        Log.i("test_light", "onDpUpdate:" + dataPoint);
    }

    @Override
    public void onRemoved() {
        Log.i("test_light", "onRemoved");
    }

    @Override
    public void onStatusChanged(boolean status) {
        Log.i("test_light", "onDpUpdate:" + status);
    }

    @Override
    public void onNetworkStatusChanged(boolean status) {
        Log.i("test_light", "onDpUpdate:" + status);
    }

    @Override
    public void onDevInfoUpdate() {
        Log.i("test_light", "onDevInfoUpdate:");
    }
});
// 开灯
lightDevice.powerSwitch(true, new IResultCallback() {
    @Override
    public void onError(String code, String error) {
        Log.i("test_light", "powerSwitch onError:" + code + error);
    }

    @Override
    public void onSuccess() {
        Log.i("test_light", "powerSwitch onSuccess:");
    }
});
// 晚安场景
lightDevice.scene(LightScene.SCENE_GOODNIGHT, new IResultCallback() {
    @Override
    public void onError(String code, String error) {
        Log.i("test_light", "scene onError:" + code + error);
    }

    @Override
    public void onSuccess() {
        Log.i("test_light", "scene onSuccess:");
    }
});
// 设置颜色
lightDevice.colorHSV(100, 100, 100, new IResultCallback() {
    @Override
    public void onError(String code, String error) {
        Log.i("test_light", "colorHSV onError:" + code + error);
    }
    @Override
    public void onSuccess() {
        Log.i("test_light", "colorHSV onSuccess:");
    }
});
```

更多API请参考下面的文档。

### 注册监听

**方法说明**

```java
/**
 * 注册监听
 */
void registerLightListener(ILightListener listener);
```

其中，ILightListener回调如下：

```java
public interface ILightListener {
    /**
     * 监听照明设备dp点变化
     *
     * @param dataPoint 该灯具所有dp点的状态
     */
    void onDpUpdate(LightDataPoint dataPoint);

    /**
     * 设备移除
     */
    void onRemoved();

    /**
     * 设备上下线
     */
    void onStatusChanged(boolean online);

    /**
     * 网络状态
     */
    void onNetworkStatusChanged(boolean status);

    /**
     * 设备信息更新例如name之类的
     */
    void onDevInfoUpdate();
}
```

**参数说明**

值得说明的是`LightDataPoint`对象，该对象封装了当前设备所有功能点。当功能点发生变化时，将会回调。每次回调的都会是完整的对象。

以下是该对象参数的具体含义：

```java
public class LightDataPoint {
    /**
     * 开关
     */
    public boolean powerSwitch;
    /**
     * 工作模式。
     * <p>
     * MODE_WHITE为白光模式；
     * MODE_COLOUR为彩光模式；
     * MODE_SCENE为情景模式；
     */
    public LightMode workMode;

    /**
     * 亮度百分比，从0到100
     */
    public int brightness;

    /**
     * 色温百分比，从0到100
     */
    public int colorTemperature;

    /**
     * 颜色值，HSV色彩空间.
     * <p>
     * 其中H为色调，取值范围0-360；
     * 其中S为饱和度，取值范围0-100；
     * 其中V为明度，取值范围0-100；
     */
    public LightColourData colorHSV;
    /**
     * 彩灯情景。
     *
     * SCENE_GOODNIGHT为晚安情景；
     * SCENE_WORK为工作情景；
     * SCENE_READ为阅读情景；
     * SCENE_CASUAL为休闲情景；
     */
    public LightScene scene;
}
```

### 获取当前灯的类型

灯共分为一路灯（仅有白光）、二路灯（白光+冷暖控制）、三路灯（仅有彩光模式）、四路灯（白光+彩光）、五路灯（白光+彩光+冷暖）。

这5种灯具在功能定义上有所区别，在开发相应的UI和控制时有所区别。

该方法可获取当前灯的类型。

```java
/**
 * 获取当前是几路灯
 *
 * @return {@link LightType}
 */
LightType lightType();
```

其中LightType中定义的类型有：

```java
/**
 * 白光灯，dpCode：bright_value
 */
TYPE_1,
/**
 * 白光+冷暖，dpCode：bright_value + temp_value
 */
TYPE_2,
/**
 * RGB，dpCode：colour_data
 */
TYPE_3,
/**
 * 白光+RGB，dpCode：bright_value + colour_data
 */
TYPE_4,
/**
 * 白光+冷暖+RGB，dpCode：bright_value + temp_value + colour_data
 */
TYPE_5
```


### 获取当前设备所有功能的值

打开一个设备面板时，需要获取所有功能点值来展示。可通过此接口获取上面提到的LightDataPoint对象。

```java
/**
 * 获取灯所有功能点的值
 */
LightDataPoint getLightDataPoint();
```

### 开关

控制灯的开关

**方法说明**

```java
/**
 * 开灯 or 关灯
 *
 * @param status         true or false
 * @param resultCallback callback
 */
void powerSwitch(boolean status, IResultCallback resultCallback);
```
**参数说明**

|字段|含义|
|---|---|
| status |true为开|
| resultCallback |仅表示此次发送指令成功or失败，真正控制成功需要识别ILightListener中的dp变化|

### 工作模式
控制工作模式的切换。

**方法说明**

```java
/**
 * 切换工作模式
 *
 * @param mode           工作模式
 * @param resultCallback callback
 */
void workMode(LightMode mode, IResultCallback resultCallback);
```

**参数说明**

|字段|含义|
|---|---|
| mode |工作模式，其值有MODE_WHITE（白光）, MODE_COLOUR（彩光）, MODE_SCENE（情景模式）|
| resultCallback |仅表示此次发送指令成功or失败，真正控制成功需要识别ILightListener中的dp变化|

**调用示例**

如切换到彩光模式：

```java
lightDevice.workMode(LightMode.MODE_COLOUR, new IResultCallback() {
    @Override
    public void onError(String code, String error) {
        Log.i("test_light", "workMode onError:" + code + error);
    }

    @Override
    public void onSuccess() {
        Log.i("test_light", "workMode onSuccess");
    }
});
```

注意：部分灯具必须切换到对应的工作模式才可以控制，比如控制彩光，必须先切换到彩光模式才可以发颜色的值。

### 亮度

控制亮度

**方法说明**

```java
/**
 * 亮度控制。
 *
 * @param status         亮度的百分比，取值范围0-100
 * @param resultCallback callback
 */
void brightness(int status, IResultCallback resultCallback);
```

**参数说明**

|字段|含义|
|---|---|
| status |亮度的百分比|
| resultCallback |仅表示此次发送指令成功or失败，真正控制成功需要识别ILightListener中的dp变化|
### 冷暖

控制灯的冷暖值

**方法说明**

```java
/**
 * 色温控制
 *
 * @param status         色温的百分比，取值范围0-100
 * @param resultCallback callback
 */
void colorTemperature(int status, IResultCallback resultCallback);
```
**参数说明**

|字段|含义|
|---|---|
| status |色温的百分比|
| resultCallback |仅表示此次发送指令成功or失败，真正控制成功需要识别ILightListener中的dp变化|


### 彩光
控制彩色灯的颜色

**方法说明**

```java
/**
 * 设置彩灯的颜色
 *
 * @param hue            色调 （范围：0-360）
 * @param saturation     饱和度（范围：0-100）
 * @param value          明度（范围：0-100）
 * @param resultCallback callback
 */
void colorHSV(int hue, int saturation, int value, IResultCallback resultCallback);
```


### 情景

切换彩灯的情景模式，目前共有四种模式：

```java
LightScene.SCENE_GOODNIGHT为晚安情景；
LightScene.SCENE_WORK为工作情景；
LightScene.SCENE_READ为阅读情景；
LightScene.SCENE_CASUAL为休闲情景；
```

**方法说明**

```java
/**
 * @param lightScene     {@link LightScene}
 * @param resultCallback callback
 */
void scene(LightScene lightScene, IResultCallback resultCallback);
```

### 附录：灯具所有标准功能点

|code|名称|数据类型|取值描述|说明|
|--- |--- |--- |--- |--- |
|switch_led|开关|Boolean|{}||
|work_mode|模式|Enum|{"range":["white","colour","scene","music","scene_1","scene_2","scene_3","scene_4"]}||
|bright_value|亮度|Integer|{"min":25,"scale":0,"unit":"","max":255,"step":1}||
|bright_value_v2|亮度|Integer|{"min":10,"scale":0,"unit":"","max":1000,"step":1}||
|temp_value|冷暖|Integer|{"min":0,"scale":0,"unit":"","max":255,"step":1}||
|temp_value_v2|冷暖|Integer|{"min":0,"scale":0,"unit":"","max":1000,"step":1}||
|colour_data|彩光模式数|Json|{}||
|colour_data_v2|彩光模式数|Json|{}||
|scene_data|情景模式数|Json|{}||
|scene_data_v2|情景模式数|Json|{}||
|flash_scene_1|柔光模式|Json|{}||
|flash_scene_2|缤纷模式|Json|{}||
|flash_scene_3|炫彩模式|Json|{}||
|flash_scene_4|斑斓模式|Json|{}||
|music_data|音乐灯模式控制|Json|{}||
|control_data|调节dp控制|Json|{}||
|countdown_1|倒计时|Integer|{"unit":"","min":0,"max":86400,"scale":0,"step":1}||
|scene_select|场景选择|Enum|{"range":["1","2","3","4","5"]}|老版本|
|switch_health_read|健康阅读开关|Boolean|{}|老版本|
|read_time|健康阅读-阅读时间设置|Integer|{"unit":"minute","min":1,"max":60,"scale":0,"step":1}|老版本|
|rest_time|健康阅读-休息时间设置|Integer|{"unit":"minute","min":1,"max":60,"scale":0,"step":1}|老版本|
