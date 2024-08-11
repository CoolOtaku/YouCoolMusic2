package com.example.youcoolmusic2.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;
import com.example.youcoolmusic2.Adapters.SearchVideoAdapter;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Interfaces.Player;
import com.example.youcoolmusic2.Obg.NetWork;
import com.example.youcoolmusic2.R;
import com.example.youcoolmusic2.Obg.Video;
import com.example.youcoolmusic2.Tasks.DownloadJSONTask;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Comparator;

public class SearchActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText nameVideo;
    private ImageView searchButton;
    public static RecyclerView listVideo;
    private DownloadJSONTask task = new DownloadJSONTask(SearchActivity.this);
    public static TextView count_items;
    private TextView textSort;
    private ImageView imageSort;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(App.sp.getInt("id_theme",R.style.AppThemeDark));
        setContentView(R.layout.activity_search);

        nameVideo = (EditText) findViewById(R.id.nameVideo);
        searchButton = (ImageView) findViewById(R.id.searchButton);
        count_items = (TextView) findViewById(R.id.count_items);
        textSort = (TextView) findViewById(R.id.textSort);
        imageSort = (ImageView) findViewById(R.id.imageSort);
        listVideo = (RecyclerView) findViewById(R.id.listVideo);
        listVideo.setLayoutManager(new LinearLayoutManager(this));

        searchButton.setOnClickListener(this);
        textSort.setOnClickListener(this);
        imageSort.setOnClickListener(this);

        task.execute("https://www.googleapis.com/youtube/v3/search?maxResults=50&part=snippet,id&type=video&q=Музыка&key="+App.API_KEY_2);

        /*AccountManager am = AccountManager.get(this);
        Account[] accounts = am.getAccounts();
        task.execute("https://www.googleapis.com/youtube/v3/activitiespart=snippet%2CcontentDetails&channelId="+accounts[0].name+"&maxResults=25&regionCode=ua&key="+API_KEY_2);
    */
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.searchButton:
                String name = nameVideo.getText().toString();
                if(name.isEmpty()){
                    Toast.makeText(SearchActivity.this, getString(R.string.please_paint_text),Toast.LENGTH_LONG).show();
                    break;
                }
                try {
                    if(NetWork.hasConnection(SearchActivity.this)) {
                        task = new DownloadJSONTask(SearchActivity.this);
                        task.execute("https://www.googleapis.com/youtube/v3/search?maxResults=50&part=snippet,id&type=video&q=" + URLEncoder.encode(name, String.valueOf(StandardCharsets.UTF_8))
                                + "&key="+App.API_KEY_1);
                    }else{
                        App.toast.showToas(SearchActivity.this,getString(R.string.error_internet),false);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.textSort:
            case R.id.imageSort:
                PopupMenu popup = new PopupMenu(SearchActivity.this,v);
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.pm_by_usual:
                                Collections.reverse(App.videos);
                                imageSort.setImageResource(R.drawable.ic_sort);
                                App.searchVideoAdapter = new SearchVideoAdapter(SearchActivity.this);
                                listVideo.setAdapter(App.searchVideoAdapter);
                                return true;
                            case R.id.pm_by_growing_up:
                                Collections.sort(App.videos,new Comparator<Video>() {
                                    @Override
                                    public int compare(Video lhs, Video rhs) {
                                        return lhs.title.compareTo(rhs.title);
                                    }
                                });
                                imageSort.setImageResource(R.drawable.ic_keyboard_up);
                                App.searchVideoAdapter = new SearchVideoAdapter(SearchActivity.this);
                                listVideo.setAdapter(App.searchVideoAdapter);
                                return true;
                            case R.id.pm_by_decrease:
                                Collections.sort(App.videos,new Comparator<Video>() {
                                    @Override
                                    public int compare(Video lhs, Video rhs) {
                                        return lhs.title.compareTo(rhs.title);
                                    }
                                });
                                Collections.reverse(App.videos);
                                imageSort.setImageResource(R.drawable.ic_keyboard_down);
                                App.searchVideoAdapter = new SearchVideoAdapter(SearchActivity.this);
                                listVideo.setAdapter(App.searchVideoAdapter);
                                return true;
                            case R.id.pm_by_random:
                                Collections.shuffle(App.videos);
                                imageSort.setImageResource(R.drawable.ic_swap_vert);
                                App.searchVideoAdapter = new SearchVideoAdapter(SearchActivity.this);
                                listVideo.setAdapter(App.searchVideoAdapter);
                                return true;
                            default:
                                return false;
                        }
                    }
                });
                MenuInflater inflater = popup.getMenuInflater();
                inflater.inflate(R.menu.sort_menu,popup.getMenu());
                popup.show();
                break;
        }
    }
    @Override
    public void onStart() {
        App.Activity_Context = SearchActivity.this;
        Player.window = getWindow();
        super.onStart();
    }

    @Override
    public void onBackPressed() {
        if (App.main_player.isFullScreen()) {
            App.main_player.exitFullScreen();
        } else {
            super.onBackPressed();
        }
    }
}
