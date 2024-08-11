package com.example.youcoolmusic2.Interfaces;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.View;
import android.widget.VideoView;
import androidx.core.content.ContextCompat;
import com.example.youcoolmusic2.Activity.ControlPlayerActivity;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Tasks.BackgroundMusicService;
import com.example.youcoolmusic2.R;
import java.io.File;
import static com.example.youcoolmusic2.Activity.ControlPlayerActivity.blurRenderScript;
import static com.example.youcoolmusic2.App.Activity_Context;
import static com.example.youcoolmusic2.App.SavePosition;
import static com.example.youcoolmusic2.App.positionArr;
import static com.example.youcoolmusic2.App.searchVideoAdapter;
import static com.example.youcoolmusic2.App.toast;
import static com.example.youcoolmusic2.App.video;

public class OflinePlayer extends Player {

    private VideoView videoView;
    private MediaPlayer media;
    public static boolean is_FullScreen;

    public OflinePlayer(Context context){
        super(context);
        videoView = view.findViewById(R.id.backgroundVideo1);
        videoView.setVisibility(View.VISIBLE);
        videoView.setOnClickListener(this);
        super.StartVideo();
    }

    @Override
    public void StartVideo() {
        super.StartVideo();
        if(!video.isDownloads){
            restart();
        }else {
            String path = App.DownloadDirectories + video.getId() + "_video.webm";
            File f = new File(path);
            if (f.exists() && f.isFile()) {
                videoView.setVideoPath(path);
            } else {
                path = "android.resource://" + context.getPackageName() + "/" + R.raw.dance_girl;
                videoView.setVideoURI(Uri.parse(path));
                String finalPath = path;
                videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.reset();
                        videoView.setVideoURI(Uri.parse(finalPath));
                        videoView.start();
                    }
                });
            }
            try {
                String path1 = App.DownloadDirectories + video.getId() + "_audio.webm";
                if(media!=null){
                    media.release();
                }
                media = MediaPlayer.create(context, Uri.parse(path1));
                media.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mediaPlayer) {
                        mediaPlayer.start();
                        videoView.start();
                        is_Play = true;
                        BackgroundMusicService.showNote(context, video.getTitle(), R.drawable.ic_pause);
                        play_pause_music.setBackgroundResource(R.drawable.ic_pause);
                        try {
                            ControlPlayerActivity.play_pause_music_AC.setBackgroundResource(R.drawable.ic_pause);
                            ControlPlayerActivity.img_video.setImageBitmap(blurRenderScript(App.Activity_Context,App.currentPoster, 15));
                            ControlPlayerActivity.MusicVisualizer.setAudioSessionId(GetSessionId());
                            ControlPlayerActivity.name_music_AC.setText(video.getTitle());
                        }catch (Exception e){}
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    while (runingService) {
                                        Thread.sleep(1000);
                                        Sesions();
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }).start();
                    }
                });
                media.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mediaPlayer) {
                        videoView.pause();
                        is_Play = false;
                        NextVideo();
                    }
                });
                searchVideoAdapter.notifyItemChanged(positionArr);
                SavePosition(context);
            } catch (Exception e) {
                toast.showToas(Activity_Context, context.getString(R.string.error_file), false);
            }
        }
    }

    @Override
    public void Pause_Play() {
        if(is_Play){
            videoView.pause();
            media.pause();
            is_Play = false;
            BackgroundMusicService.showNote(context,video.getTitle(),R.drawable.ic_play);
            play_pause_music.setBackgroundResource(R.drawable.ic_play);
            try {
                ControlPlayerActivity.play_pause_music_AC.setBackgroundResource(R.drawable.ic_play);
                ControlPlayerActivity.img_video.setImageBitmap(blurRenderScript(App.Activity_Context,App.currentPoster, 15));
                ControlPlayerActivity.name_music_AC.setText(video.getTitle());
            }catch (Exception e){}
        }else{
            videoView.start();
            media.start();
            is_Play = true;
            BackgroundMusicService.showNote(context,video.getTitle(),R.drawable.ic_pause);
            play_pause_music.setBackgroundResource(R.drawable.ic_pause);
            try {
                ControlPlayerActivity.play_pause_music_AC.setBackgroundResource(R.drawable.ic_pause);
                ControlPlayerActivity.img_video.setImageBitmap(blurRenderScript(App.Activity_Context,App.currentPoster, 15));
                ControlPlayerActivity.name_music_AC.setText(video.getTitle());
            }catch (Exception e){}
        }
        videoView.seekTo(media.getCurrentPosition());
    }

    @Override
    public void NextVideo() {
        super.NextVideo();
        try {
            StartVideo();
        }catch (Exception e){
            restart();
        }
    }

    @Override
    public void BackVideo() {
        super.BackVideo();
        try {
            StartVideo();
        }catch (Exception e){
            restart();
        }
    }

    @Override
    public void SeekTo(long pos) {
        media.seekTo((int) pos);
        videoView.seekTo((int) pos);
    }

    @Override
    public int GetSessionId() {
        //return media.getAudioSessionId();
        return 0;
    }

    @Override
    public void clouseMusic() {
        super.clouseMusic();
        release();
    }

    @Override
    public void Sesions() {
        try {
            super.Sesions(media.getCurrentPosition(), media.getDuration());
        }catch (Exception e){}
    }

    @Override
    public boolean isFullScreen() {
        return is_FullScreen;
    }

    @Override
    public void InFullScreen() {
        OflinePlayer.is_FullScreen = true;
        super.InFullScreen();
    }

    @Override
    public void ShowWindow() {
        super.ShowWindow();
        videoView.seekTo(media.getCurrentPosition());
    }

    @Override
    public void exitFullScreen() {
        OflinePlayer.is_FullScreen = false;
        WithFullScreen();
    }

    @Override
    public void release() {
        videoView = null;
        if(media!=null) {
            media.release();
        }
    }

    @Override
    public void restart() {
        if(isFullScreen()){
            exitFullScreen();
        }
        clouseMusic();
        Intent intent = new Intent(context, BackgroundMusicService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        ContextCompat.startForegroundService(context,intent);
    }
}
