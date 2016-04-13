package shine.tran.tubeview.gui.businessobjects;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by Administrator on 04/11/2016.
 */
public class MySevice extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO do something useful
        //smsHandler.sendEmptyMessageDelayed(DISPLAY_DATA, 1000);
        return Service.START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO for communication return IBinder implementation
        return null;
    }
}
