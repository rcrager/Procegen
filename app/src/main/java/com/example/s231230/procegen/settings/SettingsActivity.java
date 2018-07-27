package com.example.s231230.procegen.settings;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.example.s231230.procegen.MainActivity;
import com.example.s231230.procegen.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

/**
 * Created by Robert on 11/27/17.
 * Settings tab of Procegen
 * Allows user to choose the
 * current color palette, if
 * shadows are enabled and how
 * far they are, and lets them
 * choose the number of generated
 * shapes to be generated.
 */

public class SettingsActivity extends Activity implements View.OnClickListener{

    AdView ad;

    int genAmount;
    SeekBar sb;
    int[] colors; //api lvl 26- & 26+
    int[][] palettes;
    TextView shapeDisplay;
    SharedPreferences settings;
    LinearLayout row1;
    LinearLayout row2;
    LinearLayout row3;
    LinearLayout row4;
    RelativeLayout rl;
    Dialog colorPicker;
    View lastButtonCLick;
    LinearLayout[] rows;
    Switch shadows;
    SeekBar shadowNum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //initialize ads
        ad = findViewById(R.id.mainActAd);
        AdRequest adRequest = new AdRequest.Builder().build();
        ad.loadAd(adRequest);
        settings = getSharedPreferences("Preferences",0);
//        settings = MainActivity.settings;
        sb = findViewById(R.id.sb_amount);
        shadows = findViewById(R.id.sw_shadows);
        shadowNum = findViewById(R.id.sb_shadows);
        shadows.setChecked(settings.getBoolean("shadowBool",false));
        shadowNum.setProgress(settings.getInt("shadowNum",0));
        if(shadows.isChecked()){
            shadowNum.setVisibility(View.VISIBLE);
        }else{
            shadowNum.setVisibility(View.INVISIBLE);
        }
        shadows.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //when user clicks shadow toggle switch it'll write it to the settings
                settings.edit().putBoolean("shadowBool",shadows.isChecked()).apply();
                if(shadows.isChecked()){
                    shadowNum.setVisibility(View.VISIBLE);
                }else {
                    shadowNum.setVisibility(View.INVISIBLE);
                }
            }
        });
        shadowNum.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            //when user changes the progress on the bar under the shadow text
            //it'll change the distance of the shadow & write it to the settings
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                settings.edit().putInt("shadowNum",progress).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        shapeDisplay = findViewById(R.id.tv_num);
//        if(settings.contains(settings + ".genAmount")){
        if(settings.contains(".genAmount")){
//            sb.setProgress(settings.getInt(settings + ".genAmount", 1));
            sb.setProgress(settings.getInt(".genAmount", 1));
        }
        genAmount = sb.getProgress();
        shapeDisplay.setText("" + sb.getProgress());
//        settings.edit().putInt( settings + ".genAmount",genAmount).apply();
        settings.edit().putInt( ".genAmount",genAmount).apply();
        colors = new int[genAmount];
        palettes = new int[4][colors.length];
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
                //when user changes progress on bar to right of shape text
                //changes the number of shapes generated for the generate part
                shapeDisplay.setText(String.valueOf(progress));
                if(sb.getProgress()<=0){
                    sb.setProgress(1);
                }
                genAmount = sb.getProgress();
//                settings.edit().putInt(settings + ".genAmount",genAmount).apply();
                settings.edit().putInt(".genAmount",genAmount).apply();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        row1 = findViewById(R.id.cols_1);
        row2 = findViewById(R.id.cols_2);
        row3 = findViewById(R.id.cols_3);
        row4 = findViewById(R.id.cols_4);
        rows = new LinearLayout[4];
        rows[0] = row1;
        rows[1] = row2;
        rows[2] = row3;
        rows[3] = row4;
        rl = findViewById(R.id.rl_rows);
