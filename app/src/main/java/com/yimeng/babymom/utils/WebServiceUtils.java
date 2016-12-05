package com.yimeng.babymom.utils;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapPrimitive;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.Map;

/**
 * 用soap调用webservice方法的工具类
 */
public class WebServiceUtils {

    /**
     * 调用WebService，返回结果字符串。此方法应当异步调用。
     *
     * @param serviceUrl webservice服务器地址
     * @param nameSpace  WebService的命名空间
     * @param methodName WebService方法名
     * @param params     参数列表（map键值对形式）
     * @return WebService的返回值字符串形式，异常为null
     */
    public static String callWebService(String serviceUrl, String nameSpace, String methodName, Map<String, Object> params) {
        // 1、指定webservice的命名空间和调用的方法名
        SoapObject request = new SoapObject(nameSpace, methodName);

        // 2、设置调用方法的参数值，如果没有参数，可以省略，
        if (params != null) {
            for (Object o : params.entrySet()) {
                Map.Entry entry = (Map.Entry) o;
                request.addProperty((String) entry.getKey(), entry.getValue());
            }
        }

        // 3、生成调用Webservice方法的SOAP请求信息。该信息由SoapSerializationEnvelope对象描述
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER12);
        envelope.bodyOut = request;
        // c#写的应用程序必须加上这句
        envelope.dotNet = true;
        HttpTransportSE ht = new HttpTransportSE(serviceUrl);
        // 使用call方法调用WebService方法
        try {
            ht.call(null, envelope);
            final SoapPrimitive result = (SoapPrimitive) envelope.getResponse();
            if (result != null) {
                MyLog.i("webservice", result.toString());
                return result.toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
