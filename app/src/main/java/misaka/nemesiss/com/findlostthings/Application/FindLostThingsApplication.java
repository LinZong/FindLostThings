package misaka.nemesiss.com.findlostthings.Application;

import android.content.Context;
import android.content.IntentFilter;
import android.support.multidex.MultiDexApplication;
import cn.jpush.android.api.JPushInterface;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.tauth.Tencent;
import misaka.nemesiss.com.findlostthings.Activity.SplashActivity;
import misaka.nemesiss.com.findlostthings.Services.Common.AppService;
import misaka.nemesiss.com.findlostthings.Services.Common.NetworkStateReceiver;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthInfo;
import misaka.nemesiss.com.findlostthings.Services.StorageBucket.BucketInfo;
import misaka.nemesiss.com.findlostthings.Services.StorageBucket.CustomCredentialProvider;
import misaka.nemesiss.com.findlostthings.Services.Thing.ThingServices;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import misaka.nemesiss.com.findlostthings.Utils.EventProxy;
import rx.subjects.BehaviorSubject;

import java.util.Queue;

public class FindLostThingsApplication extends MultiDexApplication
{
    // Define Application-wide context
    private static Context context;

    //Define global services
    // 3rd-party services.
    private static CosXmlServiceConfig cosXmlServiceConfig;
    private static QCloudCredentialProvider credentialProvider;
    private static CosXmlService cosXmlService;
    private static Tencent QQAuthService;

    //our app services.
    private static UserService userService;
    private static AppService appService;
    private static ThingServices thingServices;
    //Define global variables

    //修改这个标志位可以跳过QQ登陆直接进入界面。但是相应功能会被屏蔽
    public static boolean JumpOutQQLogin = false;

    private static Queue<Runnable> PendingNetworkTasks;

    private static BehaviorSubject<Boolean> CurrentNetworkStatus;

    private NetworkStateReceiver networkStateReceiver;
    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
        CurrentNetworkStatus = BehaviorSubject.create(AppUtils.IfAppIsRunning(context));

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        networkStateReceiver = new NetworkStateReceiver();
        registerReceiver(networkStateReceiver,intentFilter);

        QQAuthService = Tencent.createInstance(QQAuthInfo.APPID,context);
        cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(BucketInfo.AppID,BucketInfo.Region)
                .isHttps(true)
                .setDebuggable(true)
                .builder();
        credentialProvider = new CustomCredentialProvider();
        cosXmlService = new CosXmlService(context, getCosXmlServiceConfig(), credentialProvider);
        JPushInterface.init(this);
        JPushInterface.setDebugMode(true);

        //加载此App自己的服务.
        userService = new UserService();
        appService = new AppService();
        thingServices = new ThingServices();

        
    }

    @Override
    public void onTerminate()
    {
        super.onTerminate();
        unregisterReceiver(networkStateReceiver);
    }

    public static Context getContext()
    {
        return context;
    }

    public static CosXmlServiceConfig getCosXmlServiceConfig()
    {
        return cosXmlServiceConfig;
    }

    public static CosXmlService getCosXmlService()
    {
        return cosXmlService;
    }
    public static QCloudCredentialProvider getCredentialProvider()
    {
        return credentialProvider;
    }

    public static Tencent getQQAuthService()
    {
        return QQAuthService;
    }

    public static UserService getUserService() {
        return userService;
    }

    public static AppService getAppService() {
        return appService;
    }

    public static void ReloadBeforeLogin() {
        thingServices.ReloadSchoolList(() -> {
            SplashActivity.GotoMainActivityEvent.tryemit("get_school_name", EventProxy.EventStatus.Finish,"GetSchoolNameFinish");
        });
        thingServices.ReloadThingCategory(() -> {
            SplashActivity.GotoMainActivityEvent.tryemit("get_thing_category", EventProxy.EventStatus.Finish,"GetThingCategoryFinish");
        });
        thingServices.ReloadAllThingDetail(() -> {
            SplashActivity.GotoMainActivityEvent.tryemit("get_thing_detail", EventProxy.EventStatus.Finish,"GetThingDetailFinish");
        });
    }

    public static void ReloadAfterLogin()
    {
        //这里放置一些在完成全部Login操作之后需要执行的语句.
        ((CustomCredentialProvider)credentialProvider).LoadAccessTokenAndUserID();
        UserService.LoadUserProfile();
    }

    public static ThingServices getThingServices()
    {
        return thingServices;
    }

    public static BehaviorSubject<Boolean> GetCurrentNetworkStatusObservable()
    {
        return CurrentNetworkStatus;
    }
}
