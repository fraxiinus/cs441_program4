package com.etirps.zhu.windows95;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

public class SplashScreen extends Activity implements MediaPlayer.OnCompletionListener, View.OnTouchListener {


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);
        String fileName = "android.resource://"+  getPackageName() + "/" + R.raw.win98wide;

        VideoView vv = (VideoView) this.findViewById(R.id.surface);
        vv.setVideoURI(Uri.parse(fileName));
        vv.setOnTouchListener(this);
        vv.setOnCompletionListener(this);
        vv.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Intent intent = new Intent(this, AndroidLauncher.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        Intent intent = new Intent(this, AndroidLauncher.class);
        startActivity(intent);
        finish();
        return true;
    }


}
