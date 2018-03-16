package com.bh.ocr.presenter;

import com.bh.ocr.activuty.IView;
import com.bh.ocr.utils.IModel;
import com.bh.ocr.utils.Utils;

import java.util.Map;

public class Presenter {
    private IView iv;
    private IModel im;

    public Presenter(IView iv) {
        this.iv = iv;
        this.im = new Utils();
    }

    public void connect(String url, Map<String,Object> map){
        im.doPost(url, map, new IModel.myCallBack() {
            @Override
            public String getCall(String s) {
                iv.showView(s);
                return s;
            }
        });
    }
}
