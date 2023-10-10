package at.geb21.unserprojekt.beans;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.LocalDateTime;

import at.geb21.unserprojekt.helpers.Helpers;

/**
 * A class that represents a user's sleep.
 * A Sleep object contains information about the sleep period, noise recording path, diary notes,
 * sleep quality, and noise count.
 */
@Entity(tableName = "sleep")
public class Sleep {

    /**
     * Default constructor for the Sleep class.
     */
    public Sleep() {
    }

    /**
     * Constructor for the Sleep class.
     *
     * @param start        The start datetime of the sleep.
     * @param end          The end datetime of the sleep.
     * @param noteDiary    The diary notes.
     * @param quality      The sleep quality.
     * @param noiseCount   The count of recorded noise events.
     */
    public Sleep(LocalDateTime start, LocalDateTime end, String noteDiary, int quality, int noiseCount) {
        this.start = start;
        this.end = end;
        this.noteDiary = noteDiary;
        this.quality = quality;
        this.noiseCount = noiseCount;
    }

    @PrimaryKey(autoGenerate = true)
    private int id;

    private LocalDateTime start;
    private LocalDateTime end;

    private String noteDiary;
    private int quality;
    private int noiseCount;

    /**
     * Get the ID of the sleep.
     *
     * @return The ID of the sleep.
     */
    public int getId() {
        return id;
    }

    /**
     * Set the ID of the sleep.
     *
     * @param id The ID of the sleep.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Get the start datetime of the sleep.
     *
     * @return The start datetime of the sleep.
     */
    public LocalDateTime getStart() {
        return start;
    }

    /**
     * Set the start datetime of the sleep.
     *
     * @param start The start datetime of the sleep.
     */
    public void setStart(LocalDateTime start) {
        this.start = start;
    }

    /**
     * Get the end datetime of the sleep.
     *
     * @return The end datetime of the sleep.
     */
    public LocalDateTime getEnd() {
        return end;
    }

    /**
     * Set the end datetime of the sleep.
     *
     * @param end The end datetime of the sleep.
     */
    public void setEnd(LocalDateTime end) {
        this.end = end;
    }

    /**
     * Get the diary notes.
     *
     * @return The diary notes.
     */
    public String getNoteDiary() {
        return noteDiary;
    }

    /**
     * Set the diary notes.
     *
     * @param noteDiary The diary notes.
     */
    public void setNoteDiary(String noteDiary) {
        this.noteDiary = noteDiary;
    }

    /**
     * Get the sleep quality.
     *
     * @return The sleep quality.
     */
    public int getQuality() {
        return quality;
    }

    /**
     * Set the sleep quality.
     *
     * @param quality The sleep quality.
     */
    public void setQuality(int quality) {
        this.quality = quality;
    }

    /**
     * Get the count of recorded noise events.
     *
     * @return The count of recorded noise events.
     */
    public int getNoiseCount() {
        return noiseCount;
    }

    /**
     * Set the count of recorded noise events.
     *
     * @param noiseCount The count of recorded noise events.
     */
    public void setNoiseCount(int noiseCount) {
        this.noiseCount = noiseCount;
    }

    @Override
    public String toString() {
        if (end != null)
            return String.format("%s - %s, Noise count: %d, Quality: %d, Notes: %s", Helpers.EXPORT_FORMATTER.format(start), Helpers.EXPORT_FORMATTER.format(end), noiseCount, quality, noteDiary);
        else
            return String.format("%s - sunrise, Noise count: %d, Quality: %d, Notes: %s", Helpers.EXPORT_FORMATTER.format(start), noiseCount, quality, noteDiary);
    }
}
