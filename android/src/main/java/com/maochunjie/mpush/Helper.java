package com.maochunjie.mpush;

import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class Helper {
  public static void sendEvent(String eventName, WritableMap params) {
    MpushMixModule.reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit(eventName, params);
  }
}
