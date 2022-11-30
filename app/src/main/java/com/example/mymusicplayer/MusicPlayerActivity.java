package com.example.mymusicplayer;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mymusicplayer.Services.CreatNotification;
import com.example.mymusicplayer.Services.OnClearFromRecentService;
import com.example.mymusicplayer.Services.Playable;
import com.example.mymusicplayer.databinding.ActivityMusicPlayerBinding;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener, Playable {
    Activity activity = MusicPlayerActivity.this;
    ActivityMusicPlayerBinding binding;
    ArrayList<AudioModel> songList = new ArrayList<>();
    AudioModel currentSong;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    int x = 0;
    NotificationManager notificationManager;
    boolean cameFromNotification = false;


    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getExtras().getString("actionname");
            Log.e("TAG", "broadcastReceiverAction::" + action);
            switch (action) {
                case CreatNotification.ACTION_PREV:
                    playPreviousSong();
                    //  onTrackPrev();
                    break;
                case CreatNotification.ACTION_PLAY:
                    pausePlay();
                    break;
                case CreatNotification.ACTION_NEXT:
                    playNextSong();
                    //  onTrackNext();
                    break;
                case CreatNotification.ACTION_IN_APP:
                    Log.e("TAG", "onReceive: In App ");

                    if (getIntent().getExtras() != null) {
                        Bundle b = getIntent().getExtras();
                        cameFromNotification = b.getBoolean("fromNotification");
                        Log.e("Came from notification", String.valueOf(cameFromNotification));
                    }

                    break;
            }
        }
    };

    @SuppressLint("DefaultLocale")
    public static String convertToMms(String duration) {
        Long millis = Long.parseLong(duration);
        return String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));

       /* long seconds = (millis / 1000) % 60;
        long minutes = ((millis - seconds) / 1000) / 60;
        Log.e("TAG", "seconds: " + seconds + " mi-- " + minutes);
        return minutes + ":" + seconds;*/
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMusicPlayerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Log.e("TAG", "Lonch Activity:");


        init();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CreateChannel();
            registerReceiver(broadcastReceiver, new IntentFilter("TRACKS_TRACKS"));
            startService(new Intent(this, OnClearFromRecentService.class));
        }


    }

    private void CreateChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CreatNotification.CHANNEL_ID,
                    "Nilu", NotificationManager.IMPORTANCE_HIGH);

            notificationManager = getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void init() {
        binding.ivNext.setOnClickListener(this);
        binding.ivPrev.setOnClickListener(this);
        binding.ivPause.setOnClickListener(this);
        binding.ivBack.setOnClickListener(this);


        songList = (ArrayList<AudioModel>) getIntent().getSerializableExtra("List");


        if (mediaPlayer.isPlaying()) {
            Log.e("TAG", "cameFromNotification:: " + cameFromNotification);
            AllReadyPlay();
        } else {
            CreatNotification.createNotification(MusicPlayerActivity.this, songList.get(MyMediaPlayer.currentIndex), R.drawable.pause_circle,
                    MyMediaPlayer.currentIndex, songList.size() - 1);

            setResorsWithMusic();
        }

        MusicPlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    binding.seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    binding.tvCurrentTime.setText(convertToMms(mediaPlayer.getCurrentPosition() + ""));

                    if (mediaPlayer.isPlaying()) {
                        binding.ivPause.setImageResource(R.drawable.pause_circle);
                        binding.ivMusicBig.setRotation(x++);
                        if ((songList != null && MyMediaPlayer.currentIndex != songList.size() - 1) &&
                                binding.tvCurrentTime.getText().toString().trim().equals(binding.tvTotalTime.getText().toString().trim())) {
                            Log.e("TAG", "run: " + "sndjfsasibi");
                            MyMediaPlayer.currentIndex += 1;
                            mediaPlayer.reset();
                            setResorsWithMusic();
                            onTrackNext();
                        }
                    } else {
                        binding.ivPause.setImageResource(R.drawable.baseline_stop);
                        binding.ivMusicBig.setRotation(0);
                    }
                }
                new Handler().postDelayed(this, 100);
            }
        });

        binding.seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (mediaPlayer != null && fromUser) {
                    mediaPlayer.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void setResorsWithMusic() {
        currentSong = songList.get(MyMediaPlayer.currentIndex);
        binding.tvTitle.setText(currentSong.getTitle());
        MainActivity.TitleName = currentSong.getTitle();
        binding.tvTotalTime.setText(convertToMms(currentSong.getDuration()));
        Log.e("TAG", "setResorsWithMusic: " + convertToMms(currentSong.getDuration()));
        playMusic();
    }

    private void AllReadyPlay() {
        currentSong = songList.get(MyMediaPlayer.currentIndex);
        binding.tvTitle.setText(currentSong.getTitle());
        mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
        binding.tvTotalTime.setText(convertToMms(currentSong.getDuration()));
        binding.seekBar.setMax(mediaPlayer.getDuration());
    }

    private void playMusic() {
        mediaPlayer.reset();
        try {
            Log.e("TAG", "currentSong.getPath(): " + currentSong.getPath());
            mediaPlayer.setDataSource(currentSong.getPath());
            mediaPlayer.prepare();
            mediaPlayer.start();
            binding.seekBar.setProgress(0);
            binding.seekBar.setMax(mediaPlayer.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(activity, "error: " + e, Toast.LENGTH_SHORT).show();
        }
    }

    private void playNextSong() {
        if (MyMediaPlayer.currentIndex == songList.size() - 1)
            return;
        MyMediaPlayer.currentIndex += 1;
        mediaPlayer.reset();
        setResorsWithMusic();
        onTrackNext();
    }

    private void playPreviousSong() {
        if (MyMediaPlayer.currentIndex == 0)
            return;
        MyMediaPlayer.currentIndex -= 1;
        mediaPlayer.reset();
        setResorsWithMusic();
        onTrackPrev();
    }

    private void pausePlay() {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            onTrackPause();
        } else {
            mediaPlayer.start();
            onTrackPlay();
        }
    }

    @Override
    public void onClick(View v) {
        if (v == binding.ivPause) {
            pausePlay();
        } else if (v == binding.ivPrev) {
            playPreviousSong();

        } else if (v == binding.ivNext) {
            playNextSong();
        } else if (v == binding.ivBack) {
            finish();
        }
    }

    @Override
    public void onTrackPrev() {
        CreatNotification.createNotification(MusicPlayerActivity.this, songList.get(MyMediaPlayer.currentIndex),
                R.drawable.pause_circle, MyMediaPlayer.currentIndex, songList.size() - 1);
        binding.tvTitle.setText(songList.get(MyMediaPlayer.currentIndex).getTitle());
    }

    @Override
    public void onTrackPlay() {
        CreatNotification.createNotification(MusicPlayerActivity.this, songList.get(MyMediaPlayer.currentIndex),
                R.drawable.pause_circle, MyMediaPlayer.currentIndex, songList.size() - 1);
        binding.ivPause.setImageResource(R.drawable.pause_circle);
        binding.tvTitle.setText(songList.get(MyMediaPlayer.currentIndex).getTitle());
        mediaPlayer.start();
    }

    @Override
    public void onTrackPause() {
        CreatNotification.createNotification(MusicPlayerActivity.this, songList.get(MyMediaPlayer.currentIndex),
                R.drawable.baseline_stop, MyMediaPlayer.currentIndex, songList.size() - 1);
        binding.ivPause.setImageResource(R.drawable.baseline_stop);
        binding.tvTitle.setText(songList.get(MyMediaPlayer.currentIndex).getTitle());
        mediaPlayer.pause();
    }

    @Override
    public void onTrackNext() {
        CreatNotification.createNotification(MusicPlayerActivity.this, songList.get(MyMediaPlayer.currentIndex),
                R.drawable.pause_circle, MyMediaPlayer.currentIndex, songList.size() - 1);
        binding.tvTitle.setText(songList.get(MyMediaPlayer.currentIndex).getTitle());
        Log.e("TAG", "onTrackNext: ");
    }

  /*  @Override
    protected void onDestroy() {
        super.onDestroy();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.cancelAll();
        }
        unregisterReceiver(broadcastReceiver);
    }*/
}