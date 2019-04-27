package misaka.nemesiss.com.findlostthings.Services.StorageBucket;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.tencent.qcloud.core.auth.BasicLifecycleCredentialProvider;
import com.tencent.qcloud.core.auth.QCloudLifecycleCredentials;
import com.tencent.qcloud.core.auth.SessionQCloudCredentials;
import com.tencent.qcloud.core.common.QCloudClientException;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Model.MyResponse;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Tasks.GetStoreBucketKeySync;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.MODE_PRIVATE;

public class CustomCredentialProvider extends BasicLifecycleCredentialProvider
{
    private String actk;
    private String userid;
    public CustomCredentialProvider()
    {

    }
    public CustomCredentialProvider(String AccessToken,String UserID)
    {
        SetAccessTokenAndUserID(AccessToken,UserID);
    }
    public void SetAccessTokenAndUserID(String AccessToken,String UserID)
    {
        actk = AccessToken;
        userid = UserID;
    }
    public void LoadAccessTokenAndUserID()
    {

        try {
            actk = QQAuthCredentials.GetEncryptedAccessToken();//QQ登陆后的回调里给出. PersistUserInfo 第一层回调。
            userid = String.valueOf(FindLostThingsApplication.getUserService().GetUserID()); //在向服务器上报此次登陆的用户信息之后取得.PostUserInformationAsyncTask
            SetAccessTokenAndUserID(actk,userid);

        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    @Override
    protected QCloudLifecycleCredentials fetchNewCredentials() throws QCloudClientException,IllegalArgumentException
    {
        if(!AppUtils.ConfirmStringsAllNotEmpty(actk,userid))
        {
            throw new IllegalArgumentException("传入的AccessToken和UserID不可以为空，否则无法获取临时Credentials!");
        }
        GetStoreBucketKeySync getStoreBucketKeySync = new GetStoreBucketKeySync(actk, userid);
        try
        {
            MyResponse response = getStoreBucketKeySync.GetTempKeySync().getResponse();
            String tmpSecretId = response.getCredentials().getTmpSecretId();
            String tmpSecretKey = response.getCredentials().getTmpSecretKey();
            String sessionToken = response.getCredentials().getToken();
            long expireTime = response.getExpiredTime();
            return new SessionQCloudCredentials(tmpSecretId,tmpSecretKey,sessionToken,expireTime);
        } catch (IOException e)
        {
            e.printStackTrace();
            Log.e("CustomCredential","获取临时Key的请求发送失败，网络连接错误。");
        }
        return null;
    }
}
