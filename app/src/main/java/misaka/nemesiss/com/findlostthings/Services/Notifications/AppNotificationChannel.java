package misaka.nemesiss.com.findlostthings.Services.Notifications;

import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AppNotificationChannel
{

    public static Context context;
    public static NotificationManager manager;
    public static String ChannelID = "99";
    public static String channelName = "AppChannel";
    static {
        context = FindLostThingsApplication.getContext();
    }

    @TargetApi(Build.VERSION_CODES.O)
    public static NotificationChannel GetNotificationChannel()
    {


        NotificationChannel channel = new NotificationChannel(ChannelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);
        return channel;
    }

    public static NotificationManager GetNotificationManager()
    {
        if(manager == null ){
            manager = (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            {
                manager.createNotificationChannel(GetNotificationChannel());
            }
        }
        return manager;
    }
}
