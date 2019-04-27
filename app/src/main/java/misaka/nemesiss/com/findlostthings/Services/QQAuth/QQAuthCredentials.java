package misaka.nemesiss.com.findlostthings.Services.QQAuth;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import com.tencent.tauth.Tencent;
import misaka.nemesiss.com.findlostthings.Activity.QQAuthLoginActivity;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Tasks.PostUserInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import misaka.nemesiss.com.findlostthings.Utils.HMacSha256;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.MODE_PRIVATE;

public class QQAuthCredentials
{
    private static Context ctx = FindLostThingsApplication.getContext();
    private static SharedPreferences preferences = ctx.getSharedPreferences("LoginReturnData", MODE_PRIVATE);

    public static boolean Validate()
    {

        boolean HaveUserIdentity = preferences.getBoolean("HaveStoredUserIdentity", false);
        if (HaveUserIdentity)
        {
            String openID = preferences.getString("openID", "");
            String access_token = preferences.getString("access_token", "");
            String expires = preferences.getString("expires", "");
            Tencent mTencent = FindLostThingsApplication.getQQAuthService();
            mTencent.setOpenId(openID);
            mTencent.setAccessToken(access_token,expires);
            return mTencent.isSessionValid();
        }
        else {
            return false;
        }
    }

    public static String GetAccessToken()
    {
        Context ctx = FindLostThingsApplication.getContext();
        SharedPreferences sp = ctx.getSharedPreferences("LoginReturnData", MODE_PRIVATE);
        boolean IsHaveStoredUserIdentity = sp.getBoolean("HaveStoredUserIdentity",false);

        if(IsHaveStoredUserIdentity){
            return sp.getString("access_token", null);
        }
        return null;
    }

    public static String GetEncryptedAccessToken() throws InvalidKeyException, NoSuchAlgorithmException {
        String ACTK = GetAccessToken();
        if(!TextUtils.isEmpty(ACTK)){
            return HMacSha256.Encrypt(ACTK);
        }
        throw new IllegalArgumentException("先前持久化的AccessToken不正确，为空!");
    }

    public static void PushLoginInfoToBackend(String OpenID,String NickName) {

        new PostUserInformationAsyncTask((res) ->
        {
            if (res.getStatusCode() != 0) {
                Log.d("QQAuthCredentials", "上报数据给服务器出现异常!");
            }
            //将服务器返回的UserID存入UserService对象。
            FindLostThingsApplication.getUserService().SetUserID(res.getUserID());
            Log.d("QQAuthCredentials", "成功上报数据给服务器，活动退出.");
            //根据登录后获得到的全部内容，继续完成关键部件的加载。
            FindLostThingsApplication.ReloadAfterLogin();

        }).execute(OpenID, NickName, AppUtils.getAndroidId(ctx));
    }
}