//        if (settings.contains(settings + ".currentPaletteNum")) {
        if (settings.contains(".currentPaletteNum")) {
            //highlight correct row
//            int b = settings.getInt(settings + ".currentPaletteNum",0)-1;
            int b = settings.getInt(".currentPaletteNum",0)-1;
            rows[b].setSelected(true);
            rows[b].setBackgroundColor(Color.argb(255, 204, 23, 232));
            for (int v = 0; v <=3; v++) {
                if (v != b) {
                    rows[v].setSelected(false);
                    rows[v].setBackgroundColor(Color.argb(0, 0, 0, 0));
                }
            }
        }
        for(int r = 0; r<=3; r++) {
            //palettes
            int[] colorPalettes = new int[5];
            for(int b = 0; b<=4; b++) {
                //colors
                //if the sysPrefs already contain color data just store it under
                //the color palettes. If not...
                //save all color palettes as "location.palette#.button#.colorInt";
//                if(!settings.contains(settings + "." + (r+1) + "." + (b+1))) {
                if(!settings.contains("." + (r+1) + "." + (b+1))) {
                    //write bg of buttons if not already in sysPrefs
//                    settings.edit().putInt(settings + "." + (r + 1) + "." + (b + 1), rows[r].getChildAt(b).getBackgroundTintList().getDefaultColor()).apply();
                    settings.edit().putInt("." + (r + 1) + "." + (b + 1), rows[r].getChildAt(b).getBackgroundTintList().getDefaultColor()).apply();
                }
//                    ColorStateList cl = ColorStateList.valueOf(settings.getInt(settings+"."+(r+1)+"."+(b+1),0));
                ColorStateList cl = ColorStateList.valueOf(settings.getInt("."+(r+1)+"."+(b+1),0));

                rows[r].getChildAt(b).setBackgroundTintList(cl);
                    //use get background tint list in order to keep UI the way it is and
                    //be able to change color of btn's
                    colorPalettes[b] = rows[r].getChildAt(b).getBackgroundTintList().getDefaultColor();
//                    colorPalettes[b] = Color.valueOf(rows[r].getChildAt(b).getBackgroundTintList().getDefaultColor()); //api lvl 26+
                    if(b>=4){
                        //if last button add colors to palettes list
                        palettes[r]=colorPalettes;
                    }
                }
            }
        }



    @Override
    public void onClick(View view){}

    public void addPalette(View view){}

    public void buttonClick(View view){
        lastButtonCLick = view;
        if(!view.isSelected()) {
            //if row isn't selected highlight it and deselect all other rows.
            for (int b = 0; b <=3; b++) {
                if (view.getParent() == rows[b] && !rows[b].isSelected()) {
                    rows[b].setSelected(true);
                    rows[b].setBackgroundColor(Color.argb(255, 204, 23, 232));
                    for (int v = 0; v <=3; v++) {
                        if (v != b) {
                            rows[v].setSelected(false);
                            rows[v].setBackgroundColor(Color.argb(0, 0, 0, 0));
                            //take all colors in row put them in array,
                            //(color[] stored in palette matrix) ^^,
                            //then put that array as a string
                            //called currentPalette in the sysPrefs
                            //then when need colors again split string by ","
                            //and each item should be the color int for the
                            //also make currentPaletteNum for current palette num so it is selected
                            //when user relaunches app as well

                            //for getting the color ints for each it will be presented like this
                            //[blue,green,yellow,red](but color ints)
                            //then use a for loop to loop through the array n get each int
                        }
                    }
                    String colorsArray = "";
                    for(int c = 0; c<palettes[b].length; c++){
                        if(c!=palettes[b].length-1) {
                            colorsArray = colorsArray + palettes[b][c] + ",";
                        } else{
                            colorsArray = colorsArray + "" + palettes[b][c];
                        }
                    }
//                    settings.edit().putString(settings + ".currentPalette",colorsArray).apply();
                    settings.edit().putString(".currentPalette",colorsArray).apply();
//                    settings.edit().putInt(settings + ".currentPaletteNum",b+1).apply();
                    settings.edit().putInt( ".currentPaletteNum",b+1).apply();

                }
            }
        }
        else {
            //for displaying the color picker once the row is selected and they click the button
            for(int r = 0; r<=3; r++) {
                for(int b = 0; b<=4; b++) {
                    if (view.getParent() == rows[r] && view == rows[r].getChildAt(b)) {
//                        settings.edit().putInt(settings + "." + (r+1) + "." + (b+1), view.getBackgroundTintList().getDefaultColor()).apply();
                        settings.edit().putInt("." + (r+1) + "." + (b+1), view.getBackgroundTintList().getDefaultColor()).apply();
                    }
                }
            }
            MainActivity.settings.edit().putInt("tempCurrentColor", view.getBackgroundTintList().getDefaultColor()).apply();
            colorPicker = new Dialog(SettingsActivity.this);
            colorPicker.setContentView(R.layout.dialog_color_picker);
            colorPicker.setTitle("Pick A Color");
            colorPicker.show();
            System.out.println("Color Picker Launch");
        }
    }

    public void btnCancel(View view){
        colorPicker.cancel();
    }

    public void btnOk(View view){
        int colorInt = MainActivity.settings.getInt("tempCurrentColor", 0);
        ColorStateList cl = ColorStateList.valueOf(colorInt);
        if(lastButtonCLick!=null) {
            lastButtonCLick.setBackgroundTintList(cl);
            for(int r = 0; r<=3; r++) {
                for(int b = 0; b<=4; b++) {
                    if (lastButtonCLick.getParent() == rows[r] && lastButtonCLick == rows[r].getChildAt(b)) {
//                        settings.edit().putInt(settings + "." + (r+1) + "." + (b+1), lastButtonCLick.getBackgroundTintList().getDefaultColor()).apply();
                        settings.edit().putInt("." + (r+1) + "." + (b+1), lastButtonCLick.getBackgroundTintList().getDefaultColor()).apply();
                    }
                }
            }
        }
        //loop through all buttons to set the correct button's color here
        colorPicker.dismiss();
    }
}
