package com.example.s231230.procegen;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;


import com.example.s231230.procegen.facet_image.FacetImageActivity;
import com.example.s231230.procegen.generation.GenerateActivity;
import com.example.s231230.procegen.settings.SettingsActivity;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

public class MainActivity extends Activity {

    public static SharedPreferences settings;
    private AdView ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        settings = getSharedPreferences("Preferences",0);
//        settings = PreferenceManager.getDefaultSharedPreferences(this);
        //initialize ads for testing
        MobileAds.initialize(this,"ca-app-pub-3940256099942544~3347511713");
        //initialize ads for published app
//        MobileAds.initialize(this,"ca-app-pub-1673539367458723~6422066600");
        //initialize ads
        ad = findViewById(R.id.mainActAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
    }

    public static int getScreenWidth(){
        return Resources.getSystem().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(){
        return Resources.getSystem().getDisplayMetrics().heightPixels;
    }

    public void startGeneration(View v){
        //changes to activity screen then draw on the generate activity instead of the generate view
        Intent intent = new Intent(this, GenerateActivity.class);
        this.startActivity(intent);
    }

    public void launchSettings(View v){
        //launch settings activity
        Intent intent = new Intent(this, SettingsActivity.class);
        this.startActivity(intent);
    }

    public void startFacetGen(View v){
        //launch createImage Activity
        Intent intent = new Intent(this, FacetImageActivity.class);
        this.startActivity(intent);
    }
}
