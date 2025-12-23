package net.stuple.worsecounter;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import androidx.core.app.NotificationCompat;

public class CounterService extends Service {
    private int myPickedNumber;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private MediaPlayer mediaPlayer;
    private static final String CHANNEL_ID = "CounterChannel";
    public static final String ACTION_TICK = "net.stuple.worsecounter.TICK";

    private final int[] soundList = {
            R.raw.alarm,
            R.raw.fahhh,
    };

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        createNotificationChannel();
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Worse Counter")
                .setContentText("Timer is running...")
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .build();

        startForeground(1, notification);
        resetAndStart();
        return START_STICKY;
    }

    private void resetAndStart() {
        myPickedNumber = (int)(Math.random() * 420);
        handler.post(counterRunnable);
    }

    private final Runnable counterRunnable = new Runnable() {
        @Override
        public void run() {
            Intent tickIntent = new Intent(ACTION_TICK);
            tickIntent.putExtra("remaining", myPickedNumber);
            sendBroadcast(tickIntent);

            if (myPickedNumber <= 0) {
                int randomIndex = (int) (Math.random() * soundList.length);
                int selectedSound = soundList[randomIndex];
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }
                mediaPlayer = MediaPlayer.create(CounterService.this, selectedSound);
                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0.5f, 0.5f);
                    mediaPlayer.start();
                }
                handler.postDelayed(() -> resetAndStart(), 3000);
            } else {
                myPickedNumber--;
                handler.postDelayed(this, 1000);
            }
        }
    };

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID, "Counter", NotificationManager.IMPORTANCE_LOW);
            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) { return null; }

    @Override
    public void onDestroy() {
        handler.removeCallbacksAndMessages(null);
        if (mediaPlayer != null) {
            mediaPlayer.release();
        }
        super.onDestroy();
    }
}