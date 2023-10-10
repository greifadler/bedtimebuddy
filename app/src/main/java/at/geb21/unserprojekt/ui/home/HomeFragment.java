package at.geb21.unserprojekt.ui.home;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import java.io.IOException;

import at.geb21.unserprojekt.R;
import at.geb21.unserprojekt.databinding.FragmentHomeBinding;
import at.geb21.unserprojekt.ui.alarm.YourAlarmReceiver;
import at.geb21.unserprojekt.sensors.SoundRecorder;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private HomeViewModel homeViewModel;

    /**
     * Called when creating the view for the HomeFragment.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState The saved state of the fragment.
     * @return The View for the HomeFragment's UI.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        if (homeViewModel.isAlarmAlreadySet()) {
            startActivity(new Intent(getContext(), HomeWhileSleepingActivity.class));
        }

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        homeViewModel.getAlarmTime().observe(getViewLifecycleOwner(), binding.tvAlarmTime::setText);

        homeViewModel.calculateOptimalAlarmtime();

        binding.buttonChangeAlarm.setOnClickListener(it -> {
            showTimeSpinner();
        });

        binding.buttonSleepsunrise.setOnClickListener(it -> {
            if (!homeViewModel.isAlarmAlreadySet()) {
                new Thread(() -> setAlarm(false, getContext(), 8*60)).start();
            } else {
                Toast.makeText(getContext(), R.string.alarme_already_set, Toast.LENGTH_SHORT).show();
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                System.err.println(e);
            }
            if (homeViewModel.isAlarmAlreadySet()) {
                startActivity(new Intent(getContext(), HomeWhileSleepingActivity.class));
                Toast.makeText(getContext(), R.string.alarm_set, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.unable_to_set_alarm, Toast.LENGTH_SHORT).show();
            }
        });

        binding.buttonSleep.setOnClickListener(it -> {
            if (!homeViewModel.isAlarmAlreadySet()) {
                new Thread(() -> {
                    setAlarm(getContext(), homeViewModel.getMinutesUntilAlarm());
                }).start();
            } else {
                Toast.makeText(getContext(), R.string.alarme_already_set, Toast.LENGTH_SHORT).show();
            }

            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            if (homeViewModel.isAlarmAlreadySet()) {
                startActivity(new Intent(getContext(), HomeWhileSleepingActivity.class));
                Toast.makeText(getContext(), R.string.alarm_set, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getContext(), R.string.unable_to_set_alarm, Toast.LENGTH_SHORT).show();
            }
        });

        return root;
    }

    /**
     * Displays the time spinner dialog to choose the alarm time.
     */
    private void showTimeSpinner() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (view, hourOfDay, minute) -> homeViewModel.setAlarmTime(hourOfDay, minute), homeViewModel.getHourOfDay(), homeViewModel.getMinute(), false);
        timePickerDialog.show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        homeViewModel.calculateOptimalAlarmtime();
    }

    /**
     * Sets the alarm with or without an end time.
     *
     * @param withEndTime Specifies if the alarm has an end time.
     * @return The ID of the saved alarm.
     */
    public long setAlarm(boolean withEndTime, Context context, int minutesFromNow) {
        long id =  homeViewModel.saveAlarmToDB(withEndTime);
        startNoiseRecording(context, minutesFromNow, id);
        return id;
    }

    /**
     * Sets the alarm using the provided context and minutes from now.
     *
     * @param context        The context in which the alarm is set.
     * @param minutesFromNow The number of minutes from the current time to set the alarm.
     */
    public void setAlarm(Context context, int minutesFromNow) {
        Intent intent = new Intent(context, YourAlarmReceiver.class);
        long id = setAlarm(true, context, minutesFromNow);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        long currentTimeInMillis = System.currentTimeMillis();
        long alarmTimeInMillis = currentTimeInMillis + (minutesFromNow * 60 * 1000);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, alarmTimeInMillis, pendingIntent);
    }

    /**
     * Starts recording noise using the SoundRecorder class.
     *
     * @param context         The context of the application.
     * @param minutesFromNow  The number of minutes from the current time to start recording.
     * @param id              The ID associated with the recording.
     * @throws RuntimeException If an Error occurs during the recording process.
     */
    private void startNoiseRecording(Context context, int minutesFromNow, long id) {
        try {
            SoundRecorder recorder = new SoundRecorder(context, homeViewModel.getSleepDao(), (int) id);
            recorder.startRecording(minutesFromNow);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
