package com.example.youcoolmusic2.Adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;
import com.example.youcoolmusic2.Interfaces.Player;
import com.example.youcoolmusic2.Obg.NetWork;
import com.example.youcoolmusic2.Obg.PlayListItem;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Tasks.BackgroundMusicService;
import com.example.youcoolmusic2.R;
import com.example.youcoolmusic2.Tasks.RequestDownloadVideoStream;
import com.example.youcoolmusic2.Obg.Video;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import static android.content.Context.CLIPBOARD_SERVICE;

public class SearchVideoAdapter extends RecyclerView.Adapter<SearchVideoAdapter.ExampleViewHolder> {

    private Context context;
    private Cursor cursor;
    private Intent intent;

    public boolean isVisibleDownloads = false;

    public static class ExampleViewHolder extends RecyclerView.ViewHolder {
        ImageView imageVideo;
        TextView nameView;
        Button addVideo;
        ConstraintLayout bgVideo;

        public ExampleViewHolder(View itemView) {
            super(itemView);
            imageVideo = itemView.findViewById(R.id.imageVideo);
            nameView = itemView.findViewById(R.id.nameView);
            addVideo = itemView.findViewById(R.id.addVideo);
            bgVideo = itemView.findViewById(R.id.bgVideo);
        }
    }

    public SearchVideoAdapter(Context context){
        this.context=context;
    }

