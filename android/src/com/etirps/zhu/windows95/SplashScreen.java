package com.etirps.zhu.windows95;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

public class SplashScreen extends Activity implements MediaPlayer.OnCompletionListener {

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.splash_screen);
        String fileName = "android.resource://"+  getPackageName() +"/raw/video";

        VideoView vv = (VideoView) this.findViewById(R.id.surface);
        vv.setVideoURI(Uri.parse(fileName));
        vv.setOnCompletionListener(this);
        vv.start();

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Intent intent = new Intent(this, AndroidLauncher.class);
        startActivity(intent);
        finish();
    }
}
