package com.example.youcoolmusic2.Tasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;

import androidx.core.content.FileProvider;

import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.BuildConfig;
import com.example.youcoolmusic2.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class DownloadUpdate extends AsyncTask<String, String, String> {

    @SuppressLint("StaticFieldLeak")
    private ProgressDialog pDialog;
    private String storagePath = App.Activity_Context.getExternalCacheDir().getAbsolutePath();

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(App.Activity_Context);
        pDialog.setMessage(App.Activity_Context.getString(R.string.downloads_update));
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        InputStream is = null;
        URL u = null;
        int len1 = 0;
        int temp_progress = 0;
        int progress = 0;
        try {

            u = new URL(App.UPDATE_URL+"app.txt");
            is = u.openStream();
            URLConnection huc = (URLConnection) u.openConnection();
            huc.connect();
            int size = huc.getContentLength();

            if (huc != null) {
                File f = new File(storagePath);

                FileOutputStream fos = new FileOutputStream(f+"/ycm2.apk");
                byte[] buffer = new byte[1024];
                int total = 0;
                if (is != null) {
                    while ((len1 = is.read(buffer)) != -1) {
                        total += len1;
                        progress = (int) ((total * 100) / size);
                        if(progress >= 0) {
                            temp_progress = progress;
                            publishProgress("" + progress);
                        }else
                            publishProgress("" + temp_progress+1);

                        fos.write(buffer, 0, len1);
                    }
                }

                if (fos != null) {
                    publishProgress("" + 100);
                    fos.close();
                    InstallUpdate();
                }
            }else{
                App.toast.showToas(App.Activity_Context,App.Activity_Context.getString(R.string.error_internet),false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
        pDialog.setProgress(Integer.parseInt(values[0]));
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if (pDialog.isShowing()) {
            pDialog.dismiss();
        }
    }

    private void InstallUpdate(){
        File file = new File(storagePath+"/ycm2.apk");
        if(file.exists()){
            Intent installIntent;
            Uri uriFile;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                uriFile = FileProvider.getUriForFile(App.Activity_Context, BuildConfig.APPLICATION_ID+".provider", file);
                installIntent.setData(uriFile);
                installIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                installIntent = new Intent(Intent.ACTION_VIEW);
                uriFile = Uri.fromFile(file);
                installIntent.setDataAndType(uriFile,"application/vnd.android.package-archive");
                installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            installIntent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
            installIntent.putExtra(Intent.EXTRA_RETURN_RESULT, true);
            installIntent.putExtra(Intent.EXTRA_INSTALLER_PACKAGE_NAME, App.Activity_Context.getApplicationInfo().packageName);
            App.Activity_Context.startActivity(Intent.createChooser(installIntent, "Open_Apk"));
        }else {
            App.toast.showToas(App.Activity_Context,App.Activity_Context.getString(R.string.error_install),false);
        }
    }


}
