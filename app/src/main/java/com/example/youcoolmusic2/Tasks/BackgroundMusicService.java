package com.example.youcoolmusic2.Tasks;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.IBinder;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.WindowManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Interfaces.OflinePlayer;
import com.example.youcoolmusic2.Interfaces.OnlinePlayer;
import com.example.youcoolmusic2.Interfaces.Player;
import com.example.youcoolmusic2.NotificationReceiver;
import com.example.youcoolmusic2.R;

import static com.example.youcoolmusic2.App.SavePosition;
import static com.example.youcoolmusic2.App.main_player;

public class BackgroundMusicService extends Service {

    @SuppressLint("StaticFieldLeak")
    public static NotificationManagerCompat notificationManager;
    public static MediaSessionCompat mediaSession;
    public static MediaSessionCompat.Token token;

    @Override
    public void onCreate(){
        super.onCreate();
        Player.windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        Player.view = layoutInflater.inflate(R.layout.castom_noty, null);

        try {
            if (App.videos.get(App.positionArr).isDownloads) {
                main_player = new OflinePlayer(getApplicationContext());
            } else {
                main_player = new OnlinePlayer(getApplicationContext());
            }
        }catch (Exception e){
            App.positionArr = 0;
            App.video = App.videos.get(App.positionArr);
            SavePosition(BackgroundMusicService.this);
            onCreate();
        }

        main_player.StartVideo();

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Notification notification1 = new NotificationCompat.Builder(BackgroundMusicService.this, App.CHANEL_ID)
            .setSmallIcon(R.drawable.animetan).setContentText(getString(R.string.music_player_is_started)).build();
        startForeground(1336,notification1);
        return Service.START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        System.out.println("DestroyMusic");
        Player.runingService = false;
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        main_player.ChangedScreen(newConfig);
        super.onConfigurationChanged(newConfig);
    }

    public static void showNote(Context context,String text, int playIcon){
        notificationManager = NotificationManagerCompat.from(context);
        Intent Ia1 = new Intent(context, NotificationReceiver.class).setAction("exit");
        PendingIntent pIa1  = PendingIntent.getBroadcast(context,0,Ia1,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent Ia2 = new Intent(context, NotificationReceiver.class).setAction("pause_play");
        PendingIntent pIa2  = PendingIntent.getBroadcast(context,0,Ia2,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent Ia3 = new Intent(context, NotificationReceiver.class).setAction("back_music");
        PendingIntent pIa3  = PendingIntent.getBroadcast(context,0,Ia3,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent Ia4 = new Intent(context, NotificationReceiver.class).setAction("next_music");
        PendingIntent pIa4  = PendingIntent.getBroadcast(context,0,Ia4,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent Ia5 = new Intent(context, NotificationReceiver.class).setAction("in_window");
        PendingIntent pIa5  = PendingIntent.getBroadcast(context,0,Ia5,PendingIntent.FLAG_UPDATE_CURRENT);

        try {
            mediaSession = new MediaSessionCompat(App.Activity_Context, "YouCoolMusic2");
        }catch (Exception e){}
        main_player.Sesions();
        mediaSession.setCallback(new MediaSessionCompat.Callback() {
            @Override
            public void onSeekTo(long pos) {
                main_player.SeekTo(pos);
            }
        });
        token = mediaSession.getSessionToken();


        String title = "";
        String text2 = "";
        if(text.contains("-")){
            String[] s = text.split("-",2);
            title = s[0];
            text2 = s[1];
        }else {
            title = text;
        }

        Notification notification =
                new NotificationCompat.Builder(context, App.CHANEL_ID)
                        .setSmallIcon(R.drawable.animetan)
                        .setContentTitle(title)
                        .setContentText(text2)
                        .setLargeIcon(App.currentPoster)
                        .addAction(R.drawable.ic_skip,"back_music",pIa3)
                        .addAction(playIcon,"pause_play",pIa2)
                        .addAction(R.drawable.ic_skip_next,"next_music",pIa4)
                        .addAction(R.drawable.ic_visibility,"in_window",pIa5)
                        .addAction(R.drawable.ic_close,"exit",pIa1)
                        .setStyle(new androidx.media.app.NotificationCompat.MediaStyle()
                                .setShowActionsInCompactView(0,1,2).setMediaSession(token))
                        .setPriority(NotificationCompat.PRIORITY_MAX)
                        .setOngoing(true).build();

        notificationManager.notify(1337, notification);
    }

}
