package at.geb21.unserprojekt.ui.alarm;

import androidx.lifecycle.ViewModel;

import java.time.LocalDateTime;

import javax.inject.Inject;

import at.geb21.unserprojekt.beans.Sleep;
import at.geb21.unserprojekt.dao.SleepDao;
import at.geb21.unserprojekt.sharedprefs.UserPreferences;
import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * The AlarmViewModel class is responsible for handling the logic related to the alarm functionality
 * in the alarm screen.
 */
@HiltViewModel
public class AlarmViewModel extends ViewModel {

    private SleepDao sleepDao;

    private UserPreferences userPreferences;

    private Sleep activeSleep;

    @Inject
    public AlarmViewModel(SleepDao sleepDao, UserPreferences userPreferences) {
        this.sleepDao = sleepDao;
        this.userPreferences = userPreferences;

        new Thread(() -> {
            activeSleep = sleepDao.findById((int) userPreferences.getCurrentSleepId());

        }).start();
    }

    /**
     * Stops the alarm and updates the active sleep in the database with the provided sleep quality.
     *
     * @param quality The sleep quality rating provided by the user.
     */
    public void stopAlarm(int quality) {
        if(activeSleep != null) {
            activeSleep.setEnd(LocalDateTime.now());
            activeSleep.setQuality(quality);

            new Thread(() -> {
                sleepDao.update(activeSleep);
            }).start();

            userPreferences.removeCurrentSleepId();
        } else {
            System.err.println("Unable to stop alarm");
        }
    }

}
