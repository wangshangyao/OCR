package com.bh.ocr.utils;

import java.util.Map;

/**
 * Created by 2 on 2018/3/16.
 */

public interface IModel {
    String doPost(String s, Map<String,Object> map,myCallBack m);
    interface myCallBack{
        String getCall(String s);
    }
}
