package com.maochunjie.mpush;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Process;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.xiaomi.channel.commonutils.logger.LoggerInterface;
import com.xiaomi.mipush.sdk.Logger;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

public class MpushMixModule extends ReactContextBaseJavaModule {
  private final String appId;
  private final String appKey;
  private final String TAG = "MpushMixModule";
  public static ReactApplicationContext reactContext;

  public MpushMixModule(ReactApplicationContext reactApplicationContext) {
    super(reactContext);
    reactContext = reactApplicationContext;
    ApplicationInfo appInfo = null;
    try {
      appInfo = reactContext.getPackageManager().getApplicationInfo(reactContext.getPackageName(), PackageManager.GET_META_DATA);
    } catch (PackageManager.NameNotFoundException e) {
      throw new Error(e);
    }
    if (!appInfo.metaData.containsKey("PUSH_XIAOMI_APPID")) {
      throw new Error("meta-data PUSH_XIAOMI_APPID not found in AndroidManifest.xml");
    }
    if (!appInfo.metaData.containsKey("PUSH_XIAOMI_APPKEY")) {
      throw new Error("meta-data PUSH_XIAOMI_APPKEY not found in AndroidManifest.xml");
    }
    this.appId = appInfo.metaData.get("PUSH_XIAOMI_APPID").toString().substring(3);
    this.appKey = appInfo.metaData.get("PUSH_XIAOMI_APPKEY").toString().substring(3);
  }

  @ReactMethod
  public String getName() {
    return "MpushMixModule";
  }

  @ReactMethod
  public void initSDK() {
    try {
      if (shouldInit(reactContext)) {
        MiPushClient.registerPush(reactContext, this.appId, this.appKey);
      }
      //打开Log
      LoggerInterface newLogger = new LoggerInterface() {
        @Override
        public void setTag(String tag) {
          // ignore
        }

        @Override
        public void log(String content, Throwable t) {
          Log.d(TAG, content, t);
        }

        @Override
        public void log(String content) {
          Log.d(TAG, content);
        }
      };
      Logger.setLogger(reactContext, newLogger);
    } catch (Exception e) {
      e.printStackTrace();
    }

    //冷启动的时候，这个时候js还未准备监听，所以init的时候触发下
    if (MpushMixPackage.sMiPushMessage != null) {
      Helper.sendEvent(Constants.NOTIFICATION_EVENT, MIPushHelper.parsePushMessage(MpushMixPackage.sMiPushMessage));
      MpushMixPackage.sMiPushMessage = null;
    }
  }

  @ReactMethod
  public void setAlias(ReadableMap readableMap) {
    if (readableMap == null) {
      return;
    }
    String alias = readableMap.getString(Constants.ALIAS);
    MiPushClient.setAlias(reactContext, alias, null);
  }

  @ReactMethod
  public void unsetAlias(ReadableMap readableMap) {
    if (readableMap == null) {
      return;
    }
    String alias = readableMap.getString(Constants.ALIAS);
    MiPushClient.unsetAlias(reactContext, alias, null);
  }

  @ReactMethod
  public void subscribe(ReadableMap readableMap) {
    if (readableMap == null) {
      return;
    }
    String tag = readableMap.getString(Constants.TAG);
    MiPushClient.subscribe(reactContext, tag, null);
  }

  @ReactMethod
  public void unsubscribe(ReadableMap readableMap) {
    if (readableMap == null) {
      return;
    }
    String tag = readableMap.getString(Constants.TAG);
    MiPushClient.unsubscribe(reactContext, tag, null);
  }

  @ReactMethod
  public void setAccount(ReadableMap readableMap) {
    if (readableMap == null) {
      return;
    }
    String account = readableMap.getString(Constants.TAG);
    MiPushClient.setUserAccount(reactContext, account, null);
  }

  @ReactMethod
  public void unsetAccount(ReadableMap readableMap) {
    if (readableMap == null) {
      return;
    }
    String account = readableMap.getString(Constants.ACCOUNT);
    MiPushClient.unsetUserAccount(reactContext, account, null);
  }

  @ReactMethod
  public void getInitialNotification(Promise promise) {
    promise.resolve(MIPushHelper.parsePushMessage(MpushMixPackage.sMiPushMessage));
    MpushMixPackage.sMiPushMessage = null;
  }

  @ReactMethod
  public void clearNotification(int id) {
    MiPushClient.clearNotification(reactContext, id);
  }

  @ReactMethod
  public void clearAllNotification() {
    MiPushClient.clearNotification(reactContext);
  }

  /**
   * 判断是否可以初始化
   *
   * @param context
   * @return
   */
  private boolean shouldInit(Context context) {
    ActivityManager am = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
    List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
    String mainProcessName = context.getPackageName();
    int myPid = Process.myPid();
    for (ActivityManager.RunningAppProcessInfo info : processInfos) {
      if (info.pid == myPid && mainProcessName.equals(info.processName)) {
        return true;
      }
    }
    return false;
  }
}
