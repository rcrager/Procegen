package com.example.s231230.procegen.settings.colorPicker;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.example.s231230.procegen.MainActivity;

/**
 * Created by s231230 on 12/20/17.
 * Color Picker that pops up when user
 * chooses a color. Allows user to choose
 * color rgb and alpha from circular UI
 * design.
 */

public class ColorPickerView extends View {
    Shape color;
    int currentColor;
    float[] hsvCurrentColor;
    int alpha = 255;
    int pickCenterX = 550;
    int pickCenterY = 550;
    int hueSize = 150;
    int sliderSize = 75;
    int radius = 300;
    float saturation = 1.0f;
    float value = 1.0f;
    Canvas canvas;

    public ColorPickerView(Context context) {
        super(context);
        init();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColorPickerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        canvas = new Canvas();
        color = new Shape() {
            @Override
            public void draw(Canvas canvas, Paint paint) {
                paint.setColor(Color.GRAY);
                canvas.drawCircle(0, 0, 160, paint);
            }
        };
//        currentColor = Color.valueOf(MainActivity.settings.getInt("tempCurrentColor", 0)); //api lvl 26+
        hsvCurrentColor = new float[]{0,1,1};
        currentColor = MainActivity.settings.getInt("tempCurrentColor", Color.HSVToColor(hsvCurrentColor)); //api lvl 26-
        if (MainActivity.settings.contains("tempCurrentColor")) {
            System.out.println("contains temp current color");
        }

        System.out.println("Current Color = " + currentColor);

        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent e) {
                double dist = Math.sqrt(Math.pow(pickCenterX - e.getX(), 2) + Math.pow(pickCenterY - e.getY(), 2));
                double angleClick = Math.toDegrees(Math.atan((e.getY() - pickCenterY) / (e.getX() - pickCenterX)));
                double xVal = e.getX() - pickCenterX;
                double yVal = e.getY() - pickCenterY;
                if (angleClick < 0) {
                    angleClick = Math.abs(angleClick);
                    angleClick -= 90;
                    angleClick -= 270;
                }
                if (xVal < 0 && yVal > 0) {
                    angleClick += 180;
                } else if (xVal < 0) {
                    angleClick += 180;
                }
                if (dist <= radius + hueSize && dist >= radius) {
                    Log.i("touchEventE", "Inside Hue Circle");
                    //if user clicks inside hue circle
                    angleClick = Math.abs(angleClick);
                    hsvCurrentColor[0] = (float) angleClick;
                    //get color user clicked on
                    Log.i("touchEvent Color", "Hue value = " + angleClick);
                    currentColor = Color.HSVToColor(alpha,hsvCurrentColor);
                    MainActivity.settings.edit().putInt("tempCurrentColor", currentColor).apply();
                    invalidate();//call the onDraw method basically
                    return true;
                }
//                if(user inside inner ring)
                if (dist <= radius && dist >= radius - sliderSize) {
                    Log.i("touchEventE","inside inner sector: angle="+angleClick);
                    if(Math.abs(angleClick)>=30 && Math.abs(angleClick)<=150){
//                        if(inside alpha sector)
                        int alpAng = (int)(Math.abs(angleClick)-30);
                        alpha = (int)(alpAng*2.125);
                        Log.i("touchEventE","alpha = " + alpha);
                        Log.i("touchEventE","sat = " + saturation);
                        Log.i("touchEventE","val = " + value);
                        currentColor = Color.HSVToColor(alpha,hsvCurrentColor);
                        MainActivity.settings.edit().putInt("tempCurrentColor", currentColor).apply();
                        invalidate();//call the onDraw method bascially
                        return true;//exit method
                    }
                    if(Math.abs(angleClick)>=150 && Math.abs(angleClick)<=270){
//                        if(inside saturation sector)
                        int satAng = (int)(Math.abs(angleClick)-150);
                        float sat = (satAng/120.0f);
                        hsvCurrentColor[1] = sat;
                        saturation = sat;
                        Log.i("touchEventE","alpha = " + alpha);
                        Log.i("touchEventE","sat = " + hsvCurrentColor[1]);
                        Log.i("touchEventE","val = " + hsvCurrentColor[2]);
                        currentColor = Color.HSVToColor(alpha, hsvCurrentColor);
                        MainActivity.settings.edit().putInt("tempCurrentColor", currentColor).apply();
                        invalidate();//call the onDraw method bascially
                        return true;//exit method
                    }
                    if((angleClick<=-270 && angleClick>=-360) || angleClick<=30 && angleClick>=0){
//                        if(inside value sector)
                        int valAng = (int)Math.abs(angleClick)-270;
                        if(angleClick<=30 && angleClick>=0){
                            valAng = (int)angleClick+90;
                        }
                        float val = (valAng/120.0f);
                        hsvCurrentColor[2] = val;
                        value = val;
                        Log.i("touchEventE","alpha = " + alpha);
                        Log.i("touchEventE","sat = " + hsvCurrentColor[1]);
                        Log.i("touchEventE","val = " + hsvCurrentColor[2]);
                        float[] hsvTemp = new float[3];
                        hsvTemp[0] = hsvCurrentColor[0];
                        hsvTemp[1] = hsvCurrentColor[1];
                        hsvTemp[2] = val;
                        currentColor = Color.HSVToColor(alpha, hsvTemp);
                        MainActivity.settings.edit().putInt("tempCurrentColor", currentColor).apply();
                        invalidate();//call the onDraw method bascially
                        return true;//exit method
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onDraw(Canvas canvas) {
        //draw slider at right
        double ratio = (double)this.getHeight()/(double)this.getWidth();
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        double density = metrics.densityDpi;
        hueSize = (int)(density/ratio/1.5);
        sliderSize = (int)((density/ratio)/3);
        radius = this.getWidth()/2-(int)(hueSize+(density/5));
        pickCenterX = (this.getWidth()/2);
        pickCenterY = this.getHeight()/2-hueSize/2;
        this.canvas = canvas;
        canvas.drawColor(Color.GRAY);
        drawPicker(canvas);
        drawCurrentColor(canvas, new Paint());
    }

    private void drawCurrentColor(Canvas canvas, Paint paint) {
        paint.setColor(currentColor);
        canvas.drawCircle(0,0, hueSize, paint);
    }

    private void drawPicker(Canvas canvas) {
        float[] hsvColors = {0, 1, 1};
        final Paint p = new Paint();
        int prevRot = 0;
        canvas.translate(pickCenterX, pickCenterY);
        for (int hsv = 0; hsv <= 360; hsv++) {
            p.setColor(Color.HSVToColor(hsvColors));
            //hsv also = degrees of rotation
            canvas.drawRect(radius, 0, radius + hueSize, 10, p);
            canvas.rotate(hsv - prevRot);
            hsvColors[0] = hsv;
            prevRot = hsv;
        }
        Color.colorToHSV(currentColor, hsvColors);
        for (int alpha = 0; alpha <= 255; alpha++) {
            p.setColor(Color.HSVToColor(alpha, hsvColors));
            canvas.drawRect(radius, 0, radius - sliderSize, 15, p);
            int temp = (int) ((alpha / 2.125)+30);
            canvas.rotate(temp - prevRot);
            prevRot = temp;
        }
        for (int sat = 0; sat <= 100; sat++) {
            p.setColor(Color.HSVToColor(hsvColors));
            hsvColors[1] = sat / 100f;
            canvas.drawRect(radius, 0, radius - sliderSize, 15, p);
            int temp = (int) ((sat * 1.2)+150); //offset of angle to draw slider at
            canvas.rotate(temp - prevRot);
//            saturation = sat;
            prevRot = temp;
        }
        Color.colorToHSV(currentColor, hsvColors);
        for (int val = 0; val <= 100; val++) {
            p.setColor(Color.HSVToColor(hsvColors));
            hsvColors[2] = val / 100f;
            canvas.drawRect(radius, 0, radius - sliderSize, 15, p);
            int temp = (int) ((val * 1.2) -90); //offset of angle to draw slider at
            canvas.rotate(temp - prevRot);
//            value = val;
            prevRot = temp;
        }

    }
}
