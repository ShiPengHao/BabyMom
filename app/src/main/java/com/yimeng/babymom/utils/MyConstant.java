package com.yimeng.babymom.utils;

/**
 * 常量
 */
public class MyConstant {

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
     * 获得城市名称
     */
    public static final int REQUEST_CITY = 1001;
    /**
     * 获得医院信息
     */
    public static final int REQUEST_HOSPITAL = 1002;
    /**
     * 去医院页面
     */
    public static final int REQUEST_TO_HOSPITAL = 1003;
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
