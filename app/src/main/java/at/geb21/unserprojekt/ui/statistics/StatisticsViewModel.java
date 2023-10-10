package at.geb21.unserprojekt.ui.statistics;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.jjoe64.graphview.series.DataPoint;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import at.geb21.unserprojekt.beans.Sleep;
import at.geb21.unserprojekt.dao.SleepDao;
import dagger.hilt.android.lifecycle.HiltViewModel;


@HiltViewModel
public class StatisticsViewModel extends ViewModel {

    private final MutableLiveData<Integer> mStatsChoice;

    private final SleepDao sleepDao;


    /**
     * Constructs a new instance of the StatisticsViewModel with the provided dependencies.
     * Initializes the statistics choice LiveData object with a default value of "Noise" and sets up the SleepDao.
     *
     * @param sleepDao the SleepDao object used for retrieving sleep records.
     */
    @Inject
    public StatisticsViewModel(SleepDao sleepDao) {
        mStatsChoice = new MutableLiveData<>();
        mStatsChoice.setValue(ALLOWED_STATS.NOISE.statsChoice);

        this.sleepDao = sleepDao;
    }

    /**
     * Retrieves the LiveData object representing the selected statistics choice.
     *
     * @return the LiveData object containing the selected statistics choice.
     */
    public LiveData<Integer> getStatsChoice() {
        return mStatsChoice;
    }

    /**
     * Sets the selected statistics choice using the provided ALLOWED_STATS value.
     * The value is stored in the mStatsChoice LiveData object.
     *
     * @param stat the ALLOWED_STATS value representing the selected statistics choice.
     */
    public void setStatsChoice(ALLOWED_STATS stat) {
        mStatsChoice.setValue(stat.statsChoice);
    }

    /**
     * Retrieves an array of DataPoint objects representing the relevant data points
     * for the selected statistics choice. The data points are extracted from the sleep records
     * obtained from the SleepDao and filtered based on the current time.
     *
     * @return an array of DataPoint objects representing the relevant data points.
     */
    public DataPoint[] getDataPoints() {
        List<DataPoint> points = new ArrayList<>();
        sleepDao.getAllSleep().forEach(sleep -> {
            System.out.println(sleep);
            if(sleep.getStart().isBefore(LocalDateTime.now()))
            {
                long dayX = ChronoUnit.DAYS.between(sleep.getStart(), LocalDateTime.now());
                int valY = -1;

                if(mStatsChoice.getValue().equals(ALLOWED_STATS.NOISE.statsChoice)) {
                    valY = sleep.getNoiseCount();
                }

                if(mStatsChoice.getValue().equals(ALLOWED_STATS.QUALITY.statsChoice)) {
                    valY = sleep.getQuality();
                }


                points.add(new DataPoint(dayX, valY));
            }
        });

        points.sort(Comparator.comparingDouble(DataPoint::getX));

        DataPoint[] pointsArr = new DataPoint[points.size()];
        for (int i = 0; i < points.size(); i++) {
            pointsArr[i] = points.get(i);
        }

        return pointsArr;
    }

    public List<Sleep> getAllSleep() {
        return sleepDao.getAllSleep();
    }
}