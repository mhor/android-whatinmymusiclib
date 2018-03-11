package com.mhor.whatinmymusiclib;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.Snackbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mhor.whatinmymusiclib.service.TaskQueueService;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    public static final String PREFS_NAME = "WhatInMyMusicLibPrefs" ;
    public static final String SOURCE_NAME = "sourceName";
    public static final String DEFAULT_SOURCE_NAME = "DefaultPhone";


    public static final String TAG = "MainActivity";

    /**
     * Id to identify a external storage permission request.
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    /**
     * Root of the layout of this Activity.
     */
    private View mLayout;

    private static String[] PERMISSIONS_EXTERNAL_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private String sourceName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mLayout = findViewById(R.id.main_layout);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String sourceName = settings.getString(SOURCE_NAME, DEFAULT_SOURCE_NAME);
        setSourceName(sourceName);

        final EditText sourceNameEditText = (EditText) findViewById(R.id.source_name);

        Button configButton = (Button) findViewById(R.id.save_config);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSourceName(sourceNameEditText.getText().toString());

                SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString(SOURCE_NAME, getSourceName());

                editor.commit();

                Context context = getApplicationContext();
                Toast.makeText(context, context.getString(R.string.save_config_success), Toast.LENGTH_LONG).show();

            }
        });

        Button exportButton = (Button) findViewById(R.id.export_music_lib_btn);
        exportButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();

                context.startService(TaskQueueService.getMusicLibExportIntent(context));
/*
                Cursor tracks = getContentResolver().query(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        new String[]{
                                MediaStore.Audio.AudioColumns.TITLE,
                                MediaStore.Audio.AudioColumns.TRACK,
                                MediaStore.Audio.AudioColumns.ALBUM,
                                MediaStore.Audio.AudioColumns.ARTIST,
                                MediaStore.Audio.AudioColumns.DATE_ADDED
                        },
                        MediaStore.Audio.AudioColumns.IS_MUSIC + " = ?",
                        new String[]{"1"},
                        null
                );

                Export export = new Export();
                export.setSourceName(getSourceName());
                while (tracks.moveToNext()) {
                    Track track = new Track();
                    track.setName(tracks.getString(tracks.getColumnIndex(MediaStore.Audio.AudioColumns.TITLE)));
                    track.setNumber(tracks.getInt(tracks.getColumnIndex(MediaStore.Audio.AudioColumns.TRACK)));
                    track.setAlbumName(tracks.getString(tracks.getColumnIndex(MediaStore.Audio.AudioColumns.ALBUM)));
                    track.setArtistName(tracks.getString(tracks.getColumnIndex(MediaStore.Audio.AudioColumns.ARTIST)));
                    track.setAddedAt(new Date(tracks.getLong(tracks.getColumnIndex(MediaStore.Audio.AudioColumns.DATE_ADDED)) * 1000L));
                    export.getTracks().add(track);
                }
                tracks.close();

                Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss").create();
                String json = gson.toJson(export);

                if (isExternalStorageReadable() && isExternalStorageWritable()) {
                    Long tsLong = System.currentTimeMillis() / 1000;
                    String ts = tsLong.toString();
                    File exportFile = getAppStorageDir("export-" + ts + ".json");
                    try {
                        OutputStream os = new FileOutputStream(exportFile);
                        os.write(json.getBytes());
                        os.flush();
                        os.close();

                        Context context = getApplicationContext();
                        Toast.makeText(context, context.getString(R.string.export_success), Toast.LENGTH_LONG).show();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                */
            }

        });

        Log.i(TAG, "Checking permissions.");

        // Verify that all required contact permissions have been granted.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED
                ) {
            // Contacts permissions have not been granted.
            Log.i(TAG, "External storage permissions has NOT been granted. Requesting permissions.");
            requestExternalStoragePermissions();

        } else {

            // External storage permissions have been granted. Show the contacts fragment.
            Log.i(
                    TAG,
                    "External storage permissions have already been granted."
            );
        }
    }

    private void requestExternalStoragePermissions() {
        // BEGIN_INCLUDE(contacts_permission_request)
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                || ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // For example, if the request has been denied previously.
            Log.i(TAG,
                    "Displaying external storage permission rationale to provide additional context.");

            // Display a SnackBar with an explanation and a button to trigger the request.
            Snackbar.make(mLayout, R.string.permission_external_storage_rationale,
                    Snackbar.LENGTH_INDEFINITE)
                    .setAction(R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat
                                    .requestPermissions(MainActivity.this, PERMISSIONS_EXTERNAL_STORAGE,
                                            REQUEST_EXTERNAL_STORAGE);
                        }
                    })
                    .show();
        } else {
            // Contact permissions have not been granted yet. Request them directly.
            ActivityCompat.requestPermissions(this, PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_STORAGE);
        }
        // END_INCLUDE(contacts_permission_request)
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    public File getAppStorageDir(String exportFileName) {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/WhatInMyMusicLib");
        dir.mkdirs();
        return new File(dir, exportFileName);
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getSourceName() {
        return sourceName;
    }
}
