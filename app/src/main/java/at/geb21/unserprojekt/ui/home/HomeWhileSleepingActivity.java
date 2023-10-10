package at.geb21.unserprojekt.ui.home;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import at.geb21.unserprojekt.R;
import at.geb21.unserprojekt.ui.alarm.AlarmActivity;
import dagger.hilt.android.AndroidEntryPoint;

/**
 * Activity class for the home screen while sleeping.
 */
@AndroidEntryPoint
public class HomeWhileSleepingActivity extends AppCompatActivity {

    private HomeWhileSleepingViewModel mViewModel;
    private Button saveNotiz;
    private EditText notiz;

    private TextView tvNotes;
    private TextView tvWakeUpTime;
    private TextView tvNoiseCount;
    private TextView tvWakeUpTimeLable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_while_sleeping);
        mViewModel = new ViewModelProvider(this).get(HomeWhileSleepingViewModel.class);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                + WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                +WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        saveNotiz = findViewById(R.id.btnSaveNotiz);
        notiz = findViewById(R.id.notiz);
        tvNotes = findViewById(R.id.textViewNotes);
        tvWakeUpTime = findViewById(R.id.textViewWakeUpTime);
        tvNoiseCount = findViewById(R.id.textViewNoiseCount);
        tvWakeUpTimeLable = findViewById(R.id.tfNotesWhileSleepingLabel);

        System.out.println(mViewModel.getCurrentSleep());

        mViewModel.getmNotes().observe(this, tvNotes::setText);
        mViewModel.getmWakeUpTime().observe(this, tvWakeUpTime::setText);
        mViewModel.getmNoiseCount().observe(this, tvNoiseCount::setText);

        saveNotiz.setOnClickListener(it -> {
            mViewModel.saveNote(notiz.getText().toString());
            try {
                Thread.sleep(200);
                notiz.setText("");
                new Thread(() -> {
                    mViewModel.updateAlarmValues();
                }).start();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        });

        checkIfScreenIsDone();

        runOnUiThread(new Thread(() -> {
            try {
                Thread.sleep(200);

                if (mViewModel.getCurrentSleep() != null && mViewModel.getCurrentSleep().getEnd() == null) {
                    startLightSensorClock();
                    tvWakeUpTimeLable.setText(R.string.senor_label);
                }
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }));
    }

    /**
     * Starts the light sensor clock for monitoring light intensity.
     */
    private void startLightSensorClock() {
        Toast.makeText(getApplicationContext(), mViewModel.getCurrentSleep().toString(), Toast.LENGTH_SHORT).show();

        SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        Sensor lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);

        final boolean[] hasStarted = {false};

        SensorEventListener lightSensorListener = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                float lightIntensity = event.values[0];
                mViewModel.getmWakeUpTime().setValue((int) lightIntensity + "");

                if (lightIntensity > mViewModel.getLightThreshold()) {
                    if (!hasStarted[0])
                    {
                        hasStarted[0] = true;
                        startAlarm();
                    }
                }
            }


            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {
                System.out.println("Accuracy: " + accuracy);
            }
        };

        sensorManager.registerListener(lightSensorListener, lightSensor, SensorManager.SENSOR_DELAY_NORMAL);


    }

    /**
     * Checks if the screen is done and finishes the activity if the current sleep is not set.
     */
    private void checkIfScreenIsDone() {
        try {
            Thread.sleep(200);
            if (!mViewModel.isCurrentSleepSet()) {
                finish();
            }
        } catch (InterruptedException e) {
            System.err.println(e);
        }
    }

    /**
     * Starts the alarm and displays a toast message.
     */
    private void startAlarm() {
        Context context = getApplicationContext();
        Toast.makeText(context, R.string.please_wake_up, Toast.LENGTH_SHORT).show();

        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkIfScreenIsDone();
    }

    @Override
    public void onBackPressed() {
        Toast.makeText(getApplicationContext(), R.string.home_while_sleeping_on_back_pressed_text, Toast.LENGTH_SHORT).show();
    }
}