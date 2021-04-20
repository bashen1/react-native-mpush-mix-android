# react-native-mpush-mix

小米推送

## 集成

```sh
npm install react-native-mpush-mix --save

cd ios && pod install && cd -
```

### Android

1. `android/app/build.gradle`

```
....
manifestPlaceholders = [
    ....
    PUSH_XIAOMI_APPID : "MI-222222222222222222",
    PUSH_XIAOMI_APPKEY: "MI-22222222222"
    ....
]
....

```

### iOS （目前不行，这边只做暂时记录）

1. `Info.plist`，MiSDKRun测试用Debug，正式用Online，生成的注册ID是不同的

```
<dict>
	<key>MiSDKAppID</key>
	<string>1000888</string>
	<key>MiSDKAppKey</key>
	<string>500088888888</string>
	<key>MiSDKRun</key>
	<string>Online</string>
</dict>

```

2. `AppDelegate.h`

增加MiPushSDKDelegate与UNUserNotificationCenterDelegate代理

```c
#import "MiPushSDK.h"

@interface AppDelegate : UIResponder <UIApplicationDelegate, MiPushSDKDelegate, UNUserNotificationCenterDelegate, RCTBridgeDelegate>

```


3. AppDelegate.m

```c
...
#import "MpushMix.h"
...

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
	...
  //推送
  [MiPushSDK registerMiPush:self type:0 connect:YES];
  //推送
  ...
}
...

//推送
- (void)application:(UIApplication *)application didRegisterUserNotificationSettings:(UIUserNotificationSettings *)notificationSettings
{

  [MpushMix application:application didRegisterUserNotificationSettings:notificationSettings];
}

- (void)application:(UIApplication *)application didRegisterForRemoteNotificationsWithDeviceToken:(NSData *)deviceToken
{
  [MpushMix application:application didRegisterForRemoteNotificationsWithDeviceToken:deviceToken];
}

- (void)application:(UIApplication *)application didReceiveRemoteNotification:(NSDictionary *)notification
{
  [MpushMix application:application didReceiveRemoteNotification:notification];
}

- (void)application:(UIApplication *)application didReceiveLocalNotification:(UILocalNotification *)notification {
  [MpushMix application:application didReceiveLocalNotification:notification];
}

// ios 10
// 应用在前台收到通知
- (void)userNotificationCenter:(UNUserNotificationCenter *)center willPresentNotification:(UNNotification *)notification withCompletionHandler:(void (^)(UNNotificationPresentationOptions))completionHandler {
  [MpushMix userNotificationCenter:center willPresentNotification:notification withCompletionHandler:completionHandler];
}

// 点击通知进入应用
- (void)userNotificationCenter:(UNUserNotificationCenter *)center didReceiveNotificationResponse:(UNNotificationResponse *)response withCompletionHandler:(void (^)())completionHandler {
  [MpushMix userNotificationCenter:center didReceiveNotificationResponse:response withCompletionHandler:completionHandler];
  completionHandler();
}
//推送

```


## 使用

```js
import PushMix, {PushMixEvent} from 'react-native-mpush-mix';

//初始化
PushMix.init();

//设置别名
PushMix.setAlias({
	alias: ''
})

//注销别名
PushMix.unsetAlias({
	alias: ''
})

//设置主题
PushMix.subscribe({
	tag: ''
})

//注销主题
PushMix.unsubscribe({
	tag: ''
})

//设置账号,一个账号需要多台设备接收通知
PushMix.setAccount({
	account: ''
})

//注销账号
PushMix.unsetAccount({
	account: ''
})

//添加监听
PushMix.addEventListener(PushMixEvent.CONNECT_EVENT, ()=>{

})

//移除监听，组件卸载的时候需要移除
PushMix.removeEventListener(PushMixEvent.CONNECT_EVENT)

//清除指定通知
PushMix.clearNotification(notifyId)

//清除所有通知
PushMix.clearNotifications()

//设置角标,仅支持ios
PushMix.setBadge(2)
// ...
```

## License

MIT
