package com.example.youcoolmusic2.Interfaces;

import android.content.res.Configuration;

public interface PlayerInterface {

    void StartVideo();
    void NextVideo();
    void BackVideo();
    void clouseMusic();
    void ShowWindow();
    void hide_window();
    void Sesions(int current,int duration);
    void Sesions();
    void ChangedScreen(Configuration newConfig);

}
