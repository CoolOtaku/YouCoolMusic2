package com.example.youcoolmusic2.Obg;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.youcoolmusic2.Adapters.SearchVideoAdapter;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.R;

import java.util.ArrayList;

public class PlayListView {

    @SuppressLint("Range")
    public static boolean showPlay_List(Context context, String search, RecyclerView listVideo, TextView count_items, LinearLayoutManager mLinearLayoutManager){
        try {
            App.videos = new ArrayList<>();
            App.play_list_id = App.sp.getInt("play_list_id",0);
            String request = "SELECT * FROM " + App.dataBase.TABLE_PLAY_LIST;
            String req_search = null;
            if(!search.isEmpty()){
                req_search = App.dataBase.TITLE+" LIKE '%"+search+"%' OR " +App.dataBase.TITLE+" LIKE '"+search.charAt(0);
                if(search.length()>2) {
                    req_search+=search.charAt(0) + search.charAt(1) +search.charAt(2) +"%' OR " + App.dataBase.TITLE + " LIKE '% "
                            + search.charAt(0) + search.charAt(1) +search.charAt(2) + "%'";
                }else{
                    req_search+="%'";
                }
            }
            if(App.play_list_id!=0) {
                request+= " WHERE " + App.dataBase.PLAY_LIST_ID + " LIKE '[" + App.play_list_id + "]' OR "
                        + App.dataBase.PLAY_LIST_ID + " LIKE '[" + App.play_list_id + ",%' OR " + App.dataBase.PLAY_LIST_ID
                        + " LIKE '%," + App.play_list_id + ",%' OR " + App.dataBase.PLAY_LIST_ID + " LIKE '%," + App.play_list_id
                        + "]' ";
                if(!search.isEmpty()){
                    request+=" AND"+req_search;
                }
            }else{
                if(!search.isEmpty()){
                    request+=" WHERE "+req_search;
                }
            }
            Cursor cursor = App.sql.rawQuery(request + App.SORT, null);
            if (cursor.moveToFirst()) {
                do {
                    App.videos.add(new Video(cursor.getString(cursor.getColumnIndex(App.dataBase.ID)),
                            cursor.getString(cursor.getColumnIndex(App.dataBase.TITLE)),
                            cursor.getString(cursor.getColumnIndex(App.dataBase.IMG)),
                            cursor.getString(cursor.getColumnIndex(App.dataBase.PLAY_LIST_ID))));
                } while (cursor.moveToNext());
            }
            cursor.close();
            App.searchVideoAdapter = new SearchVideoAdapter(context);
            listVideo.setAdapter(App.searchVideoAdapter);
            count_items.setText(context.getString(R.string.count_items)+": "+App.videos.size());
            App.positionArr = App.sp.getInt("position",0);
            mLinearLayoutManager.scrollToPosition(App.positionArr);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }
}
