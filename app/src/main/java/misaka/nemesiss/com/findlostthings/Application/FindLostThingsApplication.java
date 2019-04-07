package misaka.nemesiss.com.findlostthings.Application;

import android.app.Application;
import android.content.Context;

public class FindLostThingsApplication extends Application
{
    // Define Application-wide context
    private static Context context;

    //Define global services

    //Define global variables

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
    }
    public static Context getContext()
    {
        return context;
    }
}
