package net.stuple.worsecounter;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private int myPickedNumber = (int)(Math.random() * 60);
    //private int myPickedNumber = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        TextView textView = findViewById(R.id.number_view); //meins
        textView.setText(String.valueOf("Counter: "+myPickedNumber)); //meins
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.fahhh);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Handler handler = new Handler(Looper.getMainLooper());
        handler.post(new Runnable() {
            @Override
            public void run() {

                if (myPickedNumber <= 0){
                    handler.removeCallbacks(this::run);
                    mediaPlayer.setVolume(0.5f, 0.5f);
                    mediaPlayer.start();

                }
                else{
                    myPickedNumber--;
                    System.out.println(myPickedNumber);
                    textView.setText(String.valueOf("Counter: "+myPickedNumber));
                    handler.postDelayed(this::run,1000);
                }
            }
        }
        );
    }
}
