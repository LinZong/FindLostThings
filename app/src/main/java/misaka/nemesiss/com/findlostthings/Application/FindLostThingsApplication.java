package misaka.nemesiss.com.findlostthings.Application;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import misaka.nemesiss.com.findlostthings.Activity.TryUploadFilesActivity;
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
    //Define global variables

    @Override
    public void onCreate()
    {
        super.onCreate();
        context = getApplicationContext();
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
}
