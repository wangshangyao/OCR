package com.bh.ocr.utils;

import java.util.Map;

/**
 * MVP Model层接口
 */

public interface IModel {
    String doPost(String s, Map<String,Object> map,myCallBack m);
    interface myCallBack{
        String getCall(String s);
    }
}
