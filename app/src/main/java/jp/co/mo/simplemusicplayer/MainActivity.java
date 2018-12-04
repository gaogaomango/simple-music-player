package jp.co.mo.simplemusicplayer;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private SeekBar mDurationSeekBar;
    private Button mStartBtn;
    private Button mStopBtn;
    private Button mPauseBtn;
    private ListView mMusicListView;

    private MediaPlayer mMp;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO: use databinding

        mDurationSeekBar = findViewById(R.id.durationSeekBar);
//        mDurationSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//            }
//        });
        mStartBtn = findViewById(R.id.startBtn);
        mStopBtn = findViewById(R.id.stopBtn);
        mPauseBtn = findViewById(R.id.pauseBtn);
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

    private void loadMusicList() {
        final List<MusicInfo> musicInfos = createMusicList();
        MusicInfoAdapter adapter = new MusicInfoAdapter(this, musicInfos);
        mMusicListView.setAdapter(adapter);
        mMusicListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MusicInfo mInfo = musicInfos.get(position);
                mMp = new MediaPlayer();
                try {
                    mMp.setDataSource(mInfo.getPath());
                    mMp.prepare();
                    mMp.start();
                    mDurationSeekBar.setMax(mMp.getDuration());
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private List<MusicInfo> createMusicList() {
        final List<MusicInfo> musicInfos = new ArrayList<>();
        musicInfos.add(new MusicInfo("/storage/self/primary/Download/1.mp3",
                "music1",
                "one",
                "number 1 singer"));
        musicInfos.add(new MusicInfo("/storage/self/primary/Download/2.mp3",
                "music2",
                "two",
                "number 2 singer"));
        musicInfos.add(new MusicInfo("/storage/self/primary/Download/3.mp3",
                "music3",
                "three",
                "number 3 singer"));

        return musicInfos;
    }
}
