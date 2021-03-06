package jp.co.mo.simplemusicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private SeekBar mDurationSeekBar;
    private Button mStartBtn;
    private Button mStopBtn;
    private Button mPauseBtn;
    private ListView mMusicListView;

    private MediaPlayer mMp;

    private MusicInfo mCurrentMusicInfo;

    private int mMediaCurrentPostion = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: use databinding

        mDurationSeekBar = findViewById(R.id.durationSeekBar);
        mDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                mMediaCurrentPostion = progress;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mMp.seekTo(mMediaCurrentPostion);
            }
        });
        mStartBtn = findViewById(R.id.startBtn);
        mStartBtn.setOnClickListener(this);
        mStopBtn = findViewById(R.id.stopBtn);
        mStopBtn.setOnClickListener(this);
        mPauseBtn = findViewById(R.id.pauseBtn);
        mPauseBtn.setOnClickListener(this);
        mMusicListView = findViewById(R.id.musicListView);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mMp != null) {
                                mDurationSeekBar.setProgress(mMp.getCurrentPosition());
                            }
                        }
                    });
                }
            }
        }).start();

    }

    @Override
    protected void onResume() {
        super.onResume();
        checkPermissions();
    }

    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                                Manifest.permission.READ_EXTERNAL_STORAGE
                        },
                        REQUEST_CODE_ASK_PERMISSIONS);
                return;
            }
        }
        loadMusicList();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadMusicList();
                } else {
                    Toast.makeText(this, "cannot access external strage. \n please check the permission status is granted.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.startBtn:
                startMediaPlayer();
                break;
            case R.id.stopBtn:
                stopMediaPlayer();
                break;
            case R.id.pauseBtn:
                pauseMediaPlayer();
                break;
        }
    }


    private void loadMusicList() {
        final List<MusicInfo> musicInfos = getMusicList();
        MusicInfoAdapter adapter = new MusicInfoAdapter(this, musicInfos);
        mMusicListView.setAdapter(adapter);
        mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // before start media player, stop the current media player.
                stopMediaPlayer();
                mCurrentMusicInfo = musicInfos.get(position);
                startMediaPlayer();
            }
        });
    }

    private void startMediaPlayer() {
        if (mMp != null) {
            if(!mMp.isPlaying()) {
                mMp.seekTo(mMediaCurrentPostion);
                mMp.start();
            }
        } else {
            if(mCurrentMusicInfo != null) {
                mMp = new MediaPlayer();
                try {
                    if (!TextUtils.isEmpty(mCurrentMusicInfo.getPath())) {
                        mMp.setDataSource(mCurrentMusicInfo.getPath());
                        mMp.prepare();
                        mMp.start();
                        mDurationSeekBar.setMax(mMp.getDuration());
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void stopMediaPlayer() {
        if (mMp != null && mMp.isPlaying()) {
            mMp.stop();
            mMp.release();
            mMp = null;
            mMediaCurrentPostion = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mDurationSeekBar.setProgress(mDurationSeekBar.getMin());
            } else {
                // does not working setProgress when i just set mMediaCurrentPostion. So set twise.
                mDurationSeekBar.setProgress(1);
                mDurationSeekBar.setProgress(mMediaCurrentPostion);
            }
        }
    }

    private void pauseMediaPlayer() {
        if (mMp != null && mMp.isPlaying()) {
            mMediaCurrentPostion = mMp.getCurrentPosition();
            mMp.pause();
        }
    }

    private List<MusicInfo> getMusicList() {
        final List<MusicInfo> musicInfos = new ArrayList<>();

        Uri allSongsUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        Cursor cursor = getContentResolver().query(allSongsUri, null, selection, null, null);
        if(cursor != null) {
            if(cursor.moveToFirst()) {
                do {
                    String songName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String fullPath = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String albumName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    String artistName = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    musicInfos.add(new MusicInfo(fullPath, songName, albumName, artistName));
                 } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return musicInfos;
    }
}
