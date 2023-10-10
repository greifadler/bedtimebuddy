package at.geb21.unserprojekt.dagger;

import android.app.Application;

import androidx.room.Room;

import javax.inject.Inject;
import javax.inject.Singleton;

import at.geb21.unserprojekt.dao.SleepDao;
import at.geb21.unserprojekt.room.MyAppDatabase;
import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

/**
 * Dagger module that provides dependencies related to the database.
 */
@Module
@InstallIn(SingletonComponent.class)
public class DatabaseModule {

    /**
     * Provides the instance of the database.
     *
     * @param application The application context.
     * @return The instance of the database.
     */
    @Provides
    @Singleton
    MyAppDatabase provideDatabase(Application application) {
        return Room.databaseBuilder(application, MyAppDatabase.class, "db").build();
    }

    /**
     * Provides the SleepDao object.
     *
     * @param db The instance of the database.
     * @return The SleepDao object.
     */
    @Provides
    SleepDao provideSleepDao(MyAppDatabase db) {
        return db.sDao();
    }

}
