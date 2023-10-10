package at.geb21.unserprojekt.ui.statistics;

import at.geb21.unserprojekt.R;

/**
 * Represents the allowed statistics choices for analysis.
 * The enum values include "NOISE" and "QUALITY".
 */
public enum ALLOWED_STATS {
    /**
     * Represents the "Noise" statistics choice.
     */
    NOISE(R.string.noise),

    /**
     * Represents the "Quality" statistics choice.
     */
    QUALITY(R.string.quality);

    /**
     * The string representation of the statistics choice.
     */
    public final int statsChoice;

    /**
     * Constructs a new ALLOWED_STATS enum with the specified statsChoice.
     *
     * @param statsChoice the string representation of the statistics choice
     */
    ALLOWED_STATS(int statsChoice) {
        this.statsChoice = statsChoice;
    }
}
