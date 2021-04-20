package com.maochunjie.mpush;

import android.content.Context;
import android.content.Intent;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.WritableMap;
import com.xiaomi.mipush.sdk.ErrorCode;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;
import com.xiaomi.mipush.sdk.PushMessageReceiver;

import java.util.List;

public class MIPushReceiver extends PushMessageReceiver {
  private String mRegId;
  private String mTopic;
  private String mAlias;
  private String mUserAccount;


  /**
   * 接受服务端发送过来的透传消息
   *
   * @param context
   * @param miPushMessage
   */
  @Override
  public void onReceivePassThroughMessage(Context context, MiPushMessage miPushMessage) {
    super.onReceivePassThroughMessage(context, miPushMessage);
    Helper.sendEvent(Constants.CUSTOM_MESSAGE_EVENT, MIPushHelper.parsePushMessage(miPushMessage));
  }

  /**
   * 监听用户点击通知栏消息
   *
   * @param context
   * @param miPushMessage
   */
  @Override
  public void onNotificationMessageClicked(Context context, MiPushMessage miPushMessage) {
    super.onNotificationMessageClicked(context, miPushMessage);

    Intent intent = new Intent();
    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    if (MpushMixPackage.sReactContext == null) {
      // 冷启动，先将数据保存，然后在init的时候触发
      MpushMixPackage.sMiPushMessage = miPushMessage;

      String packageName = context.getApplicationContext().getPackageName();
      Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
      launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
      context.startActivity(launchIntent);
      return;
    }

    if (MpushMixPackage.sReactContext.getCurrentActivity() != null) {
      intent.setClass(context, MpushMixPackage.sReactContext.getCurrentActivity().getClass());
      context.startActivity(intent);
    } else {
      String packageName = context.getApplicationContext().getPackageName();
      Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
      context.startActivity(launchIntent);
    }
    Helper.sendEvent(Constants.NOTIFICATION_EVENT, MIPushHelper.parsePushMessage(miPushMessage));
  }

  /**
   * 消息达到客户端触发
   *
   * @param context
   * @param miPushMessage
   */
  @Override
  public void onNotificationMessageArrived(Context context, MiPushMessage miPushMessage) {
    super.onNotificationMessageArrived(context, miPushMessage);
    Helper.sendEvent(Constants.NOTIFICATION_ARRIVED_EVENT, MIPushHelper.parsePushMessage(miPushMessage));
  }

  @Override
  public void onCommandResult(Context context, MiPushCommandMessage message) {
    String command = message.getCommand();
    List<String> arguments = message.getCommandArguments();
    String cmdArg1 = ((arguments != null && arguments.size() > 0) ? arguments.get(0) : null);
    String cmdArg2 = ((arguments != null && arguments.size() > 1) ? arguments.get(1) : null);
    if (MiPushClient.COMMAND_REGISTER.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mRegId = cmdArg1;
      }
      WritableMap writableMap = Arguments.createMap();
      writableMap.putString(Constants.REGISTRATION_ID, mRegId);
      Helper.sendEvent(Constants.CONNECT_EVENT, writableMap);
    } else if (MiPushClient.COMMAND_SET_ALIAS.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mAlias = cmdArg1;
      }
      WritableMap writableMap = Arguments.createMap();
      writableMap.putString(Constants.ACTION, Constants.SET_ACTION);
      writableMap.putString(Constants.ALIAS, mAlias);
      Helper.sendEvent(Constants.ALIAS_EVENT, writableMap);

    } else if (MiPushClient.COMMAND_UNSET_ALIAS.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mAlias = cmdArg1;
      }
      WritableMap writableMap = Arguments.createMap();
      writableMap.putString(Constants.ACTION, Constants.UNSET_ACTION);
      writableMap.putString(Constants.ALIAS, mAlias);
      Helper.sendEvent(Constants.ALIAS_EVENT, writableMap);
    } else if (MiPushClient.COMMAND_SUBSCRIBE_TOPIC.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mTopic = cmdArg1;
      }
      WritableMap writableMap = Arguments.createMap();
      writableMap.putString(Constants.ACTION, Constants.SET_ACTION);
      writableMap.putString(Constants.TAG, mTopic);
      Helper.sendEvent(Constants.TAG_EVENT, writableMap);
    } else if (MiPushClient.COMMAND_UNSUBSCRIBE_TOPIC.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mTopic = cmdArg1;
      }
      WritableMap writableMap = Arguments.createMap();
      writableMap.putString(Constants.ACTION, Constants.UNSET_ACTION);
      writableMap.putString(Constants.TAG, mTopic);
      Helper.sendEvent(Constants.TAG_EVENT, writableMap);
    } else if (MiPushClient.COMMAND_SET_ACCOUNT.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mUserAccount = cmdArg1;
      }
      WritableMap writableMap = Arguments.createMap();
      writableMap.putString(Constants.ACTION, Constants.SET_ACTION);
      writableMap.putString(Constants.ACCOUNT, mUserAccount);
      Helper.sendEvent(Constants.ACCOUNT_EVENT, writableMap);
    } else if (MiPushClient.COMMAND_UNSET_ACCOUNT.equals(command)) {
      if (message.getResultCode() == ErrorCode.SUCCESS) {
        mUserAccount = cmdArg1;
      }
      WritableMap writableMap = Arguments.createMap();
      writableMap.putString(Constants.ACTION, Constants.UNSET_ACTION);
      writableMap.putString(Constants.ACCOUNT, mUserAccount);
      Helper.sendEvent(Constants.ACCOUNT_EVENT, writableMap);
    }
  }
}
