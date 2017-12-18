package com.erg.erg;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.widget.SeekBar;
import android.widget.TextView;


/**
 * Created by haykh on 22.05.2017.
 */

public class Settings extends Activity {

    private static final String SAVED_TEXT = "saved_text";
    public SeekBar sb;
    String i;
    String erg;
    int newprogress = 14;
    SharedPreferences save_size;
    private TextView ftext;
    private TextView textView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seekbar);
        ftext = (TextView) findViewById(R.id.ftext);
        Intent from_Main = getIntent();
        erg = from_Main.getStringExtra("erg");
        sb = (SeekBar) findViewById(R.id.seekBar);

        sb.setProgress(5);


        //ftext.setTextSize(SeekBar.getDefaultSize(12,1));
        //change_Text_size(10);
        // MainActivity obj = new MainActivity();
        //erg = obj.get_song1();
        ftext.setText(Html.fromHtml(erg));

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                newprogress = 14 + progress;
                ftext.setTextSize(newprogress);


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                SharedPreferences mPrefs = getSharedPreferences("IDvalue", 0);
                SharedPreferences.Editor editor = mPrefs.edit();
                editor.putInt("init", newprogress);
                editor.commit();

            }
        });

    }


    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("size", newprogress);
        setResult(RESULT_OK, intent);
        finish();
    }
}




