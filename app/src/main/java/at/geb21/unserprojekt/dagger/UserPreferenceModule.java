package at.geb21.unserprojekt.dagger;

import android.content.Context;
import android.content.SharedPreferences;
import javax.inject.Singleton;

import at.geb21.unserprojekt.sharedprefs.UserPreferences;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

/**
 * Dagger module that provides the UserPreferences dependency.
 */
@Module
@InstallIn(SingletonComponent.class)
public class UserPreferenceModule {

    /**
     * Default constructor for the UserPreferenceModule class.
     */
    public UserPreferenceModule() {
    }

    /**
     * Provides the instance of UserPreferences.
     *
     * @param context The application context.
     * @return The UserPreferences instance.
     */
    @Singleton
    @Provides
    public UserPreferences provideUserPreferences(@ApplicationContext Context context) {
        return new UserPreferences(context);
    }
}
