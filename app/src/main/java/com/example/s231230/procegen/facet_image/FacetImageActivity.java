package com.example.s231230.procegen.facet_image;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.shapes.Shape;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.s231230.procegen.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import io.github.xyzxqs.xlowpoly.LowPoly;

/**
 * Created by Robert on 2/2/18.
 * Activity for creating facets from images.
 * Number of shapes made slider at the top.
 * Open and save on bottom.
 * Generates delaunay triangulation from
 * the image the user selects.
 */

public class FacetImageActivity extends Activity {

    public static float THRESHOLD = 30.0f;
    ImageView imgView;
    private AdView ad;
    File screenshot, dir;
    SeekBar tolerance;
    TextView tv_tolerance;
    ArrayList<Float> points;
    Bitmap newImage, oldImage;
    static ArrayList<Shape> facets;
    int[][] colorData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_facet);
        //initialize ads
        ad = findViewById(R.id.mainActAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
        points = new ArrayList<>();
        imgView = findViewById(R.id.facetImgView);
        facets = new ArrayList<>();
        tv_tolerance = findViewById(R.id.tv_tolerance);
        tolerance = findViewById(R.id.sb_tolerance);
        tolerance.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                THRESHOLD = progress;
                tv_tolerance.setText("" + Math.round(THRESHOLD));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void saveImgWall(View view){
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

    public void saveButton(View view){
        Date now = new Date();
        //make save as WAll, n save as lock visible, n make saveBG invisible
        findViewById(R.id.btn_saveLock).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_saveWall).setVisibility(View.VISIBLE);
        findViewById(R.id.btn_saveBG).setVisibility(View.INVISIBLE);
        try{
            imgView.setDrawingCacheEnabled(true);
            Bitmap bit = Bitmap.createBitmap(imgView.getDrawingCache());
            imgView.setDrawingCacheEnabled(false);
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

    public void saveImgLock(View view){
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

    public void openImage(View view) {
        colorData = new int[imgView.getWidth()][imgView.getHeight()];
        imgView.setDrawingCacheEnabled(true);
        oldImage = Bitmap.createScaledBitmap(imgView.getDrawingCache(), imgView.getWidth(), imgView.getHeight(), false);
        newImage = Bitmap.createScaledBitmap(imgView.getDrawingCache(), imgView.getWidth(), imgView.getHeight(), false);
        imgView.setImageBitmap(newImage);
        Intent photoPicker = new Intent(Intent.ACTION_PICK);
        File picDir = Environment.getExternalStorageDirectory();
        String picPath = picDir.getPath();
        Uri url = Uri.parse(picPath);
        photoPicker.setDataAndType(url, "image/*");
        int RESULT_LOAD_IMAGE = 1;
        startActivityForResult(photoPicker, RESULT_LOAD_IMAGE);
        imgView.setDrawingCacheEnabled(false);
    }

    public void onActivityResult(int reqCode, int resultCode, Intent data){
        super.onActivityResult(reqCode,resultCode,data);

        if(resultCode==RESULT_OK) {
            try {
                final Uri imageUri = data.getData();
                final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                final Bitmap imageChose = BitmapFactory.decodeStream(imageStream);
                Toast.makeText(getApplicationContext(),"Loading Image...", Toast.LENGTH_LONG).show();
                imgView.setImageBitmap(imageChose);
                generateFacet(imgView);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(getApplicationContext(),"Something Went Wrong! Please Select the Image Again.", Toast.LENGTH_LONG).show();
            }
        } else{
            Toast.makeText(getApplicationContext(),"You Haven't Selected an Image! Please Select an Image and Try Again.", Toast.LENGTH_LONG).show();
        }
    }

    public void generateFacet(ImageView imageView) {
        //create shapes to fill in based on the edge data
        imageView.setDrawingCacheEnabled(true);
        Bitmap outputImg = LowPoly.lowPoly(imageView.getDrawingCache(), FacetImageActivity.THRESHOLD*10, true);
        imageView.setImageBitmap(outputImg);
        imageView.setDrawingCacheEnabled(false);
    }



}
