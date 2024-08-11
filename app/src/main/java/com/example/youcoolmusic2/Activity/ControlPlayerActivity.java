package com.example.youcoolmusic2.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Tasks.BackgroundMusicService;
import com.example.youcoolmusic2.Interfaces.Player;
import com.example.youcoolmusic2.R;
import com.gauravk.audiovisualizer.visualizer.CircleLineVisualizer;
import de.hdodenhof.circleimageview.CircleImageView;

public class ControlPlayerActivity extends AppCompatActivity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    private ImageView logo;
    public static SeekBar seekBarVideo;
    public static CircleImageView img_video;
    public static Button back_music_AC;
    public static Button play_pause_music_AC;
    public static Button next_music_AC;
    public static Button in_statusBar_AC;
    public static Button clouse_music_AC;
    public static CircleLineVisualizer MusicVisualizer;
    public static TextView name_music_AC;

    private int progress = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(App.sp.getInt("id_theme",R.style.AppThemeDark));
        setContentView(R.layout.activity_control_player);

        logo = (ImageView) findViewById(R.id.logo);
        seekBarVideo = (SeekBar) findViewById(R.id.seekBarVideo);
        img_video = (CircleImageView) findViewById(R.id.img_video);
        back_music_AC = (Button) findViewById(R.id.back_music_AC);
        play_pause_music_AC = (Button) findViewById(R.id.play_pause_music_AC);
        next_music_AC = (Button) findViewById(R.id.next_music_AC);
        in_statusBar_AC = (Button) findViewById(R.id.in_statusBar_AC);
        clouse_music_AC = (Button) findViewById(R.id.clouse_music_AC);
        name_music_AC = (TextView) findViewById(R.id.name_music_AC);

        if (!App.main_player.is_Hide_Video) {
            in_statusBar_AC.setBackgroundResource(R.drawable.ic_visibility_off);
        }else {
            in_statusBar_AC.setBackgroundResource(R.drawable.ic_visibility);
        }

        CreateMusicVisualizer();

        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.ratate_disck);
        img_video.startAnimation(anim);

        try {
            img_video.setImageBitmap(blurRenderScript(App.Activity_Context,App.currentPoster, 15));
            play_pause_music_AC.setBackground(App.main_player.play_pause_music.getBackground());
            name_music_AC.setText(App.video.getTitle());
        }catch (Exception e){
            Start();
        }

        logo.setOnClickListener(this);
        back_music_AC.setOnClickListener(this);
        play_pause_music_AC.setOnClickListener(this);
        next_music_AC.setOnClickListener(this);
        in_statusBar_AC.setOnClickListener(this);
        clouse_music_AC.setOnClickListener(this);

        seekBarVideo.setOnSeekBarChangeListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.logo:
                super.onBackPressed();
                break;
            case R.id.clouse_music_AC:
                try {
                    App.main_player.clouseMusic();
                }catch (Exception e){}
                break;
            case R.id.play_pause_music_AC:
                if(!App.main_player.runingService){
                    Start();
                }else {
                    App.main_player.Pause_Play();
                }
                break;
            case R.id.back_music_AC:
                App.main_player.BackVideo();
                break;
            case R.id.next_music_AC:
                App.main_player.NextVideo();
                break;
            case R.id.in_statusBar_AC:
                try {
                    if (App.main_player.is_Hide_Video) {
                        App.main_player.ShowWindow();
                        in_statusBar_AC.setBackgroundResource(R.drawable.ic_visibility_off);
                    } else {
                        App.main_player.hide_window();
                        in_statusBar_AC.setBackgroundResource(R.drawable.ic_visibility);
                    }
                }catch (Exception e){}
                break;
        }
    }
    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        progress = i;
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) { }
    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        App.main_player.SeekTo(progress);
    }

    private void Start(){
        App.positionArr = App.sp.getInt("position",0);
        Intent intent = new Intent(ControlPlayerActivity.this, BackgroundMusicService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Player.runingService) {
            Player.is_Play = false;
            Player.runingService = false;
            App.main_player.release();
            Player.windowManager.removeView(Player.view);
            stopService(intent);
        }
        ContextCompat.startForegroundService(ControlPlayerActivity.this,intent);
    }
    private void CreateMusicVisualizer(){
        MusicVisualizer = (CircleLineVisualizer) findViewById(R.id.MusicVisualizer);
        MusicVisualizer.setDrawLine(true);
    }

    @Override
    public void onStart() {
        App.Activity_Context = ControlPlayerActivity.this;
        try {
            MusicVisualizer.setAudioSessionId(App.main_player.GetSessionId());
        }catch (Exception e){
            MusicVisualizer.release();
            CreateMusicVisualizer();
            try {
                MusicVisualizer.setAudioSessionId(App.main_player.GetSessionId());
            }catch (Exception e1){}
        }
        Player.window = getWindow();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        MusicVisualizer.release();
        super.onDestroy();
    }

    @SuppressLint("NewApi")
    public static Bitmap blurRenderScript(Context context, Bitmap smallBitmap, int radius) {
        try {
            smallBitmap = RGB565toARGB888(smallBitmap);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Bitmap bitmap = Bitmap.createBitmap(
                smallBitmap.getWidth(), smallBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        RenderScript renderScript = RenderScript.create(context);

        Allocation blurInput = Allocation.createFromBitmap(renderScript, smallBitmap);
        Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
                Element.U8_4(renderScript));
        blur.setInput(blurInput);
        blur.setRadius(radius);
        blur.forEach(blurOutput);

        blurOutput.copyTo(bitmap);
        renderScript.destroy();

        return bitmap;

    }
    private static Bitmap RGB565toARGB888(Bitmap img) throws Exception {
        int numPixels = img.getWidth() * img.getHeight();
        int[] pixels = new int[numPixels];
        img.getPixels(pixels, 0, img.getWidth(), 0, 0, img.getWidth(), img.getHeight());
        Bitmap result = Bitmap.createBitmap(img.getWidth(), img.getHeight(), Bitmap.Config.ARGB_8888);
        result.setPixels(pixels, 0, result.getWidth(), 0, 0, result.getWidth(), result.getHeight());
        return result;
    }
}