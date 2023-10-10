package at.geb21.unserprojekt.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import at.geb21.unserprojekt.beans.Sleep;

/**
 * Data Access Object (DAO) interface for the Sleep entity.
 * Defines database operations related to Sleep objects.
 */
@Dao
public interface SleepDao {
    /**
     * Inserts a Sleep object into the database.
     *
     * @param user The Sleep object to insert.
     * @return The ID of the inserted Sleep object.
     */
    @Insert
    long insert(Sleep user);

    /**
     * Retrieves all Sleep objects from the database.
     *
     * @return A list of all Sleep objects.
     */
    @Query("SELECT * FROM sleep")
    List<Sleep> getAllSleep();

    /**
     * Updates a Sleep object in the database.
     *
     * @param user The Sleep object to update.
     */
    @Update
    void update(Sleep user);

    /**
     * Deletes a Sleep object from the database.
     *
     * @param user The Sleep object to delete.
     */
    @Delete
    void delete(Sleep user);

    /**
     * Retrieves a Sleep object from the database based on its ID.
     *
     * @param sleepId The ID of the Sleep object to retrieve.
     * @return The retrieved Sleep object, or null if not found.
     */
    @Query("SELECT * FROM sleep WHERE id = :sleepId")
    Sleep findById(int sleepId);
}
