package misaka.nemesiss.com.findlostthings.Application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.tauth.Tencent;
import misaka.nemesiss.com.findlostthings.Activity.TryUploadFilesActivity;
import misaka.nemesiss.com.findlostthings.Model.UserAccount;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthInfo;
import misaka.nemesiss.com.findlostthings.StorageBucket.BucketInfo;
import misaka.nemesiss.com.findlostthings.StorageBucket.CustomCredentialProvider;

public class FindLostThingsApplication extends MultiDexApplication
{
    // Define Application-wide context
    private static Context context;

    //Define global services

    private static CosXmlServiceConfig cosXmlServiceConfig;
    private static QCloudCredentialProvider credentialProvider;
    private static CosXmlService cosXmlService;
    private static Tencent QQAuthService;
    //Define global variables

    private static UserAccount LoginUserAccount;

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
        LoginUserAccount = new UserAccount();
        QQAuthService = Tencent.createInstance(QQAuthInfo.APPID,context);
        cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(BucketInfo.AppID,BucketInfo.Region)
                .isHttps(true)
                .setDebuggable(true)
                .builder();
        credentialProvider = new CustomCredentialProvider("actkRelax","36767411659079680");
        cosXmlService = new CosXmlService(context, getCosXmlServiceConfig(), credentialProvider);
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

    public static UserAccount getLoginUserAccount()
    {
        return LoginUserAccount;
    }

    public static void setLoginUserAccount(UserAccount loginUserAccount)
    {
        LoginUserAccount = loginUserAccount;
    }
}
