package com.example.mymusicplayer;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Telephony;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mymusicplayer.databinding.ActivityMainBinding;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    Activity activity = MainActivity.this;
    ActivityMainBinding binding;
    ArrayList<AudioModel> songsList = new ArrayList<>();
    MusicListAdapter adapter;
    public int position = -1;
    MediaPlayer mediaPlayer = MyMediaPlayer.getInstance();
    public static String TitleName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onResume() {
        super.onResume();
        RotateAnimation rotate;
        rotate = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);

        position = MyMediaPlayer.currentIndex;
        if (mediaPlayer.isPlaying()){
            binding.llBottom.setVisibility(View.VISIBLE);

            rotate.setDuration(4000);
           // rotate.setRepeatMode(Animation.REVERSE);
            rotate.setRepeatCount(Animation.INFINITE);
            rotate.setInterpolator(new LinearInterpolator());
            binding.ivSong.startAnimation(rotate);

            binding.tvSongTitle.setText(TitleName);

        }else {
            rotate.cancel();
            binding.llBottom.setVisibility(View.GONE);
        }
        adapter = new MusicListAdapter(activity, songsList);
        binding.rvAllMusic.setLayoutManager(new LinearLayoutManager(activity));
        binding.rvAllMusic.setAdapter(adapter);

        binding.rvAllMusic.getLayoutManager().scrollToPosition(position);
    }

    private void init() {
        binding.llBottom.setOnClickListener(this);
      /*  String[] projection = {
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION
        };
        String selection = MediaStore.Audio.Media.DATA;
        String sortOrder = MediaStore.Audio.Media.TITLE + " ASC";
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";*/
        binding.searchViewId.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                adapter.getFilter().filter(query.trim());
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText.trim());
                return false;
            }
        });


        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {

                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media._ID,
        };

        if (!checkPermission()) {
            requestPermission();
            return;
        } else {
            Cursor cursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    projection,
                    selection,
                    null, null);
            while (cursor.moveToNext()) {

                AudioModel songData = new AudioModel(cursor.getString(0), cursor.getString(1), cursor.getString(2),
                        cursor.getString(3), cursor.getString(4));
                if (new File(songData.getPath()).exists()) {
                    songsList.add(songData);
                }

                if (songsList.size() == 0) {
                    binding.tvNoSong.setVisibility(View.VISIBLE);
                    binding.rvAllMusic.setVisibility(View.GONE);
                } else {
                    adapter = new MusicListAdapter(activity, songsList);
                    binding.rvAllMusic.setLayoutManager(new LinearLayoutManager(activity));
                    binding.rvAllMusic.setAdapter(adapter);
                }
            }

        }
    }

    boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else
            return false;
    }

    void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            Toast.makeText(activity, " READ PERMISSION IS REQUIRE PLEAS ALLOW FROM SETTING!", Toast.LENGTH_SHORT).show();
        } else
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 123);
    }

    @Override
    public void onClick(View v) {
        if (v == binding.llBottom){
            Intent intent = new Intent(MainActivity.this,MusicPlayerActivity.class);
            intent.putExtra("List",songsList);
            startActivity(intent);
        }
    }
}