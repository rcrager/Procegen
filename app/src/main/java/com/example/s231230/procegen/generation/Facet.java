package com.example.s231230.procegen.generation;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;

import com.example.s231230.procegen.MainActivity;

/**
 * Created by Robert on 10/5/17.
 * Class that creates the actual
 * shapes that connect in the
 * generation activity.
 */

public class Facet {

    SharedPreferences settings;

    int startX, startY, amount;
    int size = 540/2;
    Paint paint;
    Path[] paths;
    Path[] shadows;
    int[] colors;
    int shapeNum;

    public Facet(int startX, int startY, int amount){
        this.startX = startX;
        this.startY = startY;
        this.amount = amount;
        paths = new Path[amount];
        shadows = paths;
        colors = new int[amount];
        shapeNum = 0;
        paint = new Paint();
        settings = MainActivity.settings;
    }

    public void generateJoiningFacet(Canvas canvas){
        /*
        * generate the joining facet by taking random points together
        * **FIRST GENERATION ALL POINTS ARE RANDOM!**
        * **SECOND GENERATION*{
        * startX = previous secondX (same thing for y-coordinates)
        * secondX = previous thirdX (same thing for y-coordinates)
        * }
        * ^REPEATS ABOVE FOR n NUMBER OF SHAPES^
        * Colors all shapes picking randomly from the color palette
        * chosen by the user. If no color palette created or selected
        * picks random colors with rgb values > 100 & alpha = 155.
        */
        paint.setStyle(Paint.Style.FILL);
        Path path;
        int secondX =0, secondY=0, thirdX=0, thirdY=0;
        int[] colorPaints = new int[4];
        if(settings.contains(".currentPaletteNum")){
            int r = settings.getInt(".currentPaletteNum",0);
            for(int b = 0; b<=colorPaints.length-1; b++){ // b=1 bc skipping bg color
                colorPaints[b] = settings.getInt("."+(r)+"."+(b+2),0);//b+1 bc is num not index &b+1 again b/c skipping bg color
            }
        }
        for(int g = 0; g<amount; g++) {
            if(!settings.contains(".currentPalette")){
                paint.setColor(Color.argb(155, (int) (Math.random() * 155) + 100, (int) (Math.random() * 155) + 100, (int) (Math.random() * 155) + 155));
            }else{
                //if currentPalette is set pick random color from list
                int rand = (int)(Math.random()*colorPaints.length);
                paint.setColor(colorPaints[rand]);
            }

            if (secondX == 0 && secondY == 0) {
                secondX = (int) ((Math.random() * size * 2) - size) + startX;
                secondY = (int) ((Math.random() * size * 2) - size) + startY;
                while (!onScreen(secondX, secondY)) {
                    secondX = (int) ((Math.random() * size * 2) - size) + startX;
                    secondY = (int) ((Math.random() * size * 2) - size) + startY;
                }
            } else {
                startX = secondX;
                startY = secondY;
                secondX = thirdX;
                secondY = thirdY;
            }
            thirdY = (int) (Math.random() * (size * 2) - size);
            thirdY += startY;
            thirdX = (int) (Math.random() * (size * 2) - size);
            thirdX += startX;
            while (!onScreen(thirdX, thirdY)) {
                thirdX = (int) ((Math.random() * size * 2) - size) + startX;
                thirdY = (int) ((Math.random() * size * 2) - size) + startY;
            }
            //make shape
            path = new Path();
            path.moveTo(startX, startY);
            path.lineTo(secondX, secondY);
            path.lineTo(thirdX, thirdY);
            path.close();
            path.moveTo(MainActivity.getScreenWidth()/2,MainActivity.getScreenHeight()/2);
            canvas.drawPath(path, paint);
            //add each color to the corresponding position in the arrays for the path.
            colors[g] = paint.getColor();
            paths[g] = path;
        }
    }


    public void setCurrentPaths(Path[] setPaths){
        this.paths = setPaths;
    }

    public void setCurrentColors(int[] setColors){
        this.colors = setColors;
    }

    public void setCurrentShadow(Path[] setShadow){
        this.shadows = setShadow;
    }

    public Path[] getCurrentPaths(){
        return paths;
    }

    public int[] getCurrentColors(){
        return colors;
    }

    private boolean onScreen(int x, int y){
        int width = MainActivity.getScreenWidth();
        int height = MainActivity.getScreenHeight();

        if(x>0 && x<width && y>0 && y<height){
            return true;
        }
        return false;
    }
}
