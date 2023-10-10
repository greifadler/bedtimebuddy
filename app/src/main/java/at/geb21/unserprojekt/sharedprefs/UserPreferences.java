package at.geb21.unserprojekt.sharedprefs;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Singleton;

/**
 * UserPreferences is a class for managing user preferences and data using SharedPreferences.
 */
@Singleton
public class UserPreferences {
    private static final String PREF_NAME = "UserPreferences";
    private static final String KEY_CURRENT_SLEEP_ID = "sleep_id";
    private static final String KEY_LIGHT_THRESHOLD = "light_threshold";
    private static final String KEY_NAME = "name";
    private static final String KEY_AGE = "age";
    private static final String KEY_SEX = "sex";

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    /**
     * Constructs a UserPreferences object with the specified context.
     *
     * @param context The application context.
     */
    public UserPreferences(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Saves user data in SharedPreferences.
     *
     * @param name The user's name.
     * @param age  The user's age.
     * @param sex  The user's sex.
     */
    public void saveUserData(String name, int age, String sex) {
        editor.putString(KEY_NAME, name);
        editor.putInt(KEY_AGE, age);
        editor.putString(KEY_SEX, sex);
        editor.apply();
    }

    /**
     * Saves the current sleep ID in SharedPreferences.
     *
     * @param id The current sleep ID to be saved.
     */
    public void saveCurrentSleepId(long id) {
        editor.putLong(KEY_CURRENT_SLEEP_ID, id);
        editor.apply();
    }

    /**
     * Removes the current sleep ID from SharedPreferences.
     */
    public void removeCurrentSleepId() {
        editor.putLong(KEY_CURRENT_SLEEP_ID, -1);
        editor.apply();
    }

    /**
     * Saves the light threshold value in SharedPreferences.
     *
     * @param selectedNumber The selected light threshold value.
     */
    public void saveLightThreshold(int selectedNumber) {
        editor.putInt(KEY_LIGHT_THRESHOLD, selectedNumber);
        editor.apply();
    }

    /**
     * Retrieves the user's name from SharedPreferences.
     *
     * @return The user's name.
     */
    public String getName() {
        return sharedPreferences.getString(KEY_NAME, "");
    }

    /**
     * Retrieves the user's age from SharedPreferences.
     *
     * @return The user's age.
     */
    public int getAge() {
        return sharedPreferences.getInt(KEY_AGE, 0);
    }

    /**
     * Retrieves the user's sex from SharedPreferences.
     *
     * @return The user's sex.
     */
    public String getSex() {
        return sharedPreferences.getString(KEY_SEX, "other");
    }

    /**
     * Retrieves the current sleep ID from SharedPreferences.
     *
     * @return The current sleep ID.
     */
    public long getCurrentSleepId() {
        return sharedPreferences.getLong(KEY_CURRENT_SLEEP_ID, -1);
    }


    /**
     * Retrieves the light threshold value from SharedPreferences.
     *
     * @return The light threshold value.
     */
    public int getLightThreshold() {
        return sharedPreferences.getInt(KEY_LIGHT_THRESHOLD, 300);
    }
}
