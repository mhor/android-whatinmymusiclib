package com.mhor.whatinmymusiclib.service;

import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.WakefulBroadcastReceiver;

import java.util.List;

/**
 * Created by mhor on 15/08/17.
 */

public class TaskQueueService extends Service {
    private static final String TAG = "TaskQueueService";

    private static final int MUSIC_LIB_EXPORT_JOB_ID = 1;
    static final String ACTION_MUSIC_LIB_EXPORT = "com.mhor.whatinmymusiclib.service.action.MUSIC_LIB_EXPORT";
    private static final long MUSIC_LIB_EXPORT_WAKELOCK_TIMEOUT_MILLIS = 30 * 1000;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(final Intent intent, int flags, final int startId) {
        if (intent.getAction() == null) {
            stopSelf();
            return START_NOT_STICKY;
        }

        String action = intent.getAction();
        if (ACTION_MUSIC_LIB_EXPORT.equals(action)) {
            new MusicLibExportTask(this) {
                PowerManager.WakeLock lock;

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                    // This is normally not started by a WakefulBroadcastReceiver so request a
                    // new wakelock.
                    PowerManager pwm = (PowerManager) getSystemService(POWER_SERVICE);
                    lock = pwm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, TAG);
                    lock.acquire(MUSIC_LIB_EXPORT_WAKELOCK_TIMEOUT_MILLIS);
                }

                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                protected void onPostExecute(Boolean success) {
                    super.onPostExecute(success);
                    if (success) {
                        cancelMusicLibExportRetries();
                    } else {
                        scheduleRetryMusicLibExport();
                    }
                    if (lock.isHeld()) {
                        lock.release();
                    }
                    WakefulBroadcastReceiver.completeWakefulIntent(intent);
                    stopSelf(startId);
                }
            }.execute();

        }
        return START_REDELIVER_INTENT;
    }

    public static Intent getMusicLibExportIntent(Context context) {
        return new Intent(context, TaskQueueService.class)
                .setAction(ACTION_MUSIC_LIB_EXPORT);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void cancelMusicLibExportRetries() {

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.cancel(MUSIC_LIB_EXPORT_JOB_ID);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void scheduleRetryMusicLibExport() {

        JobScheduler jobScheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        jobScheduler.schedule(new JobInfo.Builder(MUSIC_LIB_EXPORT_JOB_ID,
                new ComponentName(this, MusicLibExportJobService.class))
                .setRequiredNetworkType(JobInfo.NETWORK_TYPE_ANY)
                .build());

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static Intent maybeRetryMusicLibExportDueToGainedConnectivity(Context context) {

        JobScheduler jobScheduler = (JobScheduler) context.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        List<JobInfo> pendingJobs = jobScheduler.getAllPendingJobs();
        for (JobInfo pendingJob : pendingJobs) {
            if (pendingJob.getId() == MUSIC_LIB_EXPORT_JOB_ID) {
                return TaskQueueService.getMusicLibExportIntent(context);
            }
        }
        return null;
    }
}

