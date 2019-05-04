package misaka.nemesiss.com.findlostthings.Services.Common;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLDownloadTask;
import misaka.nemesiss.com.findlostthings.Activity.SplashActivity;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.BuildConfig;
import misaka.nemesiss.com.findlostthings.Model.AppSettings;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.Notifications.AppNotificationChannel;
import misaka.nemesiss.com.findlostthings.Services.StorageBucket.BucketFileOperation;
import misaka.nemesiss.com.findlostthings.Tasks.CheckNewVersionTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import misaka.nemesiss.com.findlostthings.Utils.EventProxy;

import static android.content.Context.NOTIFICATION_SERVICE;

public class AppService {
    private AppSettings appSettings;
    private SharedPreferences sp;
    private Gson gson = new Gson();
    private Context context;
    private String NewVersionApkPath;

    public AppService()
    {
        context = FindLostThingsApplication.getContext();
        sp = context.getSharedPreferences("AppServices",Context.MODE_PRIVATE);
        LoadAppSettings();
    }

    public void SaveAppSettings() {
        String settingJson = gson.toJson(appSettings,AppSettings.class);
        SharedPreferences.Editor EditSp = sp.edit();
        EditSp.putString("AppSettings",settingJson);
        EditSp.apply();
        Log.d("AppService","成功保存AppSettings.");
    }
    public AppSettings GetAppSettings() {
        return appSettings;
    }
    private void LoadAppSettings() {
        new Thread(() -> {
            String settingJson = sp.getString("AppSettings",null);
            if(!TextUtils.isEmpty(settingJson)){
                Log.d("AppService","成功读取先前已完成持久化的AppSettings");
                Log.d("AppService",settingJson);
                appSettings = gson.fromJson(settingJson,AppSettings.class);
            }
            else {
                Log.d("AppService","找不到持久化后的AppSettings，读取默认设置...");
                appSettings = new AppSettings();
                SaveAppSettings();
            }
        }).start();
    }



    public void CheckAppUpdate(Activity activity)
    {
        new CheckNewVersionTask((result) -> {
            if(result != null)
            {
                if(!BuildConfig.VERSION_NAME.equals(result.getVersionNum()))
                {
                    BuildNewVersionTips(result.getDescription(),result.getDownload(),activity);
                }
                else {
                    SplashActivity.GotoMainActivityEvent.emit("finish_check_update", EventProxy.EventStatus.Finish,"finish");
                }
            }
            else
            {
                SplashActivity.GotoMainActivityEvent.emit("finish_check_update", EventProxy.EventStatus.Finish,"finish");
            }
        }).execute();
    }

    private void BuildNewVersionTips(String versionDescription,String downloadPath,Activity activity)
    {
        Context context = FindLostThingsApplication.getContext();
        AlertDialog.Builder HintNewVersionDialog = AppUtils.ShowAlertDialog(activity,false,"发现新版本", versionDescription);
        HintNewVersionDialog.setNegativeButton("取消",(d,l) -> {
            SplashActivity.GotoMainActivityEvent.emit("finish_check_update", EventProxy.EventStatus.Finish,"finish");
        });
        HintNewVersionDialog.setPositiveButton("更新",(d,l) -> {
            DownloadNewVersion(downloadPath);
            SplashActivity.GotoMainActivityEvent.emit("finish_check_update", EventProxy.EventStatus.Finish,"finish");
        });
        HintNewVersionDialog.show();
    }

    private void DownloadNewVersion(String downloadPath)
    {
        String downloadFolder = AppUtils.GetSystemDownloadPath();
        String fileName = "zhxs.apk";
        NewVersionApkPath = downloadFolder + "/" + fileName;
        COSXMLDownloadTask downloadTask = BucketFileOperation.DownloadFile(NewVersionApkPath,downloadPath);
        downloadTask.setCosXmlProgressListener(new CosXmlProgressListener()
        {
            @Override
            public void onProgress(long complete, long target)
            {
                int progress = (int) (complete * 100 /target);

                GetNotificationManager().notify(100,GetNotification("正在下载...",progress));
            }
        });

        downloadTask.setCosXmlResultListener(new CosXmlResultListener()
        {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result)
            {
                GetNotificationManager().notify(100,GetNotification("下载完成，点击安装",-1));
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException)
            {
                GetNotificationManager().notify(100,GetNotification("下载失败",-2));
            }
        });
    }

    private NotificationManager GetNotificationManager(){
        return AppNotificationChannel.GetNotificationManager();
    }

    private Notification GetNotification(String title, int progress){


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(title);

        builder.setVibrate(null);
        builder.setVibrate(new long[]{0L});
        builder.setSound(null);
        builder.setLights(0, 0, 0);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            builder.setChannelId(AppNotificationChannel.ChannelID);
        }
        if(progress>=0){
            builder.setProgress(100,progress,false);
            builder.setContentText(progress+"%");
        }
        else if(progress == -1)
        {
            // 下载完成，点击安装
            AppUtils.InstallApk(NewVersionApkPath);
        }
        else {
            // 下载失败。

        }
        return builder.build();
    }

}
