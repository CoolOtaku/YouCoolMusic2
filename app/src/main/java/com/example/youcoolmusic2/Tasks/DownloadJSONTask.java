package com.example.youcoolmusic2.Tasks;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;

import com.example.youcoolmusic2.Activity.SearchActivity;
import com.example.youcoolmusic2.Adapters.SearchVideoAdapter;
import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.Obg.Video;
import com.example.youcoolmusic2.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadJSONTask extends AsyncTask<String, Void, String> {

    @SuppressLint("StaticFieldLeak")
    private Context context;

    public DownloadJSONTask(Context context){
        this.context = context;
    }

    @Override
    protected void onPreExecute() {
        App.loading.Start((Activity) context);
    }

    @Override
    protected String doInBackground(String... strings) {
        URL url = null;
        HttpURLConnection urlConection = null;
        StringBuilder result = new StringBuilder();
        try {
            url = new URL(strings[0]);
            urlConection = (HttpURLConnection) url.openConnection();
            InputStream inputSteram = urlConection.getInputStream();
            InputStreamReader inputSteramReader = new InputStreamReader(inputSteram);
            BufferedReader reader = new BufferedReader(inputSteramReader);
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                line = reader.readLine();
            }
            return result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConection != null) {
                urlConection.disconnect();
            }
        }
        return null;
    }
    @Override
    protected void onPostExecute(String result) {
        if(!result.isEmpty()) {
            try {
                App.loading.Close();
                App.videos = new ArrayList<>();
                JSONObject object = new JSONObject(result);
                JSONArray items = object.getJSONArray("items");
                for(int i = 0; i < items.length(); i++) {
                    JSONObject item = items.getJSONObject(i);
                    JSONObject snippet = item.getJSONObject("snippet");
                    JSONObject id = item.getJSONObject("id");
                    JSONObject img = snippet.getJSONObject("thumbnails");
                    img = img.getJSONObject("default");
                    JSONArray pljsa = new JSONArray();
                    pljsa.put(0);
                    App.videos.add(new Video(id.getString("videoId"), Jsoup.parse(snippet.getString("title")).text(),
                            img.getString("url"),pljsa.toString()));
                }
                App.searchVideoAdapter = new SearchVideoAdapter(context);
                SearchActivity.listVideo.setAdapter(App.searchVideoAdapter);
                SearchActivity.count_items.setText(context.getString(R.string.count_items)+": "+App.videos.size());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

}
