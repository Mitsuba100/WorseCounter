package net.stuple.worsecounter;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView textView;

    // This "Radio" listens for the Service's broadcast
    private final BroadcastReceiver timeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int timeLeft = intent.getIntExtra("remaining", 0);
            if (textView != null) {
                textView.setText("Counter: " + timeLeft);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.number_view);

        // Start the background service
        Intent intent = new Intent(this, CounterService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the listener when the app is in focus
        IntentFilter filter = new IntentFilter(CounterService.ACTION_TICK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(timeReceiver, filter, Context.RECEIVER_EXPORTED);
        } else {
            registerReceiver(timeReceiver, filter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister to save battery when the app is hidden
        unregisterReceiver(timeReceiver);
    }
}