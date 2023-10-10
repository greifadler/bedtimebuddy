package at.geb21.unserprojekt.ui.statistics;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.util.Collections;
import java.util.List;

import at.geb21.unserprojekt.R;
import at.geb21.unserprojekt.beans.Sleep;
import at.geb21.unserprojekt.databinding.FragmentStatisticsBinding;
import at.geb21.unserprojekt.helpers.Helpers;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class StatisticsFragment extends Fragment {

    private FragmentStatisticsBinding binding;
    private StatisticsViewModel statisticsViewModel;

    /**
     * Called when the fragment should create its associated view hierarchy.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container          The parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     *                           In this case, the fragment should restore its state from the saved bundle.
     * @return The View for the fragment's UI, or null.
     */
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        statisticsViewModel =
                new ViewModelProvider(this).get(StatisticsViewModel.class);

        binding = FragmentStatisticsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        GraphView graphView = binding.idGraphView;

        binding.showNoiseStats.setOnClickListener(it -> {
            statisticsViewModel.setStatsChoice(ALLOWED_STATS.NOISE);
            updateStats();
        });
        binding.showQualityStats.setOnClickListener(it -> {
            statisticsViewModel.setStatsChoice(ALLOWED_STATS.QUALITY);
            updateStats();
        });

        updateStats();

        final TextView textView = binding.textStatistics;
        statisticsViewModel.getStatsChoice().observe(getViewLifecycleOwner(), textView::setText);

        binding.showAsList.setOnClickListener(it -> {
            showSleepListPopup();
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /**
     * Updates the statistics graph asynchronously.
     * Retrieves the selected stats choice from the view model,
     * updates the graph title, color, and text size,
     * and populates the graph with data points using a line series.
     */
    private void updateStats() {
        new Thread(() -> {
            GraphView graphView = binding.idGraphView;

            graphView.setTitle(statisticsViewModel.getStatsChoice().getValue() + " Count");

            graphView.setTitleColor(R.color.purple_200);

            graphView.setTitleTextSize(18);

            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(statisticsViewModel.getDataPoints());

            graphView.removeAllSeries();
            graphView.addSeries(series);


        }).start();
    }

    /**
     * Displays a popup dialog showing a list of sleep entries.
     * The list is fetched from the statisticsViewModel and displayed in a table format.
     * Each sleep entry is represented by a TableRow containing TextViews for each field.
     * The popup is scrollable if the content exceeds the available space.
     * The Dialog has a tile and a ok button
     */
    private void showSleepListPopup() {
        new Thread(() -> {
            List<Sleep> sleepList = statisticsViewModel.getAllSleep();

            getActivity().runOnUiThread(() -> {
                View popupView = LayoutInflater.from(getContext()).inflate(R.layout.popup_sleep_list, null);

                TableLayout tableLayout = popupView.findViewById(R.id.tableLayout);

                for (Sleep sleep : sleepList) {
                    TableRow tableRow = new TableRow(getContext());

                    TextView startTextView = new TextView(getContext());
                    startTextView.setText(Helpers.EXPORT_FORMATTER_SHORT.format(sleep.getStart()));
                    startTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    startTextView.setGravity(Gravity.CENTER);
                    tableRow.addView(startTextView);

                    TextView endTextView = new TextView(getContext());
                    endTextView.setText(Helpers.EXPORT_FORMATTER_SHORT.format(sleep.getEnd()));
                    endTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    endTextView.setGravity(Gravity.CENTER);
                    tableRow.addView(endTextView);

                    TextView noiseCountTextView = new TextView(getContext());
                    noiseCountTextView.setText(String.valueOf(sleep.getNoiseCount()));
                    noiseCountTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    noiseCountTextView.setGravity(Gravity.CENTER);
                    tableRow.addView(noiseCountTextView);

                    TextView qualityTextView = new TextView(getContext());
                    qualityTextView.setText(String.valueOf(sleep.getQuality()));
                    qualityTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));
                    qualityTextView.setGravity(Gravity.CENTER);
                    tableRow.addView(qualityTextView);

                    TextView notesTextView = new TextView(getContext());
                    notesTextView.setText(sleep.getNoteDiary());
                    notesTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 4f));
                    notesTextView.setGravity(Gravity.CENTER);
                    tableRow.addView(notesTextView);


                    tableLayout.addView(tableRow);
                }


                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.sleep_list);
                builder.setView(popupView);
                builder.setPositiveButton(R.string.ok, null);

                AlertDialog dialog = builder.create();
                dialog.show();
            });
        }).start();
    }
}