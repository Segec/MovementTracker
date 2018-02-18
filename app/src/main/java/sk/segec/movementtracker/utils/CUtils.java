package sk.segec.movementtracker.utils;

import android.app.ActivityManager;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Michal on 18. 2. 2018.
 */
public class CUtils
{
    public static boolean isServiceRunning (Class<?> serviceClass, Context context)
    {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        }
        return false;
    }


    public static String getFullDateFromMilliseconds (Long milliseconds, Context context)
    {
        if (milliseconds != null)
        {
            SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm", context.getResources().getConfiguration().locale);
            Date now = new Date(milliseconds);
            String strDate = format.format(now);
            return strDate;
        }
        else
        {
            return "-";
        }
    }
}
