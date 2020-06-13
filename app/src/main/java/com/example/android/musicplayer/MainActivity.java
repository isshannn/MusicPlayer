package com.example.android.musicplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    SeekBar positionBar;
    TextView etim;
    TextView rtim;
    int TotalTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Media Player
        mediaPlayer = MediaPlayer.create(this, R.raw.yalgaar);
        mediaPlayer.setLooping(true);
        mediaPlayer.seekTo(0);
        mediaPlayer.setVolume(0.5f, 0.5f);
        TotalTime = mediaPlayer.getDuration();

        final ImageView play = (ImageView) findViewById(R.id.Play);
        etim = (TextView) findViewById(R.id.ElapsedTime);
        rtim = (TextView) findViewById(R.id.RemainingTime);

        // TimeBar
        positionBar = (SeekBar) findViewById(R.id.TimeBar);
        positionBar.setMax(TotalTime);
        positionBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if (fromUser){
                            mediaPlayer.seekTo(progress);
                            positionBar.setProgress(progress);
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );


        // VolumeBar
        SeekBar volumeBar = (SeekBar) findViewById(R.id.VolumeBar);
        volumeBar.setOnSeekBarChangeListener(
                new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        float volume = progress / 100f;
                        mediaPlayer.setVolume( volume , volume);
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                }
        );


        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mediaPlayer.isPlaying()) {
                    // While Paused
                    play.setBackgroundResource(R.drawable.stop);
                    mediaPlayer.start();
                } else {
                    play.setBackgroundResource(R.drawable.play);
                    mediaPlayer.pause();
                }
            }
        });


        // Thread Handling (Update TimeBar, ElapsedTime And RemainingTime)
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mediaPlayer != null){
                    try {
                        Message msg =new Message();
                        msg.what = mediaPlayer.getCurrentPosition();
                        handler.sendMessage(msg);
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {}
                }
            }
        }).start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            int currentPosition = msg.what;
            // Update PositionBar
            positionBar.setProgress(currentPosition);

            // Update labels
            String etime = createTimeLabel(currentPosition);
            etim.setText(etime);
            String rtime = createTimeLabel(TotalTime - currentPosition);
            rtim.setText("-" + rtime);
        }
    };

    public String createTimeLabel(int time){
        String timeLabel = "";
        int min = time / 1000 / 60;
        int sec = time / 1000 % 60;
        timeLabel = min + ":";
        if (sec < 10)  timeLabel += "0";
        timeLabel += sec;

        return timeLabel;
    }

}