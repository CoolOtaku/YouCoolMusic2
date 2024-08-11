package com.example.youcoolmusic2.Interfaces;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.media.MediaMetadataCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import androidx.constraintlayout.widget.ConstraintLayout;
import com.example.youcoolmusic2.Activity.ControlPlayerActivity;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Tasks.BackgroundMusicService;
import com.example.youcoolmusic2.R;
import static com.example.youcoolmusic2.App.positionArr;
import static com.example.youcoolmusic2.App.searchVideoAdapter;
import static com.example.youcoolmusic2.App.video;
import static com.example.youcoolmusic2.App.videos;
import static com.example.youcoolmusic2.Tasks.BackgroundMusicService.mediaSession;

public abstract class Player implements PlayerInterface, View.OnClickListener, View.OnTouchListener {

    public Context context;

    public static WindowManager windowManager;
    public static WindowManager.LayoutParams layoutParams;
    public static View view;
    public static Window window;
    public static LinearLayout button_bar;
    public static Button play_pause_music;
    private ConstraintLayout video_container;

    private Button clouse_music;
    private Button back_music;
    private Button next_music;
    private Button in_statusBar;
    private LinearLayout ln;

    private int xDelta, yDelta;

    public static boolean runingService = false;
    public static boolean is_Play = false;
    public static boolean is_Hide_Video = false;

    public static int width;
    public static int height;

    private long lastClickTime = 0;

