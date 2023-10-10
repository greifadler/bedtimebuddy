package at.geb21.unserprojekt.ui.alarm;

import android.app.AlertDialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import at.geb21.unserprojekt.R;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * The AlarmActivity class represents the activity that is displayed when an alarm is triggered.
 * It plays the alarm sound and allows the user to stop the alarm and rate their sleep quality.
 */
@AndroidEntryPoint
public class AlarmActivity extends AppCompatActivity {
    private MediaPlayer mediaPlayer;
    private AlarmViewModel alarmViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        alarmViewModel = new ViewModelProvider(this).get(AlarmViewModel.class);

        setContentView(R.layout.activity_alarming2);

        mediaPlayer = MediaPlayer.create(this, R.raw.alarm_sound);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        Button stopButton = findViewById(R.id.btn_stop);
        stopButton.setOnClickListener(v -> stopAlarm());
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), R.string.alarm_back_pressed_text, Toast.LENGTH_SHORT).show();

    }

    /**
     * Stops the alarm and displays the rating popup.
     */
    private void stopAlarm() {
        showRatingPopup();
    }

    /**
     * Finishes the alarm and stops the alarm sound.
     *
     * @param quality The sleep quality rating provided by the user.
     */
    private void finishAlarm(int quality) {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        alarmViewModel.stopAlarm(quality);
        finish();
    }

    /**
     * Displays the rating popup dialog.
     */
    private void showRatingPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.rate_your_sleep_title);

        LinearLayout ratingLayout = new LinearLayout(this);
        ratingLayout.setOrientation(LinearLayout.HORIZONTAL);
        ratingLayout.setGravity(Gravity.CENTER);

        final ImageView[] stars = new ImageView[5];

        final int[] selectedRating = {-1};
        for (int i = 0; i < 5; i++) {
            stars[i] = new ImageView(this);
            stars[i].setImageResource(R.drawable.baseline_star_border_24);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 30, 10, 0);
            stars[i].setLayoutParams(params);
            final int rating = i + 1;
            final int finalI = i;
            stars[i].setOnClickListener(v -> {
                selectedRating[0] = rating;

                for (int j = 0; j <= finalI; j++) {
                    stars[j].setImageResource(R.drawable.baseline_star_24);
                }
                for (int j = finalI + 1; j < 5; j++) {
                    stars[j].setImageResource(R.drawable.baseline_star_border_24);
                }
            });
            ratingLayout.addView(stars[i]);
        }

        builder.setView(ratingLayout);
        builder.setPositiveButton(R.string.ok, (dialog, which) -> {
            if(selectedRating[0] != -1) {
                dialog.dismiss();
                finishAlarm(selectedRating[0]);
            } else {
                Toast.makeText(AlarmActivity.this, "You need to rate stars!", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
