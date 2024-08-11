package com.example.youcoolmusic2.Interfaces;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.view.View;
import androidx.core.content.ContextCompat;
import com.example.youcoolmusic2.Activity.ControlPlayerActivity;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Tasks.BackgroundMusicService;
import com.example.youcoolmusic2.R;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerFullScreenListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.YouTubePlayerListener;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.utils.YouTubePlayerTracker;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView;
import static com.example.youcoolmusic2.Activity.ControlPlayerActivity.blurRenderScript;
import static com.example.youcoolmusic2.App.Activity_Context;
import static com.example.youcoolmusic2.App.SavePosition;
import static com.example.youcoolmusic2.App.searchVideoAdapter;
import static com.example.youcoolmusic2.App.video;


public class OnlinePlayer extends Player{

    private YouTubePlayerListener listener;
    private YouTubePlayer player;
    private YouTubePlayerTracker tracker = new YouTubePlayerTracker();
    private YouTubePlayerView backgroundVideo;

    private boolean isStart = false;

    public OnlinePlayer(Context context){
        super(context);
        backgroundVideo = view.findViewById(R.id.backgroundVideo);
        backgroundVideo.setVisibility(View.VISIBLE);
        super.StartVideo();
        listener = new YouTubePlayerListener() {
            @Override
            public void onError(YouTubePlayer player, PlayerConstants.PlayerError playerError) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(App.Activity_Context);
                dialog.setTitle(context.getString(R.string.error));
                dialog.setMessage(context.getString(R.string.error_in_youtube));
                dialog.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        NextVideo();
                    }
                });
                dialog.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog newDialog = dialog.create();
                newDialog.show();
                MediaPlayer error_media = MediaPlayer.create(Activity_Context,R.raw.error);
                error_media.start();
            }
            @Override
            public void onReady(YouTubePlayer player1) {
                player1.addListener(tracker);
                player = player1;
                player.loadVideo(video.getId(),0);
            }
            @Override
            public void onStateChange(YouTubePlayer player, PlayerConstants.PlayerState playerState) {
                switch (playerState){
                    case ENDED:
                        NextVideo();
                        break;
                    case PAUSED:
                        is_Play = false;
                        BackgroundMusicService.showNote(context,video.getTitle(),R.drawable.ic_play);
                        play_pause_music.setBackgroundResource(R.drawable.ic_play);
                        try {
                            ControlPlayerActivity.play_pause_music_AC.setBackgroundResource(R.drawable.ic_play);
                            ControlPlayerActivity.img_video.setImageBitmap(blurRenderScript(App.Activity_Context,App.currentPoster, 15));
                            ControlPlayerActivity.name_music_AC.setText(video.getTitle());
                        }catch (Exception e){}
                        break;
                    case PLAYING:
                        is_Play = true;
                        BackgroundMusicService.showNote(context,video.getTitle(),R.drawable.ic_pause);
                        play_pause_music.setBackgroundResource(R.drawable.ic_pause);
                        try {
                            ControlPlayerActivity.play_pause_music_AC.setBackgroundResource(R.drawable.ic_pause);
                            ControlPlayerActivity.img_video.setImageBitmap(blurRenderScript(App.Activity_Context,App.currentPoster, 15));
                            ControlPlayerActivity.name_music_AC.setText(video.getTitle());
                        }catch (Exception e){}
                        break;
                    case BUFFERING:
                        is_Play = false;
                        break;
                }
            }
            @Override
            public void onPlaybackQualityChange(YouTubePlayer player, PlayerConstants.PlaybackQuality playbackQuality) { }
            @Override
            public void onPlaybackRateChange(YouTubePlayer player, PlayerConstants.PlaybackRate playbackRate) { }
            @Override
            public void onCurrentSecond(YouTubePlayer player, float v) { Sesions(); }
            @Override
            public void onVideoDuration(YouTubePlayer player, float v) {
                if (v!=0){
                    is_Play = true;
                }
            }
            @Override
            public void onVideoLoadedFraction(YouTubePlayer player, float v) { }
            @Override
            public void onVideoId(YouTubePlayer player, String s) { }
            @Override
            public void onApiChange(YouTubePlayer player) { }
        };
        backgroundVideo.addFullScreenListener(new YouTubePlayerFullScreenListener() {
            @Override
            public void onYouTubePlayerEnterFullScreen() {
                InFullScreen();
            }
            @Override
            public void onYouTubePlayerExitFullScreen() {
                WithFullScreen();
            }
        });

    }

    @Override
    public void StartVideo() {
        super.StartVideo();
        if(App.video.isDownloads){
            restart();
        }else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(1000);
                        isStart = true;
                    }catch (Exception e) {e.printStackTrace();}
                }
            }).start();
            if (is_Play) {
                player.loadVideo(video.getId(), 0);
            } else {
                backgroundVideo.addYouTubePlayerListener(listener);
            }
            BackgroundMusicService.showNote(context, video.getTitle(), R.drawable.ic_pause);
            play_pause_music.setBackgroundResource(R.drawable.ic_pause);
            try {
                ControlPlayerActivity.play_pause_music_AC.setBackgroundResource(R.drawable.ic_pause);
                ControlPlayerActivity.img_video.setImageBitmap(blurRenderScript(App.Activity_Context,App.currentPoster, 15));
                ControlPlayerActivity.name_music_AC.setText(video.getTitle());
            }catch (Exception e){}
            searchVideoAdapter.notifyItemChanged(App.videos.indexOf(video));
            SavePosition(context);
        }
    }

    @Override
    public void Pause_Play() {
        try {
            if (is_Play) {
                player.pause();
            } else {
                player.play();
            }
        }catch (Exception e){}
    }

    @Override
    public void NextVideo() {
        if(isStart){
            isStart = false;
            super.NextVideo();
            StartVideo();
        }
    }

    @Override
    public void BackVideo() {
        if(isStart){
            isStart = false;
            super.BackVideo();
            StartVideo();
        }
    }

    @Override
    public void SeekTo(long pos) {
        player.seekTo(ToFloat(pos));
    }

    @Override
    public int GetSessionId() {
        return 0;
    }

    @Override
    public void clouseMusic() {
        super.clouseMusic();
        release();
    }

    public void Sesions() {
        super.Sesions(getMillis(tracker.getCurrentSecond()),getMillis(tracker.getVideoDuration()));
    }
    public boolean isFullScreen(){
        return backgroundVideo.isFullScreen();
    }

    @Override
    public void exitFullScreen() {
        backgroundVideo.exitFullScreen();
    }

    public void release(){
        backgroundVideo.release();
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
    private int getMillis(float d){
        return (int)((d) * 1000);
    }
    private float ToFloat(long d){
        return (float)((d) / 1000);
    }
}
