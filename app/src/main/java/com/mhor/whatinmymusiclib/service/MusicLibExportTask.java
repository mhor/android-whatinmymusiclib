package com.mhor.whatinmymusiclib.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.SystemClock;
import android.provider.BaseColumns;

/**
 * Created by mhor on 15/08/17.
 */

public class MusicLibExportTask extends AsyncTask<Void, Void, Boolean> {

    private static final String TAG = "MusicLibExportTask";
    private final Context mApplicationContext;

    public MusicLibExportTask(Context context) {
        mApplicationContext = context.getApplicationContext();
    }

    @Override
    protected Boolean doInBackground(Void... voids) {

        return true;
    }

    @Override
    protected void onPostExecute(Boolean success) {
        if (success) {
            // TODO
            return;
        }

        return;
    }
}
