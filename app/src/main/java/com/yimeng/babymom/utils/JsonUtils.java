package com.yimeng.babymom.utils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * json相关工具类
 */
public class JsonUtils {
    /**
     * 解析包含JsonArray结构的数据的json对象字符串，将结果存入集合中
     *
     * @param result    json对象字符串，键data对应一个JsonArray
     * @param arrayList 存入数据的集合
     * @param clazz     javabean的字节码文件
     */
    public static <T> void parseListResult(ArrayList<T> arrayList, Class<T> clazz, String result) throws Exception {
        arrayList.clear();
        JSONArray array = new JSONObject(result).optJSONArray("data");
        for (int i = 0; i < array.length(); i++) {
            arrayList.add(new Gson().fromJson(array.optString(i), clazz));
        }
    }
}
