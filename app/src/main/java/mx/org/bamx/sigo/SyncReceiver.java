package mx.org.bamx.sigo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Class for receiving alarms, when automatic sync needs to happen.
 */
public class SyncReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        new CommunityActivity().syncWithSigo();
    }

}