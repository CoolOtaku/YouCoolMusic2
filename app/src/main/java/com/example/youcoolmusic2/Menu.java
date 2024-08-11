package com.example.youcoolmusic2;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import com.example.youcoolmusic2.Activity.AboutActivity;
import com.example.youcoolmusic2.Adapters.SelectPlayListAdapter;
import com.example.youcoolmusic2.Obg.PlayListItem;
import com.example.youcoolmusic2.Obg.PlayListView;
import com.example.youcoolmusic2.Obg.Video;
import com.example.youcoolmusic2.Tasks.DownloadUpdate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import static com.example.youcoolmusic2.Activity.MainActivity.count_items;
import static com.example.youcoolmusic2.Activity.MainActivity.listVideo;
import static com.example.youcoolmusic2.Activity.MainActivity.mLinearLayoutManager;

public class Menu {

    static AlertDialog.Builder dialogBuilder;
    static AlertDialog dialog;

    @SuppressLint("ResourceType")
    public static void start(int id){
        switch (id){
            case R.id.b_export:
                dialogBuilder = new AlertDialog.Builder(App.Activity_Context);
                dialogBuilder.setTitle(App.Activity_Context.getString(R.string.b_export));
                View view = View.inflate(App.Activity_Context,R.layout.export_play_list,null);
                dialogBuilder.setView(view);
                EditText input_name_file = (EditText) view.findViewById(R.id.input_name_file);
                dialogBuilder.setPositiveButton(App.Activity_Context.getString(R.string.to_save), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String name_file = input_name_file.getText().toString();
                        if(name_file.isEmpty()){
                            App.toast.showToas(App.Activity_Context,App.Activity_Context.getString(R.string.edit_text_is_empty),false);
                            return;
                        }
                        try {
                            JSONArray array = new JSONArray();
                            for (int i = 0; i < App.videos.size();i++){
                                Video video = App.videos.get(i);
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("id",video.getId());
                                jsonObject.put("title",video.getTitle());
                                jsonObject.put("img",video.getImg());
                                array.put(jsonObject.toString(1));
                            }
                            String folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                            File file = new File(folder + "/" + name_file + ".txt");
                            file.createNewFile();
                            FileOutputStream output = new FileOutputStream(file);
                            output.write(array.toString(1).getBytes());
                            output.flush();
                            output.close();
                            App.toast.showToas(App.Activity_Context,App.Activity_Context.getString(R.string.create_play_list)+" "+name_file,true);
                        }catch (Exception e) {
                            e.printStackTrace();
                            App.toast.showToas(App.Activity_Context,App.Activity_Context.getString(R.string.error),false);
                        }
                    }
                });
                dialogBuilder.setNegativeButton(App.Activity_Context.getString(R.string.to_cancel), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog = dialogBuilder.create();
                dialog.show();
                break;
            case R.id.b_delete_play_list:
                dialogBuilder = new AlertDialog.Builder(App.Activity_Context);
                dialogBuilder.setTitle(App.Activity_Context.getString(R.string.b_delete_play_list));
                dialogBuilder.setMessage(App.Activity_Context.getString(R.string.you_exactly_want_delete_all_music));
                dialogBuilder.setPositiveButton(App.Activity_Context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        App.sql.delete(App.dataBase.TABLE_PLAY_LIST,null,null);
                        PlayListView.showPlay_List(App.Activity_Context,"",listVideo,count_items,mLinearLayoutManager);
                        dialog.cancel();
                    }
                });
                dialogBuilder.setNegativeButton(App.Activity_Context.getString(R.string.no), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog = dialogBuilder.create();
                dialog.show();
                break;
            case R.id.b_about:
                Intent intent1 = new Intent(App.Activity_Context, AboutActivity.class);
                intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                App.Activity_Context.startActivity(intent1);
                break;
            case R.id.b_update:
                new DownloadUpdate().execute();
                break;
        }
    }
    @SuppressLint("Range")
    public static void Overrayding(String data){
        dialogBuilder = new AlertDialog.Builder(App.Activity_Context);
        dialogBuilder.setTitle(App.Activity_Context.getString(R.string.select_play_list));
        View view = View.inflate(App.Activity_Context,R.layout.select_play_list,null);
        dialogBuilder.setView(view);
        dialog = dialogBuilder.create();
        ListView list_play_list = (ListView) view.findViewById(R.id.list_play_list);
        ArrayList<PlayListItem> arr_play_list = new ArrayList<>();
        arr_play_list.add(new PlayListItem(App.Activity_Context.getString(R.string.mains),0));
        try {
            Cursor cursor = App.sql.rawQuery("SELECT * FROM " + App.dataBase.TABLE_MORE_PLAY_LIST, null);
            cursor.moveToFirst();
            do {
                arr_play_list.add(new PlayListItem(cursor.getString(cursor.getColumnIndex(App.dataBase.TITLE)),
                        cursor.getInt(cursor.getColumnIndex(App.dataBase.ID))));
            } while (cursor.moveToNext());
        }catch (Exception e){e.printStackTrace();}
        SelectPlayListAdapter selectPlayListAdapter = new SelectPlayListAdapter(App.Activity_Context,arr_play_list);
        list_play_list.setAdapter(selectPlayListAdapter);
        list_play_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = App.sql.rawQuery("SELECT "+App.dataBase.ID+", "+App.dataBase.PLAY_LIST_ID+" FROM "
                        +App.dataBase.TABLE_PLAY_LIST+" WHERE "+ App.dataBase.PLAY_LIST_ID
                        +" LIKE '["+arr_play_list.get(position).getId()+"]' OR " + App.dataBase.PLAY_LIST_ID
                        +" LIKE '["+arr_play_list.get(position).getId()+",%' OR "+
                        App.dataBase.PLAY_LIST_ID+" LIKE '%,"+arr_play_list.get(position).getId()+",%' OR "+App.dataBase.PLAY_LIST_ID
                        +" LIKE '%,"+arr_play_list.get(position).getId()+"]'", null);
                cursor.moveToFirst();
                do{
                    try{
                        JSONArray array = new JSONArray(cursor.getString(cursor.getColumnIndex(App.dataBase.PLAY_LIST_ID)));
                        for(int i = 0;i < array.length();i++){
                            if(array.getInt(i) == arr_play_list.get(position).getId()){
                                array.remove(i);
                            }
                        }
                        ContentValues values = new ContentValues();
                        values.put(App.dataBase.PLAY_LIST_ID,array.toString());
                        App.sql.update(App.dataBase.TABLE_PLAY_LIST, values, App.dataBase.ID
                                +" LIKE '"+cursor.getString(cursor.getColumnIndex(App.dataBase.ID))+"'", null);
                    }catch (Exception e) {e.printStackTrace();}
                }while(cursor.moveToNext());
                cursor.close();
                JSONArray items = null;
                try {
                    items = new JSONArray(data);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                for (int i = 0; i < items.length(); i++) {
                    ContentValues contentValues = new ContentValues();
                    try {
                        JSONObject x = new JSONObject(items.getString(i));
                        contentValues.put(App.dataBase.ID, x.getString("id"));
                        contentValues.put(App.dataBase.TITLE, x.getString("title"));
                        contentValues.put(App.dataBase.IMG, x.getString("img"));
                    }catch (Exception e) {e.printStackTrace();}
                    JSONArray array = new JSONArray();
                    array.put(arr_play_list.get(position).getId());
                    contentValues.put(App.dataBase.PLAY_LIST_ID,array.toString());
                    boolean isContains = true;
                    try {
                        cursor = App.sql.rawQuery("SELECT " + App.dataBase.PLAY_LIST_ID + " FROM "
                                + App.dataBase.TABLE_PLAY_LIST + " WHERE " + App.dataBase.ID + " LIKE '"
                                + contentValues.getAsString(App.dataBase.ID) + "'", null);
                        cursor.moveToFirst();
                        array = new JSONArray(cursor.getString(cursor.getColumnIndex(App.dataBase.PLAY_LIST_ID)));
                    }catch (Exception e){
                        isContains = false;
                    }
                    if(isContains){
                        array.put(arr_play_list.get(position).getId());
                        contentValues.remove(App.dataBase.PLAY_LIST_ID);
                        contentValues.put(App.dataBase.PLAY_LIST_ID, array.toString());
                        App.sql.update(App.dataBase.TABLE_PLAY_LIST, contentValues, App.dataBase.ID
                                +" LIKE '" + contentValues.getAsString(App.dataBase.ID) + "'", null);
                    }else {
                        App.sql.insert(App.dataBase.TABLE_PLAY_LIST, null, contentValues);
                    }
                }
                dialog.cancel();
                App.toast.showToas(App.Activity_Context, App.Activity_Context.getString(R.string.play_list_overading), true);
                PlayListView.showPlay_List(App.Activity_Context,"",listVideo,count_items,mLinearLayoutManager);
            }
        });
        dialog.show();
    }
    public static void AddPlayList(String data){
        try {
            dialogBuilder = new AlertDialog.Builder(App.Activity_Context);
            dialogBuilder.setTitle(App.Activity_Context.getString(R.string.add_play_list));
            View view = View.inflate(App.Activity_Context, R.layout.export_play_list, null);
            dialogBuilder.setView(view);
            EditText input_name_play_list = (EditText) view.findViewById(R.id.input_name_file);
            input_name_play_list.setHint(App.Activity_Context.getString(R.string.name_play_list));
            dialogBuilder.setPositiveButton(App.Activity_Context.getString(R.string.to_save), new DialogInterface.OnClickListener() {
                @SuppressLint("Range")
                public void onClick(DialogInterface dialog, int which) {
                    String name_play_list = input_name_play_list.getText().toString();
                    if (name_play_list.isEmpty()) {
                        App.toast.showToas(App.Activity_Context, App.Activity_Context.getString(R.string.edit_text_is_empty), false);
                        return;
                    }
                    int id = 0;
                    try {
                        Cursor cursor = App.sql.rawQuery("SELECT " + App.dataBase.ID + " FROM "
                                + App.dataBase.TABLE_MORE_PLAY_LIST + " WHERE " + App.dataBase.TITLE + " LIKE '"
                                + name_play_list + "'", null);
                        cursor.moveToFirst();
                        id = cursor.getInt(cursor.getColumnIndex(App.dataBase.ID));
                        cursor.close();
                        if (id != 0) {
                            App.toast.showToas(App.Activity_Context, App.Activity_Context.getString(R.string.this_play_list_is_contains), false);
                            return;
                        }
                    }catch (Exception e) {e.printStackTrace();}
                    ContentValues contentValues = new ContentValues();
                    contentValues.put(App.dataBase.TITLE, input_name_play_list.getText().toString());
                    App.sql.insert(App.dataBase.TABLE_MORE_PLAY_LIST, null, contentValues);
                    Cursor cursor = App.sql.rawQuery("SELECT " + App.dataBase.ID + " FROM "
                            + App.dataBase.TABLE_MORE_PLAY_LIST + " WHERE " + App.dataBase.TITLE + " LIKE '"
                            + name_play_list + "'", null);
                    cursor.moveToFirst();
                    id = cursor.getInt(cursor.getColumnIndex(App.dataBase.ID));
                    JSONArray items = null;
                    try {
                        items = new JSONArray(data);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    for (int i = 0; i < items.length(); i++) {
                        contentValues = new ContentValues();
                        try {
                            JSONObject x = new JSONObject(items.getString(i));
                            contentValues.put(App.dataBase.ID, x.getString("id"));
                            contentValues.put(App.dataBase.TITLE, x.getString("title"));
                            contentValues.put(App.dataBase.IMG, x.getString("img"));
                        }catch (JSONException e) {e.printStackTrace();}
                        JSONArray array = new JSONArray();
                        array.put(id);
                        contentValues.put(App.dataBase.PLAY_LIST_ID, array.toString());
                        boolean isContains = true;
                        try {
                            cursor = App.sql.rawQuery("SELECT " + App.dataBase.PLAY_LIST_ID + " FROM "
                                    + App.dataBase.TABLE_PLAY_LIST + " WHERE " + App.dataBase.ID + " LIKE '"
                                    + contentValues.getAsString(App.dataBase.ID) + "'", null);
                            cursor.moveToFirst();
                            array = new JSONArray(cursor.getString(cursor.getColumnIndex(App.dataBase.PLAY_LIST_ID)));
                        }catch (Exception e){
                            isContains = false;
                        }
                        if(isContains){
                            array.put(id);
                            contentValues.remove(App.dataBase.PLAY_LIST_ID);
                            contentValues.put(App.dataBase.PLAY_LIST_ID, array.toString());
                            App.sql.update(App.dataBase.TABLE_PLAY_LIST, contentValues, App.dataBase.ID
                                    + " LIKE '" + contentValues.getAsString(App.dataBase.ID) + "'", null);
                        }else {
                            App.sql.insert(App.dataBase.TABLE_PLAY_LIST, null, contentValues);
                        }
                    }
                    cursor.close();
                    PlayListView.showPlay_List(App.Activity_Context,"",listVideo,count_items,mLinearLayoutManager);
                    App.toast.showToas(App.Activity_Context, App.Activity_Context.getString(R.string.play_list_add), true);
                }
            });
            dialogBuilder.setNegativeButton(App.Activity_Context.getString(R.string.to_cancel), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            dialog = dialogBuilder.create();
            dialog.show();
        }catch (Exception e){
            e.printStackTrace();
            App.toast.showToas(App.Activity_Context, App.Activity_Context.getString(R.string.error), false);
        }
    }
}