    public Player(){}
    public Player(Context context){
        this.context = context;
        runingService = true;
        if(layoutParams == null){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }else{
                layoutParams = new WindowManager.LayoutParams(
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.WRAP_CONTENT,
                        WindowManager.LayoutParams.TYPE_PHONE,
                        WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                        PixelFormat.TRANSLUCENT);
            }
            layoutParams.gravity = Gravity.CENTER | Gravity.CENTER;
            layoutParams.x = 0;
            layoutParams.y = 0;
        }
        try {
            windowManager.addView(view, layoutParams);
        }catch (Exception e){
            AlertDialog.Builder dialog = new AlertDialog.Builder(App.Activity_Context);
            dialog.setTitle(context.getString(R.string.error));
            dialog.setMessage(context.getString(R.string.start_video_error));
            dialog.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                            Uri.parse("package:" + context.getPackageName()));
                    App.Activity_Context.startActivity(intent);
                }
            });
            dialog.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog newDialog = dialog.create();
            newDialog.show();
        }
        clouse_music = (Button) view.findViewById(R.id.clouse_music);
        back_music = (Button) view.findViewById(R.id.back_music);
        play_pause_music = (Button) view.findViewById(R.id.play_pause_music);
        next_music = (Button) view.findViewById(R.id.next_music);
        in_statusBar = (Button) view.findViewById(R.id.in_statusBar);
        button_bar = (LinearLayout) view.findViewById(R.id.button_bar);
        video_container = view.findViewById(R.id.video_container);
        ln = (LinearLayout) view.findViewById(R.id.ln);

        if(is_Hide_Video){
            hide_window();
        }

        clouse_music.setOnClickListener(this);
        play_pause_music.setOnClickListener(this);
        back_music.setOnClickListener(this);
        next_music.setOnClickListener(this);
        in_statusBar.setOnClickListener(this);
        view.setOnTouchListener(this);
        button_bar.setOnTouchListener(this);
    }

    @Override
    public void StartVideo() {
        if(positionArr > videos.size()-1){
            positionArr = 0;
        }
        video = videos.get(positionArr);
        App.getCurrentPoster(context);
    }

    @Override
    public void NextVideo() {
        positionArr = videos.indexOf(video);
        video = null;
        searchVideoAdapter.notifyItemChanged(positionArr);
        if (positionArr+1 != videos.size()){
            positionArr++;
        }else{
            positionArr=0;
        }
    }

    @Override
    public void BackVideo() {
        positionArr = videos.indexOf(video);
        video = null;
        searchVideoAdapter.notifyItemChanged(positionArr);
        if(positionArr == 0){
            positionArr = videos.size()-1;
        }else{
            positionArr--;
        }
    }

    @Override
    public void clouseMusic() {
        is_Play = false;
        runingService = false;
        try {
            windowManager.removeView(view);
        }catch (Exception e){}
        if(BackgroundMusicService.notificationManager!=null) {
            BackgroundMusicService.notificationManager.cancel(1337);
        }
        Intent intent1 = new Intent(context, BackgroundMusicService.class);
        context.stopService(intent1);
        try {
            ControlPlayerActivity.play_pause_music_AC.setBackgroundResource(R.drawable.ic_play);
        }catch (Exception e){}
    }

    @Override
    public void ShowWindow() {
        is_Hide_Video = false;
        button_bar.setVisibility(View.VISIBLE);
        video_container.setVisibility(View.VISIBLE);
        try {
            ControlPlayerActivity.in_statusBar_AC.setBackgroundResource(R.drawable.ic_visibility_off);
        }catch (Exception e){}
    }

    @Override
    public void hide_window() {
        is_Hide_Video = true;
        button_bar.setVisibility(View.GONE);
        video_container.setVisibility(View.GONE);
        try {
            ControlPlayerActivity.in_statusBar_AC.setBackgroundResource(R.drawable.ic_visibility);
        }catch (Exception e){}
    }

    public static int getPx(int dp,Context context){
        float scale = context.getResources().getDisplayMetrics().density;
        return((int) (dp * scale + 0.5f));
    }

    @Override
    public void ChangedScreen(Configuration newConfig){
        if(isFullScreen()){
            WindowManager.LayoutParams lP = (WindowManager.LayoutParams) view.getLayoutParams();
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                lP.width = height;
                lP.height = width;
                video_container.setLayoutParams(new ConstraintLayout.LayoutParams(height,width-50));
            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                lP.height = height;
                lP.width = width;
                video_container.setLayoutParams(new ConstraintLayout.LayoutParams(width, height-50));
            }
            view.setLayoutParams(lP);
            windowManager.updateViewLayout(view, lP);
        }
    }

    @Override
    public void Sesions(int current,int duration){
        MediaMetadataCompat.Builder metadataBuilder = new MediaMetadataCompat.Builder();
        metadataBuilder.putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration);

        PlaybackStateCompat.Builder mStateBuilder = new PlaybackStateCompat.Builder()
                .setState(is_Play ? PlaybackStateCompat.STATE_PLAYING : PlaybackStateCompat.STATE_PAUSED,
                        current, 0)
                .setActions(PlaybackStateCompat.ACTION_SEEK_TO);

        mediaSession.setMetadata(metadataBuilder.build());
        mediaSession.setPlaybackState(mStateBuilder.build());

        try {
            ControlPlayerActivity.seekBarVideo.setMax(duration);
            ControlPlayerActivity.seekBarVideo.setProgress(current);
        }catch (Exception e){}
    };

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back_music:
                BackVideo();
                break;
            case R.id.play_pause_music:
                Pause_Play();
                break;
            case R.id.next_music:
                NextVideo();
                break;
            case R.id.in_statusBar:
                hide_window();
                break;
            case R.id.clouse_music:
                clouseMusic();
                break;
            case R.id.backgroundVideo1:
                long clickTime = System.currentTimeMillis();
                if (clickTime - lastClickTime < 500) {
                    if(isFullScreen()){
                        OflinePlayer.is_FullScreen = false;
                        exitFullScreen();
                    }else{
                        OflinePlayer.is_FullScreen = true;
                        InFullScreen();
                    }
                }
                lastClickTime = clickTime;
                break;
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {
        final int x = (int) event.getRawX();
        final int y = (int) event.getRawY();
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                WindowManager.LayoutParams lParams = (WindowManager.LayoutParams) view.getLayoutParams();
                xDelta = x - lParams.x;
                yDelta = y - lParams.y;
                break;
            case MotionEvent.ACTION_MOVE:
                layoutParams = (WindowManager.LayoutParams) view.getLayoutParams();
                layoutParams.x = x - xDelta;
                layoutParams.y = y - yDelta;
                layoutParams.gravity=Gravity.NO_GRAVITY;
                view.setLayoutParams(layoutParams);
                windowManager.updateViewLayout(view,layoutParams);
                break;
        }
        return true;
    }

    public void InFullScreen(){
        WindowManager.LayoutParams lP = (WindowManager.LayoutParams) view.getLayoutParams();
        lP.x = 0;
        lP.y = 0;
        lP.gravity = Gravity.CENTER|Gravity.CENTER;
        button_bar.setVisibility(View.GONE);
        int orientation = context.getResources().getConfiguration().orientation;
        if (orientation == Configuration.ORIENTATION_PORTRAIT) {
            lP.height = height+100;
            lP.width = width+100;
            video_container.setLayoutParams(new ConstraintLayout.LayoutParams(width, height-50));
        } else {
            lP.height = width+100;
            lP.width = height+100;
            video_container.setLayoutParams(new ConstraintLayout.LayoutParams(height,width-50));
        }
        view.setBackgroundResource(R.color.colorBlack);
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE);

        view.setLayoutParams(lP);
        windowManager.updateViewLayout(view, lP);
    }

    public void WithFullScreen(){
        WindowManager.LayoutParams lP = (WindowManager.LayoutParams) view.getLayoutParams();
        lP.x = 0;
        lP.y = 0;
        lP.gravity = Gravity.CENTER|Gravity.CENTER;

        button_bar.setVisibility(View.VISIBLE);
        lP.height = WindowManager.LayoutParams.WRAP_CONTENT;
        lP.width = WindowManager.LayoutParams.WRAP_CONTENT;
        view.setBackgroundResource(R.drawable.bottom_rounded_menu_color);
        video_container.setLayoutParams(new ConstraintLayout.LayoutParams(getPx(260,context),
                getPx(130,context)));
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);

        view.setLayoutParams(lP);
        windowManager.updateViewLayout(view, lP);
    }

    public abstract boolean isFullScreen();

    public abstract void exitFullScreen();

    public abstract void release();

    public abstract void restart();

    public abstract void Pause_Play();

    public abstract void SeekTo(long pos);

    public abstract int GetSessionId();
}
