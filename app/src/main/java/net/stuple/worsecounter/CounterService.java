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
    private MediaPlayer mediaPlayer; // We will create this only when needed
    private static final String CHANNEL_ID = "CounterChannel";
    public static final String ACTION_TICK = "net.stuple.worsecounter.TICK";

    // Your array of sounds
    private final int[] soundList = {
            R.raw.alarm,
            R.raw.fahhh,
    };

    @Override
    public void onCreate() {
        super.onCreate();
        // Don't initialize mediaPlayer here anymore,
        // we do it inside the loop now!
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
            // Send the current time to the Activity
            Intent tickIntent = new Intent(ACTION_TICK);
            tickIntent.putExtra("remaining", myPickedNumber);
            sendBroadcast(tickIntent);

            if (myPickedNumber <= 0) {
                // 1. Pick a random sound from the array
                int randomIndex = (int) (Math.random() * soundList.length);
                int selectedSound = soundList[randomIndex];

                // 2. Clear out the old sound if it exists
                if (mediaPlayer != null) {
                    mediaPlayer.release();
                }

                // 3. Create the player with the NEW random sound
                mediaPlayer = MediaPlayer.create(CounterService.this, selectedSound);

                if (mediaPlayer != null) {
                    mediaPlayer.setVolume(0.5f, 0.5f);
                    mediaPlayer.start();
                }

                // 4. Wait 3 seconds, then reset automatically
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