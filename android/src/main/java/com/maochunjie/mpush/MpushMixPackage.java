package com.maochunjie.mpush;

import com.facebook.react.ReactPackage;
import com.facebook.react.bridge.JavaScriptModule;
import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ViewManager;
import com.xiaomi.mipush.sdk.MiPushMessage;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class MpushMixPackage implements ReactPackage {

  public static ReactApplicationContext sReactContext;
  public static MiPushMessage sMiPushMessage = null;

  @Override
  public List<NativeModule> createNativeModules(ReactApplicationContext reactContext) {
    sReactContext = reactContext;
    return Arrays.<NativeModule>asList(new MpushMixModule(reactContext));
  }

  // Deprecated from RN 0.47
  public List<Class<? extends JavaScriptModule>> createJSModules() {
    return Collections.emptyList();
  }

  @Override
  public List<ViewManager> createViewManagers(ReactApplicationContext reactContext) {
    return Collections.emptyList();
  }
}
