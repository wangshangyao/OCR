package com.bh.ocr.activuty;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bh.ocr.R;
import com.bh.ocr.api.Api;
import com.bh.ocr.event.DataEvent;
import com.bh.ocr.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
 * MainActivity
 * 选取照片 上传
 * */
public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ImageView id_z;
    private ImageView id_f;

    private int ID_Z_CODE = 0;
    private int ID_F_CODE = 1;
    private TextView id_z_t;
    private TextView id_f_t;

    private String img1 = "";
    private String img2 = "";
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(DataEvent data){
        if(data.getType() == ID_Z_CODE){
            img1 = data.getImg();
            id_z.setImageBitmap(Utils.base64ToBitmap(img1));
            id_z_t.setVisibility(View.INVISIBLE);
        }else if(data.getType() == ID_F_CODE){
            img2 = data.getImg();
            id_f.setImageBitmap(Utils.base64ToBitmap(img2));
            id_f_t.setVisibility(View.INVISIBLE);
        }else{
            Toast.makeText(this, "拍取照片失败请重试", Toast.LENGTH_SHORT).show();
        }
    }
    //初始化控件
    private void initView() {
        id_z = findViewById(R.id.id_z);
        id_f = findViewById(R.id.id_f);
        btn = findViewById(R.id.btn);
        id_z.setOnClickListener(this);
        id_f.setOnClickListener(this);
        btn.setOnClickListener(this);

        id_z_t = findViewById(R.id.id_z_t);
        id_f_t = findViewById(R.id.id_f_t);
    }
    //点击事件
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.id_z:
                //6.0权限判断
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            10086);
                }else{
                    //不需要申请权限
                    Intent it = new Intent(this,CameraActivity.class);
                    it.putExtra("type",ID_Z_CODE);
                    startActivity(it);
                }
                break;
            case R.id.id_f:
                //6.0权限判断
                if (ContextCompat.checkSelfPermission(MainActivity.this,
                        Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{Manifest.permission.CAMERA},
                            10086);
                }else{
                    //不需要申请权限
                    Intent it2 = new Intent(this,CameraActivity.class);
                    it2.putExtra("type",ID_F_CODE);
                    startActivity(it2);
                }
                break;
            case R.id.btn:
                //获取设备的唯一标识
                String ANDROID_ID = Settings.System.getString(getContentResolver(), Settings.System.ANDROID_ID);

                //非空判断
                if(img1 != null && !img1.equals("") && img2 != null && !img2.equals("") ){
                    List list_img1 = new ArrayList();
                    list_img1.add(img1);
                    List list_img2 = new ArrayList();
                    list_img2.add(img2);
                    Map<String,Object> map = new HashMap<>();
                    map.put("type",5);
                    map.put("img1",list_img1);
                    map.put("img2",list_img2);
                    map.put("appid",ANDROID_ID);
                    Utils.doPost(Api.getCard,map);
                }else if(img1 == null || img1.equals("")){
                    Toast.makeText(MainActivity.this,"身份证正面不能为空",Toast.LENGTH_SHORT).show();
                }else if(img2 == null || img2.equals("")){
                    Toast.makeText(MainActivity.this,"身份证反面面不能为空",Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 10086: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent it = new Intent(this,CameraActivity.class);
                    it.putExtra("type",ID_Z_CODE);
                    startActivity(it);
                }else {
                    Toast.makeText(this, "打开相机失败", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }
}
