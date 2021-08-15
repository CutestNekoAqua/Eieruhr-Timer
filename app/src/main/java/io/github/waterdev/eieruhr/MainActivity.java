package io.github.waterdev.eieruhr;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Random;

import io.github.waterdev.eieruhr.timer.Time;

public class MainActivity extends AppCompatActivity {

    private static final String channelID = "countdown";

    public Handler mHandler;
    public static Time time;
    public static final int DELAY = 1000; //1s

    public short h,m,s;

    public TextView clock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clock = findViewById(R.id.textView);
        clock.setVisibility(View.INVISIBLE);
        mHandler = new Handler(Looper.getMainLooper());
        createNotificationChannel();
        h = 0;
        m = 0;
        s = 30;
    }

    public void startTimer() {
        new Thread(() -> {
            time = new Time(h, m, s);
            boolean run = true;
            mHandler.post(() -> {
                clock.setText(time.toString());
                clock.setVisibility(View.VISIBLE);
            });
            while (run) {
                try {
                    Thread.sleep(DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                time.count();
                if (time.ended()) {
                    mHandler.post(() -> {
                        if(time != null) clock.setText(time.toString());
                        clock.setVisibility(View.INVISIBLE);
                    });
                    run = false;
                    if(time.isForceEnded()) return;
                    alertUser();
                } else {
                    mHandler.post(() -> {
                        clock.setText(time.toString());
                        clock.setVisibility(View.VISIBLE);
                    });
                }
            }
        }).start();
    }

    private void alertUser() {

        Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        if(alert == null){
            // alert is null, using backup
                alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }

        // Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelID)
                .setSmallIcon(R.drawable.ic_eieruhr_n)
                .setContentTitle(getResources().getString(R.string.notification_title))
                .setContentText(getResources().getString(R.string.notification_message))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(new Random().nextInt(), builder.build());

        Ringtone r = RingtoneManager.getRingtone(this, alert);
        r.play();
        try {
            Thread.sleep(15000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        r.stop();

    }

    public void goButton(View view) {
        EditText field = findViewById(R.id.editTextTime);
        String[] raw = field.getText().toString().split(":");
        if(raw.length == 3) useThree(raw);
        else if(raw.length == 2) useTwo(raw);
        else useOne(raw);
        startTimer();
    }

    private void useOne(String[] raw) {
        s = Short.parseShort(raw[0]);
    }

    private void useTwo(String[] raw) {
        m = Short.parseShort(raw[0]);
        s = Short.parseShort(raw[1]);
    }

    private void useThree(String[] raw) {
        h = Short.parseShort(raw[0]);
        m = Short.parseShort(raw[1]);
        s = Short.parseShort(raw[2]);
    }

    public void cancelButton(View view) {
        if(time != null) {
            time.forceEnd();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mHandler == null) {
            mHandler = new Handler(Looper.getMainLooper());
        }
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        //}
    }
}