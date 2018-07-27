package com.example.s231230.procegen.generation;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Path;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.s231230.procegen.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

public class GenerateActivity extends Activity {

    GenerateView genView;
    File dir;
    File screenshot;
    ArrayList<Path[]> shapeHist;
    ArrayList<int[]> colorHist;
    private AdView ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate);
        //initialize ads
        ad = findViewById(R.id.mainActAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
        genView = findViewById(R.id.genView);
        shapeHist = new ArrayList<>();
        colorHist = new ArrayList<>();
        Path tempPath = new Path();tempPath.moveTo(0,0);tempPath.lineTo(1,1);tempPath.close();
        shapeHist.add(new Path[]{tempPath});
        colorHist.add(new int[]{Color.GRAY});
        checkWritePermission(this);
        shapeHist.add(genView.currentFacet.getCurrentPaths());
        colorHist.add(genView.currentFacet.getCurrentColors());
    }
    public void generateNew(View v) {
        //handles the visibility of buttons, then creates new facet object
        //(generating a new facet)
        if(findViewById(R.id.btn_saveBG).getVisibility()!=View.VISIBLE){
            findViewById(R.id.btn_saveLock).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_saveWall).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_saveBG).setVisibility(View.VISIBLE);
        }
        genView.generateFacet(new Canvas());
        if(genView!=null) {
            genView.invalidate();
        }
        else{Log.i("GenAct.genNew()","genView = null.");}
        shapeHist.add(genView.currentFacet.getCurrentPaths());
        colorHist.add(genView.currentFacet.getCurrentColors());
    }

    public void saveBG(View v){
        //take screenshot
        Date now = new Date();
        //make save as WAll, n save as lock visible, n make saveBG invisible
        findViewById(R.id.btn_saveLock).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_saveWall).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_saveBG).setVisibility(View.INVISIBLE);
        try{
                genView.setDrawingCacheEnabled(true);
                Bitmap bit = Bitmap.createBitmap(genView.getDrawingCache());
                genView.setDrawingCacheEnabled(false);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                bit.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Procegen");
                if(!dir.exists()){
                    Log.i("GenAct.saveBG","Dir doesn't exist");
                    dir.mkdirs();
                }
                screenshot = new File(dir, now + ".jpg");
                FileOutputStream fOS = new FileOutputStream(screenshot);
                fOS.write(outputStream.toByteArray());
                fOS.flush();
                fOS.close();
                MediaScannerConnection.scanFile(this,new String[] {screenshot.getPath()}, new String[] {"image/jpeg"},null);
                Log.i("GenAct.saveBG()", "Path = " + screenshot.getPath());
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void saveAsWallpaper(View v){
        //Save image as wallpaper
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        Bitmap bitmap = BitmapFactory.decodeFile(screenshot.getPath());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        try {
//            wallpaperManager.setBitmap(bitmap);
            wallpaperManager.setStream(inputStream,null, true, WallpaperManager.FLAG_SYSTEM);
            Toast.makeText(getApplicationContext(),"Set Background Successfully", Toast.LENGTH_LONG).show();
            findViewById(R.id.btn_saveLock).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_saveWall).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_saveBG).setVisibility(View.VISIBLE);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void saveAsLockScreen(View v){
        //Save image to lock screen
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
        Bitmap bitmap = BitmapFactory.decodeFile(screenshot.getPath());
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        try {
            wallpaperManager.setStream(inputStream,null, true, WallpaperManager.FLAG_LOCK);
            Toast.makeText(getApplicationContext(),"Set Lock Screen Successfully", Toast.LENGTH_LONG).show();
            findViewById(R.id.btn_saveLock).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_saveWall).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_saveBG).setVisibility(View.VISIBLE);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public void previousGeneration(View v){
        //go back one shape and get the corresponding colors to it
        //subtract one b/c going back one
        //then sub another b/c need index
        if(shapeHist.size()>2) {
            shapeHist.remove(shapeHist.size()-1);
            colorHist.remove(colorHist.size()-1);
            genView.currentFacet.setCurrentPaths(shapeHist.get(shapeHist.size() - 1));
            genView.currentFacet.setCurrentColors(colorHist.get(colorHist.size() - 1));
            genView.currentFacet.setCurrentShadow(shapeHist.get(shapeHist.size() - 1));
            genView.invalidate();
        }
    }

    public static void checkWritePermission(Activity activity){
        //give app permission to save bg & lock screen
        String[] PERMISSIONS_STORAGE = {
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        };
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permission != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, 1);
        }
    }
}
