package misaka.nemesiss.com.findlostthings.Utils;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.BuildConfig;
import misaka.nemesiss.com.findlostthings.R;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AppUtils
{
    public static OkHttpClient.Builder clientInstance = null;
    public static String packageName = BuildConfig.APPLICATION_ID;
    public static ProgressDialog ShowProgressDialog(Context ctx, boolean Cancelable, String title, String content){
        ProgressDialog dialog = new ProgressDialog(ctx);
        dialog.setCancelable(Cancelable);
        dialog.setTitle(title);
        dialog.setMessage(content);
        return dialog;
    }

    public static AlertDialog.Builder ShowAlertDialog(Context ctx, boolean Cancelable, String title, String content)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setCancelable(Cancelable);
        dialog.setTitle(title);
        dialog.setMessage(content);
        return dialog;
    }

    public static void ShowNoNetworkError()
    {
        Toast.makeText(FindLostThingsApplication.getContext(), R.string.CannotConnectToServer,Toast.LENGTH_SHORT).show();
    }

    public static List<Pair<String,String>> BearerAuthRequestHeaders(String token)
    {
        List<Pair<String,String>> header = new ArrayList<>();
        header.add(new Pair<>("Authorization","Bearer "+token));
        return header;
    }

    public static SimpleDateFormat TokenDateFormatter()
    {
        return new SimpleDateFormat("yyyy/M/d HH:mm:ss", Locale.CHINA);
    }

    public static boolean ConfirmStringsAllNotEmpty(String... strs)
    {
        for (int i = 0; i < strs.length; i++)
        {
            if(TextUtils.isEmpty(strs[i])) return false;
        }
        return true;
    }

    public static boolean ConfirmResponseSuccessful(Response resp)
    {
        return resp!=null && resp.isSuccessful();
    }

    public static OkHttpClient.Builder GetOkHttpClient()
    {
        if(clientInstance == null)
        {
            clientInstance = new OkHttpClient.Builder().connectTimeout(4500, TimeUnit.MILLISECONDS);
        }
        return clientInstance;
    }

    public static Date UnixStamp2Date(long timeStamp)
    {
        //String  formats = "yyyy-MM-dd HH:mm:ss";
        Long timestamp = timeStamp * 1000;
        return new Date(timestamp);
    }

    public static long Date2UnixStamp(Date date)
    {
        return date.getTime()/1000;
    }

    public static String UnixStampToFmtString(long unix)
    {
        return TokenDateFormatter().format(UnixStamp2Date(unix));
    }

    public static void ToolbarShowReturnButton(AppCompatActivity activity, Toolbar tb){
        activity.setSupportActionBar(tb);
        ActionBar ab = activity.getSupportActionBar();
        if(ab!=null)
        {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }


    public static boolean IfAppIsRunning(Context context)
    {
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for(int i = 0; i < processInfos.size(); i++){
            if(processInfos.get(i).processName.equals(packageName)){
                return true;
            }
        }
        return false;
    }

}