    @NonNull
    @Override
    public ExampleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.videolist, parent, false);
        ExampleViewHolder evh = new ExampleViewHolder(v);
        return evh;
    }

    @SuppressLint("Range")
    @Override
    public void onBindViewHolder(final ExampleViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final Video video = App.videos.get(position);
        if(!video.equals(App.video)) {
            holder.bgVideo.setSelected(false);
        }else{
            holder.bgVideo.setSelected(true);
            App.positionArr = position;
        }
        boolean isContains;
        try {
            cursor = App.sql.rawQuery("SELECT " + App.dataBase.ID + " FROM " + App.dataBase.TABLE_PLAY_LIST + " WHERE "
                    + App.dataBase.ID + " = '" + video.getId() + "'", null);
            String localId = "";
            cursor.moveToFirst();
            localId = cursor.getString(cursor.getColumnIndex(App.dataBase.ID));
            cursor.close();
            if(!localId.isEmpty()){
                isContains = true;
            }else{
                isContains = false;
            }
        }catch (Exception e){
            isContains = false;
        }

        holder.nameView.setText(video.getTitle());

        if(isContains){
            Picasso.get().load(video.getImg()).placeholder(R.drawable.youtube).into(holder.imageVideo);
            holder.addVideo.setBackgroundResource(R.drawable.ic_more);
            if(video.isDownloads){
                holder.addVideo.setBackgroundResource(R.drawable.ic_more_green);
            }else{
                holder.addVideo.setBackgroundResource(R.drawable.ic_more);
            }
            holder.addVideo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PopupMenu popup = new PopupMenu(context,v);
                    MenuInflater inflater = popup.getMenuInflater();
                    inflater.inflate(R.menu.setinds_menu,popup.getMenu());
                    if(video.isDownloads){
                        popup.getMenu().removeItem(R.id.pm_download_video);
                        popup.getMenu().add(1,R.id.pm_delete_file,1,context.getString(R.string.delete_file));
                    }
                    popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                        @SuppressLint("Range")
                        @Override
                        public boolean onMenuItemClick(MenuItem item) {
                            switch (item.getItemId()){
                                case R.id.pm_copy_url_video:
                                    ClipboardManager cm = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                                    String videoUrl = "https://youtu.be/"+video.getId();
                                    ClipData cd = ClipData.newPlainText(videoUrl,videoUrl);
                                    cm.setPrimaryClip(cd);
                                    Toast.makeText(context,context.getString(R.string.copy_link_to_video),Toast.LENGTH_LONG).show();
                                    return true;
                                case R.id.pm_copy_video_name:
                                    ClipboardManager cm1 = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
                                    String videoName = video.getTitle();
                                    ClipData cd1 = ClipData.newPlainText(videoName,videoName);
                                    cm1.setPrimaryClip(cd1);
                                    Toast.makeText(context,context.getString(R.string.copy_name_on_video),Toast.LENGTH_LONG).show();
                                    return true;
                                case R.id.pm_download_video:
                                    if(NetWork.hasConnection(context)) {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                        dialog.setTitle(context.getString(R.string.select_metod_in_download));
                                        View view = View.inflate(context,R.layout.select_metod_in_download,null);
                                        dialog.setView(view);
                                        AlertDialog newDialog = dialog.create();
                                        Button from_video = (Button) view.findViewById(R.id.from_video);
                                        from_video.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                new getUrlsForVideo(video.getId(),context,position).execute("1");
                                                newDialog.cancel();
                                            }
                                        });
                                        Button not_video = (Button) view.findViewById(R.id.not_video);
                                        not_video.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                new getUrlsForVideo(video.getId(),context,position).execute("0");
                                                newDialog.cancel();
                                            }
                                        });
                                        newDialog.show();
                                    }else{
                                        App.toast.showToas(context,context.getString(R.string.error_internet),false);
                                    }
                                    return true;
                                case R.id.pm_move_to_playlist:
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                                    dialog.setTitle(context.getString(R.string.select_play_list));
                                    View view = View.inflate(context,R.layout.select_play_list,null);
                                    dialog.setView(view);
                                    AlertDialog newDialog = dialog.create();
                                    ListView list_play_list = (ListView) view.findViewById(R.id.list_play_list);
                                    ArrayList<PlayListItem> arr_play_list = new ArrayList<>();
                                    arr_play_list.add(new PlayListItem(context.getString(R.string.mains),0));
                                    try {
                                        cursor = App.sql.rawQuery("SELECT * FROM " + App.dataBase.TABLE_MORE_PLAY_LIST, null);
                                        cursor.moveToFirst();
                                        do {
                                            arr_play_list.add(new PlayListItem(cursor.getString(cursor.getColumnIndex(App.dataBase.TITLE)),
                                                    cursor.getInt(cursor.getColumnIndex(App.dataBase.ID))));
                                        } while (cursor.moveToNext());
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    SelectPlayListAdapter selectPlayListAdapter = new SelectPlayListAdapter(context,arr_play_list);
                                    list_play_list.setAdapter(selectPlayListAdapter);
                                    list_play_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        @Override
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                            ContentValues contentValues = new ContentValues();
                                            JSONArray pljsa = new JSONArray();
                                            pljsa.put(arr_play_list.get(position).getId());
                                            contentValues.put(App.dataBase.PLAY_LIST_ID, pljsa.toString());
                                            App.sql.update(App.dataBase.TABLE_PLAY_LIST,contentValues,App.dataBase.ID
                                                    + " = '"+video.getId()+"'",null);
                                            Toast.makeText(context,context.getString(R.string.video_is_to_move_other_playlist),Toast.LENGTH_LONG).show();
                                            if(App.play_list_id!=arr_play_list.get(position).getId()){
                                                int newPos = holder.getAdapterPosition();
                                                if(!App.videos.isEmpty()) {
                                                    App.videos.remove(newPos);
                                                }
                                                notifyItemRemoved(newPos);
                                            }
                                            newDialog.cancel();
                                        }
                                    });
                                    newDialog.show();
                                    return true;
                                case R.id.pm_delete_file:
                                    video.DeleteFiles(context);
                                    notifyItemChanged(position);
                                    return true;
                                case R.id.pm_delete_video:
                                    AlertDialog.Builder dialog1 = new AlertDialog.Builder(context);
                                    dialog1.setTitle(context.getString(R.string.delete_video));
                                    dialog1.setMessage(context.getString(R.string.you_exactly_want_delete_video));
                                    dialog1.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            App.sql.delete(App.dataBase.TABLE_PLAY_LIST, App.dataBase.ID
                                                    + " = '"+video.getId()+"'",null);
                                            video.DeleteFiles(context);
                                            int newPos = holder.getAdapterPosition();
                                            if(!App.videos.isEmpty()) {
                                                App.videos.remove(newPos);
                                            }
                                            notifyItemRemoved(newPos);
                                        }
                                    });
                                    dialog1.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.cancel();
                                        }
                                    });
                                    AlertDialog newDialog1 = dialog1.create();
                                    newDialog1.show();
                                    return true;
                                default:
                                    return false;
                            }
                        }
                    });
                    popup.show();
                }
            });
        }else{
            Picasso.get().load(video.getImg())
                    .networkPolicy(NetworkPolicy.NO_CACHE).memoryPolicy(MemoryPolicy.NO_CACHE)
                    .placeholder(R.drawable.youtube).into(holder.imageVideo);
            holder.addVideo.setBackgroundResource(R.drawable.ic_add);
            holder.addVideo.setOnClickListener(new View.OnClickListener() {
                @SuppressLint("Range")
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setTitle(context.getString(R.string.select_play_list));
                    View view = View.inflate(context,R.layout.select_play_list,null);
                    dialog.setView(view);
                    AlertDialog newDialog = dialog.create();
                    ListView list_play_list = (ListView) view.findViewById(R.id.list_play_list);
                    ArrayList<PlayListItem> arr_play_list = new ArrayList<>();
                    arr_play_list.add(new PlayListItem(context.getString(R.string.mains),0));
                    try {
                        cursor = App.sql.rawQuery("SELECT * FROM " + App.dataBase.TABLE_MORE_PLAY_LIST, null);
                        cursor.moveToFirst();
                        do {
                            arr_play_list.add(new PlayListItem(cursor.getString(cursor.getColumnIndex(App.dataBase.TITLE)),
                                    cursor.getInt(cursor.getColumnIndex(App.dataBase.ID))));
                        } while (cursor.moveToNext());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    SelectPlayListAdapter selectPlayListAdapter = new SelectPlayListAdapter(context,arr_play_list);
                    list_play_list.setAdapter(selectPlayListAdapter);
                    list_play_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(App.dataBase.ID, video.getId());
                            contentValues.put(App.dataBase.TITLE, video.getTitle());
                            contentValues.put(App.dataBase.IMG, video.getImg());
                            JSONArray pljsa = new JSONArray();
                            pljsa.put(arr_play_list.get(position).getId());
                            contentValues.put(App.dataBase.PLAY_LIST_ID, pljsa.toString());
                            App.sql.insert(App.dataBase.TABLE_PLAY_LIST, null, contentValues);
                            Toast.makeText(context,context.getString(R.string.successfully_append_video),Toast.LENGTH_LONG).show();
                            notifyItemChanged(holder.getAdapterPosition());
                            newDialog.cancel();
                        }
                    });
                    newDialog.show();
                }
            });
        }
        View.OnClickListener START = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(App.positionArr!=-1){
                    App.video = null;
                    notifyItemChanged(App.positionArr);
                }
                StartVideo(position);
            }
        };
        holder.imageVideo.setOnClickListener(START);
        holder.nameView.setOnClickListener(START);
    }
    private void StartVideo(int p){
        App.Activity_Context = context;
        App.positionArr = p;
        intent = new Intent(context, BackgroundMusicService.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Player.runingService) {
            Player.is_Play = false;
            Player.runingService = false;
            App.main_player.release();
            Player.windowManager.removeView(Player.view);
            context.stopService(intent);
        }
        ContextCompat.startForegroundService(context,intent);
    }
    private class getUrlsForVideo extends AsyncTask<String, Void, String> {
        String name;
        Context context;
        int pos;
        public getUrlsForVideo(String name, Context context,int pos){
            this.name = name;
            this.context = context;
            this.pos = pos;
        }
        @Override
        protected void onPreExecute() {
            App.loading.Start((Activity) context);
        }
        @Override
        protected String doInBackground(String... strings) {
            try {
                if(!Python.isStarted()){
                    Python.start(new AndroidPlatform(context));
                }
                Python py = Python.getInstance();
                PyObject object1 = py.getModule("script");
                PyObject object2 = object1.callAttr("main",name);

                JSONObject jsonObject = new JSONObject(object2.toString());
                if(strings[0].equals("1")){
                    jsonObject.put("from_video",true);
                }else{
                    jsonObject.put("from_video",false);
                }
                return jsonObject.toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            App.loading.Close();
            if(!result.isEmpty()) {
                try {
                    JSONObject object = new JSONObject(result);
                    RequestDownloadVideoStream downloadVideoStream = new RequestDownloadVideoStream(context,pos);
                    if(object.getBoolean("from_video")){
                        downloadVideoStream.execute(object.getString("audio"), name, object.getString("video"));
                    }else{
                        downloadVideoStream.execute(object.getString("audio"), name, null);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }
    public void VisibleDownloads(){
        for(int i = getItemCount()-1; i >= 0; i--){
            if(!App.videos.get(i).isDownloads) {
                App.videos.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    @Override
    public int getItemCount() {
        return App.videos.size();
    }
}