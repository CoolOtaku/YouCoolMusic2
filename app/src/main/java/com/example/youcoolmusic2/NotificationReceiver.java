package com.example.youcoolmusic2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static com.example.youcoolmusic2.App.main_player;

public class NotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        switch(intent.getAction()){
            case "exit":
                main_player.clouseMusic();
                break;
            case "pause_play":
                main_player.Pause_Play();
                break;
            case "back_music":
                main_player.BackVideo();
                break;
            case "next_music":
                main_player.NextVideo();
                break;
            case "in_window":
                main_player.ShowWindow();
                break;
        }
    }

}
