package at.geb21.unserprojekt.ui.profile;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.Locale;

import javax.inject.Inject;

import at.geb21.unserprojekt.sharedprefs.UserPreferences;
import dagger.hilt.android.lifecycle.HiltViewModel;
/**
 * ViewModel class for the Profile screen.
 */
@HiltViewModel
public class ProfileViewModel extends ViewModel {

    private UserPreferences sharedPreferences;

    private final MutableLiveData<String> mName;
    private final MutableLiveData<String> mAge;
    private final MutableLiveData<String> mGender;

    private final MutableLiveData<String> mLight;

    /**
     * Constructs a new instance of the ProfileViewModel class.
     *
     * @param sharedPreferences The UserPreferences object for accessing user preferences.
     */
    @Inject
    public ProfileViewModel(UserPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
        mName = new MutableLiveData<>();
        mAge = new MutableLiveData<>();
        mGender = new MutableLiveData<>();
        mLight = new MutableLiveData<>();

        updateVals();
    }

    /**
     * Updates the LiveData objects with the latest values from the UserPreferences.
     */
    private void updateVals() {
        mName.setValue(sharedPreferences.getName());
        mAge.setValue(sharedPreferences.getAge() + " years");
        mGender.setValue(sharedPreferences.getSex());
        mLight.setValue(sharedPreferences.getLightThreshold()+"");
    }

    /**
     * Returns the LiveData object containing the user's name.
     *
     * @return The LiveData object for the user's name.
     */
    public LiveData<String> getName() {
        return mName;
    }

    /**
     * Returns the LiveData object containing the user's age.
     *
     * @return The LiveData object for the user's age.
     */
    public LiveData<String> getAge() {
        return mAge;
    }

    /**
     * Returns the LiveData object containing the user's gender.
     *
     * @return The LiveData object for the user's gender.
     */
    public LiveData<String> getGender() {
        return mGender;
    }

    /**
     * Saves the user's profile information.
     *
     * @param name   The user's name.
     * @param age    The user's age.
     * @param gender The user's gender.
     */
    public void saveProfile(String name, int age, String gender) {
        sharedPreferences.saveUserData(name, age, gender);
        updateVals();
    }

    /**
     * Saves the selected light amount threshold.
     *
     * @param selectedNumber The selected light amount threshold.
     */
    public void saveLightAmount(int selectedNumber) {
        sharedPreferences.saveLightThreshold(selectedNumber);
        mLight.setValue(selectedNumber+"");
    }

    /**
     * Returns the current light amount threshold.
     *
     * @return The current light amount threshold.
     */
    public int getLightAmount() {
        return sharedPreferences.getLightThreshold();
    }

    /**
     * Returns the LiveData object containing the current light amount threshold.
     *
     * @return The LiveData object for the current light amount threshold.
     */
    public LiveData<String> getLightAmountLd() {
        return mLight;
    }


}
