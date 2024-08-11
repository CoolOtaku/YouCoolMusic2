package com.example.youcoolmusic2.Obg;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.example.youcoolmusic2.R;

public class Permissions {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void Start(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED||
                ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO},1);
        }
        CheckPermissionInstall(context);
        CheckPermissionOverlay(context);

    }

    public static void CheckPermissionInstall(Context context){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.getPackageManager().canRequestPackageInstalls()) {
                ((Activity) context).startActivityForResult(new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).setData(Uri.parse(String.format("package:%s", context.getPackageName()))), 2);
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void CheckPermissionOverlay(Context context){
        if (!Settings.canDrawOverlays(context)) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(context);
            dialog.setTitle(context.getString(R.string.permission));
            dialog.setMessage(context.getString(R.string.permission_required));
            dialog.setPositiveButton(context.getString(R.string.yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    ((Activity) context).startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION).setData(Uri.parse(String.format("package:%s", context.getPackageName()))), 3);
                }
            });
            dialog.setNegativeButton(context.getString(R.string.no), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });
            AlertDialog newDialog = dialog.create();
            newDialog.show();
        }
    }
    public static void AlertFromPer(Context context){
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle(context.getString(R.string.permission));
        dialog.setMessage(context.getString(R.string.permission_denies));
        dialog.setPositiveButton(context.getString(R.string.okay), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent appSettingsIntent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:" + context.getPackageName()));
                ((Activity) context).startActivityForResult(appSettingsIntent, 0);
            }
        });
        AlertDialog newDialog = dialog.create();
        newDialog.show();
    }
}
