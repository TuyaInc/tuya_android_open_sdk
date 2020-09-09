# 灯具功能封装

## 添加依赖
```groovy
// homesdk 依赖
implementation 'com.tuya.smart:tuyasmart-tuyahomesdk:centralcontrol_v2.0.0-SNAPSHOT'
// 中控控制依赖
implementation 'com.tuya.smart:tuyasmart-centralcontrol:1.0.0-SNAPSHOT'
```

注意：homesdk依赖先使用centralcontrol_v2.0.0-SNAPSHOT版本，后续会升级成release包，开发阶段暂时使用此版本。

## 基本使用

首先，创建ITuyaLightDevice对象，灯相关的方法均封装在此方法中。

```java
ITuyaLightDevice lightDevice= new TuyaLightDevice(String devId);
```

该对象封装了灯的所有dp点，包括控制和上报。

调用方不需要关心任何dp点的值即可进行开发。

这里提供几个简单的调用示例：
    
```java
// 创建lightDevice
ITuyaLightDevice lightDevice = new TuyaLightDevice("vdevo159793004250542");

// 注册监听
lightDevice.registerLightListener(new ILightListener() {
    @Override
    public void onDpUpdate(LightDataPoint dataPoint) {
        L.i("test_light", "onDpUpdate:" + dataPoint);
    }

    @Override
    public void onRemoved() {
        L.i("test_light", "onRemoved");
    }

    @Override
    public void onStatusChanged(boolean status) {
        L.i("test_light", "onDpUpdate:" + status);
    }

    @Override
    public void onNetworkStatusChanged(boolean status) {
        L.i("test_light", "onDpUpdate:" + status);
    }

    @Override
    public void onDevInfoUpdate() {
        L.i("test_light", "onDevInfoUpdate:");
    }
});
// 开灯
lightDevice.powerSwitch(true, new IResultCallback() {
    @Override
    public void onError(String code, String error) {
        L.i("test_light", "powerSwitch onError:" + code + error);
    }

    @Override
    public void onSuccess() {
        L.i("test_light", "powerSwitch onSuccess:");
    }
});
// 晚安场景
lightDevice.scene(LightScene.SCENE_GOODNIGHT, new IResultCallback() {
    @Override
    public void onError(String code, String error) {
        L.i("test_light", "scene onError:" + code + error);
    }

    @Override
    public void onSuccess() {
        L.i("test_light", "scene onSuccess:");
    }
});
// 设置颜色
lightDevice.colorHSV(100, 100, 100, new IResultCallback() {
    @Override
    public void onError(String code, String error) {
        L.i("test_light", "colorHSV onError:" + code + error);
    }
    @Override
    public void onSuccess() {
        L.i("test_light", "colorHSV onSuccess:");
    }
});
```

更多API请参考下文：

## 注册监听

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

值得说明的是`LightDataPoint`对象，该对象封装了当前设备所有功能点。当功能点发生变化时，将会回调。每次回调的都会是完整的对象，即使只改变了一个dp点。

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

## 获取当前灯的类型

彩灯共分为一路灯、二路灯、三路灯、四路灯、五路灯。

该方法可区分当前灯的类型。

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


## 获取当前设备所有功能点

打开一个设备面板时，需要获取所有功能点值来展示，这里封装了一个接口，获取上面提到的LightDataPoint对象。

```java
/**
 * 获取灯所有功能点的值
 */
LightDataPoint getLightDataPoint();
```

## 开关

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

## 工作模式
控制工作模式的切换

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
| mode |工作模式|
| resultCallback |仅表示此次发送指令成功or失败，真正控制成功需要识别ILightListener中的dp变化|


## 亮度
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
## 冷暖

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


## 彩光
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


## 情景

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

## 附录：灯具所有标准功能点

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

注：情景实现分为v1和v2两个版本

其中，v1版本参见链接 [场景定义及默认值](https://wiki.tuya-inc.com:7799/pages/viewpage.action?pageId=43040107)

v2版本参见链接[照明公版情景默认参数定义表](https://wiki.tuya-inc.com:7799/pages/viewpage.action?pageId=25406842)
