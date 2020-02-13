package com.example.dokkannotif;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    private static final String CHANNEL_ID = "0";
    private static final int notificationId = 001;
    private static String output;
    private static staminaManager instance;
    private static Thread myThread;
    private static SeekBar cSVal = null;
    private static SeekBar rTVal = null;
    private static SeekBar mSVal = null;
    private static EditText cSValTex = null;
    private static EditText rTValTex = null;
    private static EditText mSValTex = null;

    private class EditTextListener implements EditText.OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (getResources().getResourceEntryName((v.getId())).equals("currentStaText")) {
                SeekBar seekBar = findViewById(R.id.currentStaminaVal);
                seekBar.setProgress(Integer.parseInt(String.valueOf(cSValTex.getText())));
            }
            if (getResources().getResourceEntryName((v.getId())).equals("currentMaxSta")) {
                SeekBar seekBar = findViewById(R.id.maxStaminaVal);
                seekBar.setProgress(Integer.parseInt(String.valueOf(mSValTex.getText())));
            }
            if (getResources().getResourceEntryName((v.getId())).equals("currentRepSta")) {
                SeekBar seekBar = findViewById(R.id.replenishTimeVal);
                if (Integer.parseInt(String.valueOf(rTValTex.getText())) > 5) {
                    rTValTex.setText("5");
                }
                if (Integer.parseInt(String.valueOf(rTValTex.getText())) < 0) {
                    rTValTex.setText("0");
                }
                seekBar.setProgress(Integer.parseInt(String.valueOf(rTValTex.getText())));
            }
        }
    }


    private class MoveListener implements SeekBar.OnSeekBarChangeListener {

        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            if (getResources().getResourceEntryName((seekBar.getId())).equals("currentStaminaVal")) {
                EditText editText = findViewById(R.id.currentStaText);
                editText.setText(Integer.toString(seekBar.getProgress()));
                EditText editText1 = findViewById(R.id.currentMaxSta);
                SeekBar seekBar1 = findViewById(R.id.maxStaminaVal);
                if (seekBar.getProgress() > seekBar1.getProgress()) {
                    seekBar1.setProgress((seekBar.getProgress()));
                    editText1.setText(Integer.toString(seekBar.getProgress()));
                }
            }
            if (getResources().getResourceEntryName((seekBar.getId())).equals("maxStaminaVal")) {
                EditText editText = findViewById(R.id.currentMaxSta);
                editText.setText(Integer.toString(seekBar.getProgress()));
                SeekBar staminaBar = findViewById(R.id.currentStaminaVal);
                if (seekBar.getProgress() < staminaBar.getProgress()) {
                    staminaBar.setProgress(seekBar.getProgress());
                }

            }
            if (getResources().getResourceEntryName((seekBar.getId())).equals("replenishTimeVal")) {
                EditText editText = findViewById(R.id.currentRepSta);
                if (seekBar.getProgress() > 5) {
                    seekBar.setProgress(5);
                }
                if (seekBar.getProgress() < 0) {
                    seekBar.setProgress(0);
                }
                editText.setText(Integer.toString(seekBar.getProgress()));
            }
        }

        public void onStartTrackingTouch(SeekBar seekBar) {}

        public void onStopTrackingTouch(SeekBar seekBar) {}

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cSVal = findViewById(R.id.currentStaminaVal);
        rTVal = findViewById(R.id.replenishTimeVal);
        mSVal = findViewById(R.id.maxStaminaVal);
        cSVal.setOnSeekBarChangeListener(new MoveListener());
        rTVal.setOnSeekBarChangeListener(new MoveListener());
        mSVal.setOnSeekBarChangeListener(new MoveListener());
        cSValTex = findViewById((R.id.currentStaText));
        rTValTex = findViewById((R.id.currentRepSta));
        mSValTex = findViewById((R.id.currentMaxSta));
        cSValTex.setOnFocusChangeListener(new EditTextListener());
        rTValTex.setOnFocusChangeListener(new EditTextListener());
        mSValTex.setOnFocusChangeListener(new EditTextListener());
    }

    public void makeInstance(View view) {
        boolean cS = true;
        boolean rT = true;
        boolean mS = true;
        if (cS) {
            if (String.valueOf(cSVal.getProgress()) != "") {
                cS = false;
            }
        }
        if (rT) {
            if (String.valueOf(rTVal.getProgress()) != "") {
                rT = false;
            }
        }
        if (mS) {
            if (String.valueOf(mSVal.getProgress()) != "") {
                mS = false;
            }
        }
        if (cS == false && rT == false && mS == false) {
            try {
                int cSValue = cSVal.getProgress();
                int rTValue = rTVal.getProgress();
                int msValue = mSVal.getProgress();

                if (cSValue > msValue) {

                }

                instance = new staminaManager(cSValue,msValue,rTValue);

            }
            catch (Exception e) {
                instance = new staminaManager();
            }
        }
        runInstance(instance);
    }


    private void runInstance(final staminaManager instance) {

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                while (instance.getCurrentStamina() <= instance.getMaxStamina()) {
                    sendNotif();
                    instance.checkTime();
                    if (instance.getCurrentStamina() >= instance.getMaxStamina()) {
                        fullNotif();
                    }
                    try {
                        Thread.sleep(60000);
                    } catch (InterruptedException e) {

                    }
                }
            }
        };

        if (myThread == null) {
            myThread = new Thread(myRunnable);
            myThread.start();
        }
    }

    private void fullNotif() {
        createNotificationChannel(1);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Your stamina is at:")
                .setContentText("Your stamina is full!")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, builder.build());
    }

    public void sendNotif(){
        createNotificationChannel(0);
        output = instance.getCurrentStamina() + "/" + instance.getMaxStamina();

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.notification_icon)
                .setContentTitle("Your stamina is at:")
                .setContentText(output)
                .setOnlyAlertOnce(true)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        notificationManager.notify(notificationId, builder.build());
    }

    private void createNotificationChannel(int index) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_LOW;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
