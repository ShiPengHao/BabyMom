package com.yimeng.babymom.utils;

/**
 * 常量
 */
public class MyConstant {
    /**
     * 账号、登陆有关的本地存储sp文件名称
     */
    public static final String PREFS_ACCOUNT = "PREFS_ACCOUNT";
    /**
     * 首次运行的键
     */
    public static final String KEY_ACCOUNT_FIRSTRUNNING = "KEY_ACCOUNT_FIRSTRUNNING";
    /**
     * 自动更新的键
     */
    public static final String KEY_ACCOUNT_AUTOUPDATE = "KEY_ACCOUNT_AUTOUPDATE";
    /**
     * 自动登陆的键
     */
    public static final String KEY_ACCOUNT_AUTOLOGIN = "KEY_ACCOUNT_AUTOLOGIN";
    /**
     * 上次登录成功的用户名的键
     */
    public static final String KEY_ACCOUNT_LAST_USERNAME = "KEY_ACCOUNT_LAST_USERNAME";
    /**
     * 上次登录成功的用户的密码的键
     */
    public static final String KEY_ACCOUNT_LAST_PASSWORD = "KEY_ACCOUNT_LAST_PASSWORD";
    /**
     * 上次登录成功的用户名的是否记住密码的键
     */
    public static final String KEY_ACCOUNT_LAST_REMEMBER = "KEY_ACCOUNT_LAST_REMEMBER";
    /**
     * 上次登录成功的用户的id的键
     */
    public static final String KEY_ACCOUNT_LAST_ID = "KEY_ACCOUNT_LAST_ID";
    /**
     * 上次登录成功的用户的类型的键
     */
    public static final String KEY_ACCOUNT_LAST_TYPE = "KEY_ACCOUNT_LAST_TYPE";

    /**
     * webservice命名空间
     */
    public static final String NAMESPACE = "http://s.hyzczg.com/";//http://192.168.0.108:888/";
    /**
     * webserviceUrl
     */
    public static final String WEB_SERVICE_URL = NAMESPACE + "API/ymOR_WebService.asmx";

    /**
     * 请求码
     */
    public static final String REQUEST_CODE = "REQUEST_CODE";
    /**
     * android
     */
    public static final String ANDROID = "android";
    /**
     * url合法格式
     */
    public static final String URL_PATTERN =
            "^((https|http|ftp|rtsp|mms)?://)"//ftp的user@
                    // IP形式的URL- 199.194.52.184
                    // 允许IP和DOMAIN（域名）
                    // 域名- www.
                    // 二级域名
                    // first level domain- .com or .museum
                    // 端口- :80
                    + "?(([0-9a-zA-Z_!~*'().&=+$%-]+: )?[0-9a-zA-Z_!~*'().&=+$%-]+@)?" //ftp的user@
                    + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP形式的URL- 199.194.52.184
                    + "|" // 允许IP和DOMAIN（域名）
                    + "([0-9a-zA-Z_!~*'()-]+\\.)*" // 域名- www.
                    + "([0-9a-zA-Z][0-9a-zA-Z-]{0,61})?[0-9a-zA-Z]\\." // 二级域名
                    + "[a-zA-Z]{2,6})" // first level domain- .com or .museum
                    + "(:[0-9]{1,4})?" // 端口- :80
                    + "((/?)|"
                    + "(/[0-9a-zA-Z_!~*'().;?:@&=+$,%#-]+)+/?)$";
}
