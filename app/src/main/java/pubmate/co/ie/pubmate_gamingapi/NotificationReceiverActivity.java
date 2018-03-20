package pubmate.co.ie.pubmate_gamingapi;

/**
 * Created by David on 23/01/2018.
 */

import android.app.Activity;
import android.os.Bundle;

public class NotificationReceiverActivity extends Activity {
    /*
        Notification Receiver Class specifies the activity to load when the user clicks on the notification.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.result);
    }
}
