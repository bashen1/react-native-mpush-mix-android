import {
  NativeModules,
  Platform,
  NativeEventEmitter,
  PermissionsAndroid, DeviceEventEmitter,
} from 'react-native';

const {MpushMixModule} = NativeModules;

let listeners = {}

class PushMix {
  /**
   * 初始化
   */
  static init = () => {
    if (Platform.OS == 'android') {
      PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE).then((state) => {
        if (state) {
          PermissionsAndroid.check(PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE).then((state) => {
            if (state) {
              MpushMixModule.initSDK();
            } else {
              PermissionsAndroid.request(PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE).then((granted) => {
                if (granted === PermissionsAndroid.RESULTS.GRANTED) {
                  MpushMixModule.initSDK();
                }
              });
            }
          });
        } else {
          PermissionsAndroid.requestMultiple([
            PermissionsAndroid.PERMISSIONS.READ_PHONE_STATE,
            PermissionsAndroid.PERMISSIONS.WRITE_EXTERNAL_STORAGE,
          ]).then((granted) => {
            // if (granted === PermissionsAndroid.RESULTS.GRANTED) {
            MpushMixModule.initSDK();
            // }
          });
        }
      });
    }else{
      MpushMixModule.initSDK();
    }
  }


  /**
   * 设置别名
   * @param param
   * {alias: ''}
   */
  static setAlias = (param) => {
    MpushMixModule.setAlias(param);
  }

  /**
   * 注销别名
   * @param param
   * {alias: ''}
   */
  static unsetAlias = (param) => {
    MpushMixModule.unsetAlias(param);
  }

  /**
   * 设置主题,类似tag
   * @param param
   * {tag: ''}
   */
  static subscribe = (param) => {
    MpushMixModule.subscribe(param);
  }

  /**
   * 注销主题
   * @param param
   * {tag: ''}
   */
  static unsubscribe = (param) => {
    MpushMixModule.unsubscribe(param);
  }

  /**
   * 设置账号,一个账号需要多台设备接收通知
   * @param text
   * {account: ''}
   */
  static setAccount = (param) => {
    MpushMixModule.setAccount(param);
  }

  /**
   * 注销账号
   * @param param
   * {account: ''}
   */
  static unsetAccount = (param) => {
    MpushMixModule.unsetAccount(param);
  }

  /**
   *
   * @param type
   * ios :
   * notification => 监听收到apns通知
   * localNotification => 监听收到本地通知
   * register => 注册deviceToken 通知
   *
   * android :
   * xmpush_notify => 监听收到推送
   * xmpush_click => 监听推送被点击
   * xmpush_message => 监听收到透传消息
   * @param handler
   */
  static addEventListener = (type, handler) => {
    listeners[type] = DeviceEventEmitter.addListener(
      type, result => {
        handler(result)
      })
  }

  static removeEventListener = (type) => {
    if (!listeners[type]) {
      return
    }
    listeners[type].remove()
    listeners[type] = null
  }

  /**
   * 清除指定通知
   * @param notifyId
   * ios : userInfo
   * android : id
   */
  static clearNotification = (notifyId) => {
    MpushMixModule.clearNotification(notifyId);
  }

  /**
   * 清除所有通知
   */
  static clearNotifications = () => {
    MpushMixModule.clearAllNotification();
  }

  /**
   * 设置角标,仅支持ios
   * @param num
   */
  static setBadge = (num) => {
    if (Platform.OS == 'ios') {
      MpushMixModule.setBadge(num);
    }
  }
}

export const PushMixEvent = {
  CONNECT_EVENT: 'ConnectEvent', //初始化
  NOTIFICATION_EVENT: 'NotificationEvent', //通知点击
  NOTIFICATION_ARRIVED_EVENT: 'NotificationArrivedEvent', //通知送达
  CUSTOM_MESSAGE_EVENT: 'CustomMessageEvent', //消息透传
  TAG_EVENT: 'TagEvent', //操作Tag
  ALIAS_EVENT: 'AliasEvent', //操作Alias
  ACCOUNT_EVENT: 'AccountEvent' //操作Account
}

export default PushMix
