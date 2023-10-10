package at.geb21.unserprojekt.ui.alarm;

import android.app.Dialog;
import android.app.job.JobService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.PowerManager;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.legacy.content.WakefulBroadcastReceiver;

import at.geb21.unserprojekt.R;

/**
 * The YourAlarmReceiver class is responsible for receiving the alarm broadcast and starting the
 * alarm activity.
 */
public class YourAlarmReceiver extends WakefulBroadcastReceiver {

    /**
     * Called when the alarm broadcast is received.
     *
     * @param context The context in which the receiver is running.
     * @param intent  The intent containing information about the broadcast.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Please wake up!", Toast.LENGTH_SHORT).show();

        PowerManager powerManager = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "YourAlarmReceiver:WakeLock");
        wakeLock.acquire(10 * 60 * 1000L /*10 minutes*/);

        Intent alarmIntent = new Intent(context, AlarmActivity.class);
        alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(alarmIntent);
    }

}
