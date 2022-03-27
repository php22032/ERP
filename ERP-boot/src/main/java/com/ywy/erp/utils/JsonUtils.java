package com.ywy.erp.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

/**
 * Created by  2022-2-11 09:48:08
 *
 * @author
 */
public class JsonUtils {

    public static JSONObject ok(){
        JSONObject obj = new JSONObject();
        JSONObject tmp = new JSONObject();
        tmp.put("message", "成功");
        obj.put("code", 200);
        obj.put("data", tmp);
        return obj;
    }

}
