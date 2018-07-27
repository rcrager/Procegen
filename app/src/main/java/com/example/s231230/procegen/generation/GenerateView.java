package com.example.s231230.procegen.generation;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.example.s231230.procegen.MainActivity;

/**
 * Created by Robert on 9/21/17.
 * Draw the current complete shape
 * This also controls most of the
 * shadow work as well.
 */

public class GenerateView extends View{
    int width = MainActivity.getScreenWidth();
    int height = MainActivity.getScreenHeight();
    Facet currentFacet;

    SharedPreferences prefs = MainActivity.settings;
//    int fAmount = prefs.getInt(prefs + ".genAmount", 1);
    int fAmount = prefs.getInt(".genAmount", 1);


    public GenerateView(Context context){
        super(context);
        generateFacet(new Canvas());
    }

    public GenerateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        generateFacet(new Canvas());
    }

    public GenerateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        generateFacet(new Canvas());
    }

    public void generateFacet(Canvas canvas){
        currentFacet = new Facet(width/2,height/2,fAmount);
        currentFacet.generateJoiningFacet(canvas);
    }

    @Override
    public void onDraw(Canvas canvas){
        super.onDraw(canvas);
        drawCurrentFacet(canvas);
    }
    public void drawCurrentFacet(Canvas canvas){
        Paint p = new Paint();
        //get first element in color palette array and use that as the bG color
        int currentPalette = prefs.getInt(".currentPaletteNum", 0);
        //get first element in current palette to set that as the BG color
        int bgCol = prefs.getInt( "." + currentPalette + ".1",0); //bc first button is bg color
        canvas.drawColor(bgCol);
        int shadowDepth = 0;
        boolean shadowBool = false;
        if(prefs.contains("shadowNum")){
            shadowDepth = prefs.getInt("shadowNum",0);
        }
        if(prefs.contains("shadowBool")){
            shadowBool = prefs.getBoolean("shadowBool",false);
        }
        if(shadowBool) {
        for(int g = 0; g<fAmount; g++){
            //draw shadow here if necessary
        Path currentPath = currentFacet.getCurrentPaths()[g];
                p.setColor(Color.DKGRAY);
                currentPath.offset(shadowDepth, shadowDepth);
                canvas.drawPath(currentPath, p);
                p.setColor(Color.DKGRAY);
                canvas.drawPath(currentPath,p);
                currentPath.offset(-shadowDepth,-shadowDepth);
            }
        }
        for(int g = 0; g<fAmount; g++){
            //draw just the shape here
            Path currentPath = currentFacet.getCurrentPaths()[g];
            p.setColor(currentFacet.getCurrentColors()[g]);
            canvas.drawPath(currentPath,p);
        }
    }
}
