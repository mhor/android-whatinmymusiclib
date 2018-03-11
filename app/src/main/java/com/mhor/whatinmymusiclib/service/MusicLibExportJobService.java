package com.mhor.whatinmymusiclib.service;

import android.app.job.JobParameters;
import android.app.job.JobService;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;

/**
 * Created by mhor on 15/08/17.
 */
@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class MusicLibExportJobService  extends JobService {

    private MusicLibExportTask mMusicLibExportTask = null;

    @Override
    public boolean onStartJob(final JobParameters params) {
        mMusicLibExportTask = new MusicLibExportTask(this) {
            @Override
            protected void onPostExecute(Boolean success) {
                super.onPostExecute(success);
                jobFinished(params, !success);
            }
        };
        mMusicLibExportTask.execute();
        return true;
    }

    @Override
    public boolean onStopJob(final JobParameters params) {
        if (mMusicLibExportTask != null) {
            mMusicLibExportTask.cancel(true);
        }
        return true;
    }

}
