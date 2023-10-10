package at.geb21.unserprojekt.sensors;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.time.LocalDateTime;

import at.geb21.unserprojekt.beans.Sleep;
import at.geb21.unserprojekt.dao.SleepDao;

/**
 * SoundRecorder is a class that allows recording sound and detecting noise levels during a specified duration.
 */
public class SoundRecorder {
    private static final int SAMPLE_RATE = 8000; // Sample rate in Hz
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    /**
     * Cooldown between new noise capture in Minutes
     */
    private static final int COOLDOWN_BETWEEN_NOISES = 1;

    private boolean isRecording = false;
    private Context context;
    private SleepDao dao;
    private int sleepId;

    /**
     * Constructs a SoundRecorder object with the specified context, SleepDao, and sleep ID.
     *
     * @param context The application context.
     * @param dao     The SleepDao object for accessing sleep-related data.
     * @param sleepId The ID of the sleep entity.
     * @throws IOException If an I/O error occurs.
     */
    public SoundRecorder(Context context, SleepDao dao, int sleepId) throws IOException {
        this.dao = dao;
        this.context = context;
        this.sleepId = sleepId;
    }

    /**
     * Starts recording sound and detects noise levels for the specified duration.
     *
     * @param minutesToRecord The number of minutes to record sound and detect noise levels.
     */
    public void startRecording(int minutesToRecord) {
        isRecording = true;

        final long durationInMillis = minutesToRecord * 60 * 1000;

        final int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        final int samplesToCapture = (int) ((SAMPLE_RATE * durationInMillis) / bufferSize) + 1;

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            System.out.println("No permissions");
            return;
        }

        final AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE,
                CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);

        final short[] buffer = new short[bufferSize];
        final int[] noiseCounter = {0};

        audioRecord.startRecording();

        new Thread(() -> {
            int totalSamples = 0;
            LocalDateTime nextTheoreticallyNoiseTime = LocalDateTime.now();


            while (isRecording && totalSamples < samplesToCapture) {
                int numSamples = audioRecord.read(buffer, 0, bufferSize);
                totalSamples += numSamples;
                double sum = 0;
                for (int i = 0; i < numSamples / 2; i++) {
                    double y = (buffer[i * 2] | buffer[i * 2 + 1] << 8);
                    sum += y * y;
                }
                double rms = sum / numSamples / 2;
                double dbAmp = 20.0 * Math.log10(rms / 32768.0);

                if (LocalDateTime.now().isAfter(nextTheoreticallyNoiseTime) && dbAmp > 130) {
                    noiseCounter[0]++;
                    Sleep sleep = dao.findById(sleepId);
                    sleep.setNoiseCount(sleep.getNoiseCount() + 1);
                    dao.update(sleep);
                    nextTheoreticallyNoiseTime = LocalDateTime.now().plusMinutes(COOLDOWN_BETWEEN_NOISES);
                }
            }

            audioRecord.stop();
            audioRecord.release();
        }).start();

        new Thread(() -> {
            try {
                Thread.sleep(durationInMillis);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            isRecording = false;
        }).start();

        new Thread(() -> {
            Sleep firstSleep = dao.findById(sleepId);
            if (firstSleep.getEnd() != null) {
                return;
            }

            while (true) {
                Sleep currentSleep = dao.findById(sleepId);
                try {
                    Thread.sleep(5 * 60 * 1000); // 5 Minuten
                    if (currentSleep.getEnd() != null) {
                        isRecording = false;
                        return;
                    }
                } catch (InterruptedException e) {
                    System.err.println(e);
                }
            }

        }).start();

    }
}
