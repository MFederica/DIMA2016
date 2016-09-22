package com.appetite.cooking;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import com.appetite.R;

public class BroadcastService extends Service {

    private final static String TAG = "BroadcastService";

    public static final String COUNTDOWN_BR = "com.appetite.countdown_br";
    public final static String MILLIS = "com.appetite.cooking.BroadcastService.MILLIS";
    Intent bi = new Intent(COUNTDOWN_BR);

    CountDownTimer cdt = null;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: ");
    }

    @Override
    public void onDestroy() {

        cdt.cancel();
        Log.i(TAG, "Timer cancelled");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        Log.i(TAG, "Starting timer...");

        cdt = new CountDownTimer(intent.getIntExtra(MILLIS, 60000), 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

                Log.i(TAG, "Countdown seconds remaining: " + millisUntilFinished / 1000);
                bi.putExtra("countdown", millisUntilFinished);
                sendBroadcast(bi);
            }

            @Override
            public void onFinish() {
                Log.i(TAG, "Timer finished");
                bi.putExtra("countdown", 0);
                //TODO suoneria corta (usa volume media xò)

                MediaPlayer mMediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.alarm);
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.start();
                sendBroadcast(bi);

                /*
                //TODO cancellare (x usare default ringtone)
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    r = RingtoneManager.getRingtone(getApplicationContext(), notification);
                    r.play();
                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setCancelable(false);
                    builder.setMessage(R.string.activity_cooking_timer_dialog_running_message)
                            .setTitle(R.string.activity_cooking_timer_dialog_title);
                    builder.setPositiveButton(R.string.activity_cooking_timer_dialog_running_ok, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            // User clicked OK button
                            r.stop();
                        }
                    });
                    builder.show();
                } */
            }
        };
        cdt.start();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
}