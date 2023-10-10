package at.geb21.unserprojekt.ui.home;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import javax.inject.Inject;

import at.geb21.unserprojekt.beans.Sleep;
import at.geb21.unserprojekt.dao.SleepDao;
import at.geb21.unserprojekt.helpers.Helpers;
import at.geb21.unserprojekt.sharedprefs.UserPreferences;
import dagger.hilt.android.lifecycle.HiltViewModel;

/**
 * ViewModel class for the home screen while sleeping.
 */
@HiltViewModel
public class HomeWhileSleepingViewModel extends ViewModel {

    private SleepDao sleepDao;
    private UserPreferences userPreferences;

    private final MutableLiveData<String> mNotes;
    private final MutableLiveData<String> mWakeUpTime;
    private final MutableLiveData<String> mNoiseCount;

    private Sleep currentSleep;

    /**
     * Constructs a new HomeWhileSleepingViewModel.
     *
     * @param sleepDao         The DAO for accessing sleep data.
     * @param userPreferences  The user preferences for the app.
     */
    @Inject
    public HomeWhileSleepingViewModel(SleepDao sleepDao, UserPreferences userPreferences) {
        this.sleepDao = sleepDao;
        this.userPreferences = userPreferences;

        mNotes = new MutableLiveData<>("");
        mWakeUpTime = new MutableLiveData<>("");
        mNoiseCount = new MutableLiveData<>("");

        new Thread(this::loadCurrentSleep).start();

        new Thread(() -> {
            try {
                Thread.sleep(200);
                while (true) {
                    updateAlarmValues();
                    Thread.sleep(1);
                }
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }).start();
    }

    /**
     * Get the MutableLiveData for the notes.
     *
     * @return The MutableLiveData object for the notes.
     */
    public MutableLiveData<String> getmNotes() {
        return mNotes;
    }

    /**
     * Get the MutableLiveData for the wake-up time.
     *
     * @return The MutableLiveData object for the wake-up time.
     */
    public MutableLiveData<String> getmWakeUpTime() {
        return mWakeUpTime;
    }

    /**
     * Update the alarm values based on the current sleep.
     */
    public void updateAlarmValues() {
        loadCurrentSleep();
        if (currentSleep != null) {
            mNotes.postValue(currentSleep.getNoteDiary());
            mNoiseCount.postValue(currentSleep.getNoiseCount() + "");
            if (currentSleep.getEnd() != null) {
                mWakeUpTime.postValue(Helpers.EXPORT_FORMATTER.format(currentSleep.getEnd()));
            }
        }
    }

    /**
     * Get the current sleep.
     *
     * @return The current Sleep object.
     */
    public Sleep getCurrentSleep() {
        return currentSleep;
    }

    /**
     * Load the current sleep from the DAO based on the user preferences.
     */
    public void loadCurrentSleep() {
        if (userPreferences.getCurrentSleepId() != -1) {
            currentSleep = sleepDao.findById((int) userPreferences.getCurrentSleepId());
            System.out.println(currentSleep.toString());
        }
    }

    /**
     * Save the current sleep to the DAO.
     */
    public void saveCurrentSleep() {
        if (userPreferences.getCurrentSleepId() != -1) {
            sleepDao.update(currentSleep);
        }
        loadCurrentSleep();
    }

    /**
     * Check if the current sleep is set.
     *
     * @return True if the current sleep is set, false otherwise.
     */
    public boolean isCurrentSleepSet() {
        return userPreferences.getCurrentSleepId() != -1;
    }

    /**
     * Save a note for the current sleep.
     *
     * @param text The note text to save.
     */
    public void saveNote(String text) {
        if (!text.equals("")) {
            new Thread(() -> {
                loadCurrentSleep();
                if (currentSleep != null) {
                    if (currentSleep.getNoteDiary().equals("")) {
                        currentSleep.setNoteDiary(currentSleep.getNoteDiary() + text);
                    } else {
                        currentSleep.setNoteDiary(currentSleep.getNoteDiary() + " - " + text);
                    }
                }
                saveCurrentSleep();
            }).start();
        }
    }

    /**
     * Get the light threshold from user preferences.
     *
     * @return The light threshold value.
     */
    public int getLightThreshold() {
        return userPreferences.getLightThreshold();
    }

    /**
     * Get the MutableLiveData for the noise count.
     *
     * @return The MutableLiveData object for the noise count.
     */
    public MutableLiveData<String> getmNoiseCount() {
        return mNoiseCount;
    }
}
