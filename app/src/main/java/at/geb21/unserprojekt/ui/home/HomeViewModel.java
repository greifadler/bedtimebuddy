package at.geb21.unserprojekt.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

import javax.inject.Inject;

import at.geb21.unserprojekt.beans.Sleep;
import at.geb21.unserprojekt.dao.SleepDao;
import at.geb21.unserprojekt.sharedprefs.UserPreferences;
import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class HomeViewModel extends ViewModel {

    private final MutableLiveData<String> mAlarmTime;
    private UserPreferences userPreferences;

    private SleepDao sleepDao;

    private int hourOfDay = 0;
    private int minute = 0;

    @Inject
    public HomeViewModel(UserPreferences userPreferences, SleepDao sleepDao) {
        this.userPreferences = userPreferences;
        this.sleepDao = sleepDao;
        mAlarmTime = new MutableLiveData<>();
        updateAlarmValue();
    }

    /**
     * Retrieves the SleepDao instance.
     *
     * @return The SleepDao instance.
     */
    public SleepDao getSleepDao() {
        return sleepDao;
    }

    /**
     * Updates the value of the alarm time LiveData.
     * The alarm time is formatted as a string in the format "HH:MM" using the German locale.
     * The updated value is set to the MutableLiveData object mAlarmTime.
     */
    public void updateAlarmValue() {
        mAlarmTime.setValue(String.format(Locale.GERMAN, "%02d:%02d", this.hourOfDay, this.minute));
    }

    /**
     * Retrieves the LiveData object containing the alarm time.
     *
     * @return The LiveData object containing the alarm time.
     */
    public LiveData<String> getAlarmTime() {
        return mAlarmTime;
    }

    /**
     * Sets the alarm time.
     *
     * @param hourOfDay The hour of the alarm time.
     * @param minute    The minute of the alarm time.
     */
    public void setAlarmTime(int hourOfDay, int minute) {
        this.hourOfDay = hourOfDay % 24;
        this.minute = minute % 60;
        updateAlarmValue();
    }

    /**
     * Retrieves the hour of the alarm time.
     *
     * @return The hour of the alarm time.
     */
    public int getHourOfDay() {
        return hourOfDay;
    }

    /**
     * Retrieves the minute of the alarm time.
     *
     * @return The minute of the alarm time.
     */
    public int getMinute() {
        return minute;
    }

    /**
     * Calculates the optimal alarm time based on user preferences .
     */
    public void calculateOptimalAlarmtime() {
        int age = userPreferences.getAge();
        String gender = userPreferences.getSex();

        int minutes =  calculateSleepAmount(age, gender);

        int currentMin = LocalTime.now().getMinute();
        int currentHour = LocalTime.now().getHour();
        setAlarmTime(currentHour + minutes / 60, currentMin + minutes % 60);

    }

    /**
     * Calculates the recommended sleep amount based on age and gender.
     *
     * @param age    The age of the user.
     * @param gender The gender of the user.
     * @return The recommended sleep amount in minutes.
     */
    public static int calculateSleepAmount(int age, String gender) {
        int recommendedSleepMinutes;

        if (gender.equalsIgnoreCase("male")) {
            if (age >= 18 && age <= 64) {
                recommendedSleepMinutes = 7 * 60; // 7 hours
            } else if (age >= 65) {
                recommendedSleepMinutes = 7 * 60 + 30; // 7.5 hours
            } else {
                recommendedSleepMinutes = 8 * 60; // 8 hours
            }
        } else if (gender.equalsIgnoreCase("female")) {
            if (age >= 18 && age <= 64) {
                recommendedSleepMinutes = 7 * 60 + 30; // 7.5 hours
            } else if (age >= 65) {
                recommendedSleepMinutes = 8 * 60; // 8 hours
            } else {
                recommendedSleepMinutes = 8 * 60 + 30; // 8.5 hours
            }
        } else {
            // If gender is not specified or invalid, assume gender-neutral recommendation
            recommendedSleepMinutes = 8 * 60; // 8 hours
        }

        return recommendedSleepMinutes;
    }


    /**
     * Calculates the number of minutes until the alarm.
     *
     * @return The number of minutes until the alarm.
     */
    public int getMinutesUntilAlarm() {
        Calendar now = Calendar.getInstance();
        int currentHour = now.get(Calendar.HOUR_OF_DAY);
        int currentMinute = now.get(Calendar.MINUTE);

        int hoursDiff = hourOfDay - currentHour;
        if(hoursDiff < 0) {
            hoursDiff+= 24;
        }
        int minutesDiff = minute - currentMinute;
        if(minutesDiff <0) {
            minutesDiff+= 60;
        }

        int totalMinutesDiff = (hoursDiff * 60) + minutesDiff;

        if (totalMinutesDiff < 0) {
            totalMinutesDiff += (24 * 60);
        }

        return totalMinutesDiff;
    }

    /**
     * Saves the alarm to the database.
     *
     * @param withEndDate Specifies if the alarm has an end date.
     * @return The ID of the saved alarm.
     */
    public long  saveAlarmToDB(boolean withEndDate) {
            long id =  sleepDao.insert(new Sleep(
                    LocalDateTime.now(),
                    withEndDate ? LocalDateTime.now().plusMinutes(getMinutesUntilAlarm()) : null,
                    "",
                    -1,
                    0
            ));

            userPreferences.saveCurrentSleepId(id);

            return id;
    }

    /**
     * Checks if an alarm is already set.
     *
     * @return True if an alarm is already set, false otherwise.
     */
    public boolean isAlarmAlreadySet() {
        return userPreferences.getCurrentSleepId() != -1;
    }
}