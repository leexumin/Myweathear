package app.leeweather.com.gittest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by leexumin on 2015/12/8.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {
    @Override
    public void  onReceive(Context context,Intent intent){
        Intent i = new Intent(context,AutoUpdateService.class);
        context.startService(i);
    }
}
