package com.maochunjie.mpush;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.xiaomi.mipush.sdk.MiPushMessage;

import java.util.Map;

public class MIPushHelper {
  public static WritableMap parsePushMessage(MiPushMessage miPushMessage) {
    if (miPushMessage == null) {
      return null;
    }
    try {
      WritableMap param = Arguments.createMap();
      param.putString("title", miPushMessage.getTitle());
      param.putString("description", miPushMessage.getDescription());
      param.putString("content", miPushMessage.getContent());
      convertExtras(miPushMessage.getExtra(), param);
      param.putString("category", miPushMessage.getCategory());
      param.putInt("notifyId", miPushMessage.getNotifyId());
      param.putInt("notifyType", miPushMessage.getNotifyType());
      return param;
    } catch (Exception e) {
      return null;
    }
  }

  public static void convertExtras(Map<String, String> extras, WritableMap writableMap) {
    WritableMap extrasMap = Arguments.createMap();
    for (String key : extras.keySet()) {
      String value = extras.get(key);
      extrasMap.putString(key, value);
    }
    writableMap.putMap("extra", extrasMap);
  }
}
