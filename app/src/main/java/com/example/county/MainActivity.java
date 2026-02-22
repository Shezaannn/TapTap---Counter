package com.example.county;

import static androidx.core.content.ContextCompat.startActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class MainActivity extends AppCompatActivity {

    TextView txtcounter;
    ImageButton btnminus, btnlock, btnreset, btnsettings;
    ConstraintLayout main;

    int count = 0;

    SoundPool soundPool;
    int popSound;
    Vibrator vibrator;

    SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        main = findViewById(R.id.main);
        txtcounter = findViewById(R.id.txtcounter);
        btnminus = findViewById(R.id.btnminus);
        btnlock = findViewById(R.id.btnlock);
        btnreset = findViewById(R.id.btnreset);
        btnsettings = findViewById(R.id.btnsettings);

        prefs = getSharedPreferences("county", MODE_PRIVATE);

        count = prefs.getInt("count", 0);
        txtcounter.setText(String.valueOf(count));

        soundPool = new SoundPool.Builder().setMaxStreams(2).build();
        popSound = soundPool.load(this, R.raw.pop, 1);

        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        btnsettings.setOnClickListener(v -> showDialogBox());

        main.setOnClickListener(v -> {

            if (prefs.getBoolean("sound", true)) {
                soundPool.play(popSound, 1f, 1f, 1, 0, 1.5f);
            }

            if (prefs.getBoolean("vibration", true)) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator.vibrate(
                            VibrationEffect.createOneShot(
                                    40,
                                    VibrationEffect.DEFAULT_AMPLITUDE
                            )
                    );
                } else {
                    vibrator.vibrate(40);
                }
            }

            count++;
            prefs.edit().putInt("count", count).apply();
            txtcounter.setText(String.valueOf(count));
        });

        btnlock.setOnClickListener(v -> {
            if (main.isClickable()) {
                main.setClickable(false);
                btnlock.setImageResource(R.drawable.unlocked);
            } else {
                main.setClickable(true);
                btnlock.setImageResource(R.drawable.lock);
            }
            btnlock.setScaleType(ImageView.ScaleType.FIT_CENTER);
        });


        btnreset.setOnClickListener(v -> {
            count = 0;
            prefs.edit().putInt("count", count).apply();
            txtcounter.setText(String.valueOf(count));
        });

        btnminus.setOnClickListener(v -> {
            if (count > 0) {
                count--;
                prefs.edit().putInt("count", count).apply();
                txtcounter.setText(String.valueOf(count));
            } else {
                Toast.makeText(this, "Count is already 0", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDialogBox() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Settings");

        android.view.View view = getLayoutInflater()
                .inflate(R.layout.dialog_settings, null);

        Switch switchSound = view.findViewById(R.id.switchsound);
        Switch switchVibration = view.findViewById(R.id.switchvibration);
        ImageButton btnFeedback = view.findViewById(R.id.btnfeedback);

        switchSound.setChecked(prefs.getBoolean("sound", true));
        switchVibration.setChecked(prefs.getBoolean("vibration", true));

        switchSound.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("sound", checked).apply()
        );

        switchVibration.setOnCheckedChangeListener((b, checked) ->
                prefs.edit().putBoolean("vibration", checked).apply()
        );

        btnFeedback.setOnClickListener(v -> {
            String subject = "TapTap Counter App Feedback";

            Uri uri = Uri.parse(
                    "mailto:khanshezaan21@gmail.com" +
                            "?subject=" + Uri.encode(subject)
            );

            Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
            startActivity(intent);
        });

        builder.setView(view);
        builder.setPositiveButton("Close", null);
        builder.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        soundPool.release();
    }
}
