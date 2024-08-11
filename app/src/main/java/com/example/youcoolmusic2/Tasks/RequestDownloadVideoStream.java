package com.example.youcoolmusic2.Tasks;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.example.youcoolmusic2.App;
import com.example.youcoolmusic2.R;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import javax.xml.parsers.DocumentBuilderFactory;

public class RequestDownloadVideoStream extends AsyncTask<String, String, String> {

    @SuppressLint("StaticFieldLeak")
    private Context context;
    private int pos;
    private ProgressDialog pDialog;

    public RequestDownloadVideoStream(Context context, int pos){
        this.context = context;
        this.pos = pos;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pDialog = new ProgressDialog(context);
        pDialog.setMessage(context.getString(R.string.downloading_file_please_wait));
        pDialog.setIndeterminate(false);
        pDialog.setMax(100);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
        pDialog.show();
    }

    @Override
    protected String doInBackground(String... params) {
        if(params[2] != null) {
            DownloadVideo_Audio(params[1]+"_audio.webm",params[0]);
            DownloadVideo_Audio(params[1]+"_video.webm",params[2]);
        }else{
            DownloadVideo_Audio(params[1]+"_audio.webm",params[0]);
        }
        return null;
    }

    private void DownloadVideo_Audio(String file_name, String url){
        InputStream is = null;
        URL u = null;
        int len1 = 0;
        int temp_progress = 0;
        int progress = 0;
        try {

            u = new URL(url);
            is = u.openStream();
            URLConnection huc = (URLConnection) u.openConnection();
            huc.connect();
            int size = huc.getContentLength();

            if (huc != null) {
                String storagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)+"/YouCoolMusicVideos";
                File f = new File(storagePath);
                if (!f.exists()) {
                    f.mkdir();
                }

                FileOutputStream fos = new FileOutputStream(f+"/"+file_name);
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
                    IsXML(f, file_name);
                }
            }else{
                App.toast.showToas(context,context.getString(R.string.error_internet),false);
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
    }

    private void IsXML(File f, String fileName){
        try {
            String data = readFile(f+"/"+fileName);
            if (data.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")) {
                Element node =  DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(new ByteArrayInputStream(data.getBytes()))
                    .getDocumentElement();

                NodeList nodeList = node.getElementsByTagName("BaseURL");
                Node el = nodeList.item(0);
                String url = el.getTextContent();
                DownloadVideo_Audio(fileName,url);
            }
        } catch (Exception e) {}
    }
    private String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
        String line = null;
        StringBuilder stringBuilder = new StringBuilder();
        String ls = System.getProperty("line.separator");
        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        } finally {
            reader.close();
        }
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
        App.videos.get(pos).CheckDownloadsFile();
        App.searchVideoAdapter.notifyItemChanged(pos);
    }
}