package com.example.youcoolmusic2.Activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.youcoolmusic2.Adapters.PlayListsAdapter;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Obg.Permissions;
import com.example.youcoolmusic2.Tasks.CheckUpdate;
import com.example.youcoolmusic2.Interfaces.Player;
import com.example.youcoolmusic2.Menu;
import com.example.youcoolmusic2.Obg.NetWork;
import com.example.youcoolmusic2.Obg.PlayListItem;
import com.example.youcoolmusic2.Obg.PlayListView;
import com.example.youcoolmusic2.R;
import com.google.android.material.navigation.NavigationView;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private ImageView logo;
    private ImageView searchButton;
    private ImageView Play_Lists;
    private ImageView ButtonPlayerControl;
    private ImageView ButtonVisibleDownloads;
    private TextView textSort;
    public static TextView count_items;
    private ImageView imageSort;
    public static RecyclerView listVideo;
    public static LinearLayoutManager mLinearLayoutManager;
    private DrawerLayout drawer;
    private Cursor cursor;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(App.sp.getInt("id_theme",R.style.AppThemeDark));
        setContentView(R.layout.activity_main);
        Permissions.Start(MainActivity.this);

        App.Activity_Context = MainActivity.this;
        drawer = findViewById(R.id.drawer_layout);
        final NavigationView menu = (NavigationView) findViewById(R.id.nav_view);
        listVideo = (RecyclerView) findViewById(R.id.listVideo);
        mLinearLayoutManager = new LinearLayoutManager(this);
        listVideo.setLayoutManager( mLinearLayoutManager);
        logo = (ImageView)findViewById(R.id.logo);
        searchButton = (ImageView)findViewById(R.id.searchButton);
        Play_Lists = (ImageView)findViewById(R.id.Play_Lists);
        ButtonPlayerControl = (ImageView) findViewById(R.id.ButtonPlayerControl);
        ButtonVisibleDownloads = (ImageView) findViewById(R.id.ButtonVisibleDownloads);
        textSort = (TextView) findViewById(R.id.textSort);
        count_items = (TextView) findViewById(R.id.count_items);
        imageSort = (ImageView) findViewById(R.id.imageSort);

        StartShow();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        Player.width = size.x;
        Player.height = size.y;

        logo.setOnClickListener(this);
        searchButton.setOnClickListener(this);
        Play_Lists.setOnClickListener(this);
        ButtonPlayerControl.setOnClickListener(this);
        ButtonVisibleDownloads.setOnClickListener(this);
        textSort.setOnClickListener(this);
        imageSort.setOnClickListener(this);
        menu.setNavigationItemSelectedListener(this);

        new CheckUpdate(MainActivity.this).execute();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.logo:
                drawer.openDrawer(GravityCompat.START);
                break;
            case R.id.searchButton:
                PopupMenu popup = new PopupMenu(MainActivity.this,v);
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.search_menu,popup.getMenu());
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.pm_search_in_play_list:
                                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                dialog.setTitle(getString(R.string.search));
                                View view = View.inflate(MainActivity.this,R.layout.dialog_for_search,null);
                                dialog.setView(view);
                                AlertDialog newDialog = dialog.create();
                                EditText input_text_search = view.findViewById(R.id.input_text_search);
                                ImageView searchButton_inDialog = view.findViewById(R.id.searchButton_inDialog);
                                searchButton_inDialog.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String search_text = input_text_search.getText().toString();
                                        PlayListView.showPlay_List(MainActivity.this,search_text,listVideo,count_items,mLinearLayoutManager);
                                        try {
                                            App.positionArr = 0;
                                            App.video = App.videos.get(App.positionArr);
                                            App.SavePosition(MainActivity.this);
                                        }catch (Exception e){
                                            App.toast.showToas(MainActivity.this, getString(R.string.error_search), false);
                                            PlayListView.showPlay_List(MainActivity.this,"",listVideo,count_items,mLinearLayoutManager);
                                        }
                                        newDialog.dismiss();
                                    }
                                });
                                newDialog.show();
                                return true;
                            case R.id.pm_search_in_internet:
                                if(NetWork.hasConnection(MainActivity.this)) {
                                    Intent intent1 = new Intent();
                                    intent1.setClass(MainActivity.this, SearchActivity.class);
                                    startActivity(intent1);
                                }else{
                                    App.toast.showToas(MainActivity.this,getString(R.string.error_internet),false);
                                }
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup.show();
                break;
            case R.id.Play_Lists:
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setTitle(getString(R.string.add_play_list));
                View view = View.inflate(MainActivity.this,R.layout.play_lists,null);
                dialog.setView(view);
                AlertDialog newDialog = dialog.create();
                EditText input_name_play_list = view.findViewById(R.id.input_name_play_list);
                Button button_add_play_list = view.findViewById(R.id.button_add_play_list);
                RecyclerView list_play_list = (RecyclerView) view.findViewById(R.id.list_play_list);
                LinearLayoutManager llm = new LinearLayoutManager(MainActivity.this);
                list_play_list.setLayoutManager(llm);
                ArrayList<PlayListItem> arr_play_list = new ArrayList<>();
                button_add_play_list.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(App.dataBase.TITLE, input_name_play_list.getText().toString());
                        App.sql.insert(App.dataBase.TABLE_MORE_PLAY_LIST,null,contentValues);
                        arr_play_list.clear();
                        Show_Play_Lists(arr_play_list,list_play_list,newDialog);
                    }
                });
                Show_Play_Lists(arr_play_list,list_play_list,newDialog);
                newDialog.show();
                break;
            case R.id.ButtonPlayerControl:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, ControlPlayerActivity.class);
                startActivity(intent);
                break;
            case R.id.ButtonVisibleDownloads:
                if(!App.searchVideoAdapter.isVisibleDownloads) {
                    App.searchVideoAdapter.isVisibleDownloads = true;
                    App.searchVideoAdapter.VisibleDownloads();
                    App.positionArr = 0;
                    App.video = App.videos.get(App.positionArr);
                    App.SavePosition(MainActivity.this);
                    count_items.setText(getString(R.string.count_items)+": "+App.videos.size());
                    ButtonVisibleDownloads.setImageResource(R.drawable.ic_download_green);
                }else{
                    App.searchVideoAdapter.isVisibleDownloads = false;
                    ButtonVisibleDownloads.setImageResource(R.drawable.ic_download);
                    StartShow();
                }
                break;
            case R.id.textSort:
            case R.id.imageSort:
                PopupMenu popup1 = new PopupMenu(MainActivity.this,v);
                MenuInflater inflater1 = popup1.getMenuInflater();
                inflater1.inflate(R.menu.sort_menu,popup1.getMenu());
                popup1.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.pm_by_usual:
                                App.SORT = "";
                                StartShow();
                                return true;
                            case R.id.pm_by_growing_up:
                                App.SORT = App.SORT_ASC;
                                StartShow();
                                return true;
                            case R.id.pm_by_decrease:
                                App.SORT = App.SORT_DESC;
                                StartShow();
                                return true;
                            case R.id.pm_by_random:
                                App.SORT = App.SORT_RANDOM;
                                StartShow();
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                popup1.show();
                break;
        }
    }
    private void StartShow(){
        if(App.SORT.equals(App.SORT_ASC)){
            imageSort.setImageResource(R.drawable.ic_keyboard_up);
        }else if(App.SORT.equals(App.SORT_DESC)){
            imageSort.setImageResource(R.drawable.ic_keyboard_down);
        }else if(App.SORT.equals(App.SORT_RANDOM)){
            imageSort.setImageResource(R.drawable.ic_swap_vert);
        }else{
            imageSort.setImageResource(R.drawable.ic_sort);
        }
        PlayListView.showPlay_List(MainActivity.this,"",listVideo,count_items,mLinearLayoutManager);
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.b_import:
                SelectFile(R.id.b_import);
                break;
            case R.id.b_add_play_list:
                SelectFile(R.id.b_add_play_list);
                break;
            case R.id.b_change_theme:
                try {
                    int id_theme = App.sp.getInt("id_theme",R.style.AppThemeDark);
                    if(id_theme == R.style.AppThemeDark){
                        id_theme = R.style.AppThemeWhite;
                    }else{
                        id_theme = R.style.AppThemeDark;
                    }
                    App.SaveTheme(id_theme);
                    recreate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                Menu menu = new Menu();
                menu.start(item.getItemId());
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
        return false;
    }

    @SuppressLint("Range")
    private void Show_Play_Lists(ArrayList<PlayListItem> arr_play_list, RecyclerView list_play_list, AlertDialog newDialog){
        try {
            arr_play_list.add(new PlayListItem(getString(R.string.mains),0));
            cursor = App.sql.rawQuery("SELECT * FROM " + App.dataBase.TABLE_MORE_PLAY_LIST, null);
            cursor.moveToFirst();
            do {
                arr_play_list.add(new PlayListItem(cursor.getString(cursor.getColumnIndex(App.dataBase.TITLE)),
                        cursor.getInt(cursor.getColumnIndex(App.dataBase.ID))));
            } while (cursor.moveToNext());
        }catch (Exception e){
            e.printStackTrace();
        }
        list_play_list.setAdapter(new PlayListsAdapter(MainActivity.this,arr_play_list,listVideo,count_items,newDialog,App.SORT,mLinearLayoutManager));
    }

    @Override
    public void onStart() {
        if(!App.Activity_Context.equals(MainActivity.this)){
            App.Activity_Context = MainActivity.this;
            if(App.searchVideoAdapter.isVisibleDownloads){
                PlayListView.showPlay_List(MainActivity.this,"",listVideo,count_items,mLinearLayoutManager);
                App.searchVideoAdapter.isVisibleDownloads = true;
                App.searchVideoAdapter.VisibleDownloads();
                count_items.setText(getString(R.string.count_items)+": "+App.videos.size());
            }else{
                PlayListView.showPlay_List(MainActivity.this,"",listVideo,count_items,mLinearLayoutManager);
            }
        }
        Player.window = getWindow();
        super.onStart();
    }
    @Override
    public void onBackPressed() {
        if(App.main_player.isFullScreen()){
            App.main_player.exitFullScreen();
        }else if(drawer.isDrawerOpen(GravityCompat.START)){
            drawer.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    public void SelectFile(int id){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        int reguestCode = 0;
        if(id == R.id.b_import){
            reguestCode = 10;
        }else if(id == R.id.b_add_play_list){
            reguestCode = 11;
        }
        startActivityForResult(intent, reguestCode);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                for (int item : grantResults){
                    if (item != PackageManager.PERMISSION_GRANTED) {
                        Permissions.AlertFromPer(MainActivity.this);
                        break;
                    }
                }
                break;
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            switch (requestCode){
                case 10:
                    Uri uri = data.getData();
                    LaterFunction(uri, R.id.b_import);
                    break;
                case 11:
                    Uri uri1 = data.getData();
                    LaterFunction(uri1, R.id.b_add_play_list);
                    break;
                case 2:
                    if(resultCode==RESULT_CANCELED){
                        Permissions.CheckPermissionInstall(MainActivity.this);
                    }
                    break;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void LaterFunction(Uri uri, int id) {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(uri)));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            if(id == R.id.b_import){
                Menu.Overrayding(sb.toString());
            }else if(id == R.id.b_add_play_list){
                Menu.AddPlayList(sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
