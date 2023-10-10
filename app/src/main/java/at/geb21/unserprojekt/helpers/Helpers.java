package at.geb21.unserprojekt.helpers;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import at.geb21.unserprojekt.beans.Sleep;
import at.geb21.unserprojekt.dao.SleepDao;

/**
 * Helper class that provides various DateTimeFormatters for formatting date and time.
 */
public class Helpers {

    /**
     * Formatter for exporting date and time in the format "dd.MM.yyyy HH:mm".
     * Example: "02.07.2023 15:30"
     */
    public static final DateTimeFormatter EXPORT_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    /**
     * Formatter for exporting date and time in the format "dd.MM HH:mm".
     * Example: "02.07 15:30"
     */
    public static final DateTimeFormatter EXPORT_FORMATTER_SHORT = DateTimeFormatter.ofPattern("dd.MM HH:mm");

    /**
     * Formatter for exporting time only in the format "HH:mm".
     * Example: "15:30"
     */
    public static final DateTimeFormatter EXPORT_FORMATTER_ONLY_TIME = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Formatter for database storage in the ISO_LOCAL_DATE_TIME format.
     * Example: "2023-07-02T15:30:00"
     */
    public static final DateTimeFormatter DB_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    public static void insertDummyDataToDB(SleepDao dao) {
        LocalDateTime currentDate = LocalDateTime.now();
        Random random = new Random();

        for (int i = 1; i < 10; i++) {
            LocalDateTime entryDate = currentDate.minus(i, ChronoUnit.DAYS).withHour(22).withMinute(0).plusMinutes(random.nextInt(120));
            LocalDateTime entryEndTime = entryDate.plusHours((long) (7 + random.nextDouble() * 1.5));


            String[] sleepNotesAndDreams = {
                    "Dreamed of flying",
                    "Had a peaceful sleep",
                    "Dreamt of a beautiful beach",
                    "Woke up feeling refreshed",
                    "Dreamed of a magical forest",
                    "Had a deep and restful sleep",
                    "Dreamt of an adventure",
                    "Woke up with a smile",
                    "Had a dream about a loved one",
                    "Dreamed of exploring a new world",
                    "Woke up feeling energized",
                    "Had a dream about achieving goals",
                    "Dreamt of a relaxing vacation",
                    "Woke up with a sense of gratitude",
                    "Had a dream about solving a puzzle",
                    "Dreamed of meeting a famous person",
                    "Woke up feeling inspired",
                    "Had a dream about a childhood memory",
                    "Dreamt of a peaceful garden",
                    "Woke up with a feeling of serenity"
            };

            List<String> selectedNotes = new ArrayList<>();
            for (int j = 0; j < 3; j++) {
                int index = random.nextInt(sleepNotesAndDreams.length);
                selectedNotes.add(sleepNotesAndDreams[index]);
            }

            String notes = String.join(" - ", selectedNotes);

            int quality = random.nextInt(5) + 1;
            int noiseCount = random.nextInt(11);

            Sleep dummyEntry = new Sleep(entryDate, entryEndTime, notes, quality, noiseCount);

            dao.insert(dummyEntry);
        }
    }

}
