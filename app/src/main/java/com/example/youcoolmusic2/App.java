package com.example.youcoolmusic2;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import com.example.youcoolmusic2.Adapters.SearchVideoAdapter;
import com.example.youcoolmusic2.Interfaces.Player;
import com.example.youcoolmusic2.Obg.CustomLoading;
import com.example.youcoolmusic2.Obg.CustomToast;
import com.example.youcoolmusic2.Obg.DataBase;
import com.example.youcoolmusic2.Obg.Video;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import java.util.ArrayList;

public class App extends Application {

    public static final String CHANEL_ID = "YouCoolMusic1337";
    public static final String DownloadDirectories = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/YouCoolMusicVideos/";

    public static Context Activity_Context;
    public static DataBase dataBase;
    public static SQLiteDatabase sql;
    public static SearchVideoAdapter searchVideoAdapter;
    public static Video video;
    public static ArrayList<Video> videos;
    public static CustomToast toast = new CustomToast();
    public static CustomLoading loading = new CustomLoading();
    public static SharedPreferences sp;

    public static Player main_player;
    public static Bitmap currentPoster;

    public static int play_list_id = 0;
    public static int positionArr = -1;

    public static final String API_KEY_1 = "[YOU_YouTube_API_KEY_1]";
    public static final String API_KEY_2 = "[YOU_YouTube_API_KEY_2]";
    public static final String UPDATE_URL = "https://youcoolmusicvideo.000webhostapp.com/ForYouCoolMusic2/";

    public static String SORT = "";
    public static final String SORT_ASC = " ORDER BY "+dataBase.TITLE+" ASC";
    public static final String SORT_DESC = " ORDER BY "+dataBase.TITLE+" DESC";
    public static final String SORT_RANDOM = " ORDER BY RANDOM()";

    @Override
    public void onCreate(){
        super.onCreate();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        dataBase = new DataBase(getApplicationContext());
        sql = dataBase.getWritableDatabase();
        currentPoster = BitmapFactory.decodeResource(getResources(),R.drawable.youtube);
        createNotifiChanel();
        main_player = new Player() {
            @Override
            public boolean isFullScreen() {return false;}
            @Override
            public void exitFullScreen() {}
            @Override
            public void release() {}
            @Override
            public void restart() {}
            @Override
            public void Pause_Play() {}
            @Override
            public void SeekTo(long pos) {}
            @Override
            public int GetSessionId() { return 0; }
            @Override
            public void Sesions() {}
        };
    }
    private void createNotifiChanel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(CHANEL_ID,"YouCoolMusic2", NotificationManager.IMPORTANCE_MAX);
            notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            notificationChannel.setSound(null,null);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }
    }
    public static void SavePosition(Context context){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("position",App.positionArr);
        editor.putInt("play_list_id",App.play_list_id);
        editor.commit();
    }
    public static void getCurrentPoster(Context context){
        Picasso.get().load(video.getImg()).into(new Target(){
            @Override
            public void onBitmapLoaded (final Bitmap bitmap, Picasso.LoadedFrom from) {
                currentPoster = bitmap;
            }
            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                currentPoster = BitmapFactory.decodeResource(context.getResources(),R.drawable.youtube);
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {}
        });
    }
    public static void SaveTheme(int id_theme){
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt("id_theme",id_theme);
        editor.commit();
    }
}