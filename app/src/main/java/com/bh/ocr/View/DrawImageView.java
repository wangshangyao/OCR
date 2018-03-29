package com.bh.ocr.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.WindowManager;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class DrawImageView extends ImageView {

    private int width = -1;
    private int height = -1;

    public DrawImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        WindowManager wm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        width = wm.getDefaultDisplay().getWidth();
        height = wm.getDefaultDisplay().getHeight();
    }

    Paint paint = new Paint();{
        paint.setAntiAlias(true);
        paint.setColor(Color.RED);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(2.5f);//设置线宽
        paint.setAlpha(100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);
        int left = (int)(width-(width*0.7))/2;
        int top = (int)(height-(height*0.7))/2;
        int right = (int)(left+(width*0.7));
        int bottom = (int)(top+(height*0.7));
//        canvas.drawRect(new Rect(left, top, right, bottom), paint);//绘制矩形
    }
}
