package at.geb21.unserprojekt.helpers;

import androidx.room.TypeConverter;
import java.time.LocalDateTime;

/**
 * Helper class for converting LocalDateTime objects to and from String representation.
 */
public class LocalDateTimeConverter {

    /**
     * Converts a String to a LocalDateTime in the RoomDB format.
     *
     * @param value The String value to be converted.
     * @return The LocalDateTime object parsed from the String, or null if the input value is null.
     */
    @TypeConverter
    public static LocalDateTime fromString(String value) {
        return value != null ? LocalDateTime.parse(value, Helpers.DB_FORMATTER) : null;
    }

    /**
     * Converts a LocalDateTime object to its String representation in the RoomDB format.
     *
     * @param value The LocalDateTime object to be converted.
     * @return The String representation of the LocalDateTime, or null if the input value is null.
     */
    @TypeConverter
    public static String toString(LocalDateTime value) {
        return value != null ? value.format(Helpers.DB_FORMATTER) : null;
    }
}
