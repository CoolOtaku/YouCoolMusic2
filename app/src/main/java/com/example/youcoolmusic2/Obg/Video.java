package com.example.youcoolmusic2.Obg;

import android.content.Context;
import android.widget.Toast;

import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.R;

import java.io.File;
import java.util.Objects;

public class Video {

    public String id;
    public String title;
    public String img;
    public String play_list_id;
    public boolean isDownloads = false;

    public Video(String id, String title, String img, String play_list_id) {
        this.id = id;
        this.title = title;
        this.img = img;
        this.play_list_id = play_list_id;
        CheckDownloadsFile();
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getImg() {
        return img;
    }

    public String getPlay_list_id() {
        return play_list_id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public void setPlay_list_id(String play_list_id) {
        this.play_list_id = play_list_id;
    }

    public void CheckDownloadsFile(){
        String path = App.DownloadDirectories+id+"_audio.webm";
        File f = new File(path);
        if(f.exists()&&f.isFile()){
            isDownloads = true;
        }
    }

    public void DeleteFiles(Context context){
        boolean is_delete = false;
        String path = App.DownloadDirectories+id+"_audio.webm";
        File f = new File(path);
        if(f.exists()&&f.isFile()){
            f.delete();
            is_delete = true;
        }
        String path2 = App.DownloadDirectories+id+"_video.webm";
        File f2 = new File(path2);
        if(f2.exists()&&f2.isFile()){
            f2.delete();
            is_delete = true;
        }
        if(is_delete) {
            isDownloads = false;
            Toast.makeText(context, context.getString(R.string.file_is_delete), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Video video = (Video) o;
        return Objects.equals(id, video.id) &&
                Objects.equals(title, video.title) &&
                Objects.equals(img, video.img) &&
                Objects.equals(play_list_id, video.play_list_id);
    }
}
