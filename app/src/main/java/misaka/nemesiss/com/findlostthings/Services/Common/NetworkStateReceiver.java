package misaka.nemesiss.com.findlostthings.Services.Common;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;

public class NetworkStateReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent)
    {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo!= null && networkInfo.isAvailable()) {
            Log.d("NetworkStateReceiver","网络连通");
            FindLostThingsApplication.GetCurrentNetworkStatusObservable().onNext(true);
        } else {
            FindLostThingsApplication.GetCurrentNetworkStatusObservable().onNext(false);
            Log.d("NetworkStateReceiver","网络不通");
        }
    }
}
