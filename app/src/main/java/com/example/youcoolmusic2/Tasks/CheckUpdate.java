package com.example.youcoolmusic2.Tasks;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;

import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.BuildConfig;
import com.example.youcoolmusic2.Obg.NetWork;
import com.example.youcoolmusic2.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class CheckUpdate extends AsyncTask<String, String, String> {

    @SuppressLint("StaticFieldLeak")
    private Context context;

    public CheckUpdate(Context context){
        this.context = context;
    }

    @Override
    protected String doInBackground(String... strings) {
        if(!NetWork.hasConnection(context)){
            return "";
        }
        URL url = null;
        HttpURLConnection urlConection = null;
        StringBuilder result = new StringBuilder();
        try {
            url = new URL(App.UPDATE_URL+"check.json");
            urlConection = (HttpURLConnection) url.openConnection();
            InputStream inputSteram = urlConection.getInputStream();
            InputStreamReader inputSteramReader = new InputStreamReader(inputSteram);
            BufferedReader reader = new BufferedReader(inputSteramReader);
            String line = reader.readLine();
            while (line != null) {
                result.append(line);
                line = reader.readLine();
            }
            return  result.toString();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (urlConection != null) {
                urlConection.disconnect();
            }
        }
        return "";
    }
    @Override
    protected void onPostExecute(String result) {
        if(!result.isEmpty()) {
            try {
                JSONObject object = new JSONObject(result);
                String ver = object.getString("ver");
                String VersionApp = BuildConfig.VERSION_NAME;
                if(!ver.equals(VersionApp)){
                    AlertDialog.Builder dialog = new AlertDialog.Builder(App.Activity_Context);
                    dialog.setTitle(context.getString(R.string.b_updates));
                    dialog.setMessage(context.getString(R.string.update_released));
                    dialog.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new DownloadUpdate().execute();
                        }
                    });
                    dialog.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
                    AlertDialog newDialog = dialog.create();
                    newDialog.show();
                }else{
                    String storagePath = App.Activity_Context.getExternalCacheDir().getAbsolutePath();
                    File f = new File(storagePath+"/ycm2.apk");
                    if(f.exists()){
                        f.delete();
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}