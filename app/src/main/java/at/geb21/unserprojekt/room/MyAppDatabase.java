package at.geb21.unserprojekt.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import at.geb21.unserprojekt.beans.Sleep;
import at.geb21.unserprojekt.dao.SleepDao;
import at.geb21.unserprojekt.helpers.LocalDateTimeConverter;

/**
 * Room database class that serves as the main access point to the underlying SQLite database.
 */
@Database(entities = {Sleep.class}, version = 1)
@TypeConverters(LocalDateTimeConverter.class)
public abstract class MyAppDatabase extends RoomDatabase {

    /**
     * Retrieves the DAO (Data Access Object) for accessing Sleep-related data in the database.
     *
     * @return The SleepDao object for performing database operations on Sleep entities.
     */
    public abstract SleepDao sDao();

}
