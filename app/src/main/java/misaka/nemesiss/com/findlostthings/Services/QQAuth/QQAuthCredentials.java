package misaka.nemesiss.com.findlostthings.Services.QQAuth;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.UserAccount;
import misaka.nemesiss.com.findlostthings.Tasks.PostInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import org.json.JSONObject;

import static android.content.Context.MODE_PRIVATE;

public class QQAuthCredentials
{
    public static boolean Validate()
    {
        Context ctx = FindLostThingsApplication.getContext();
        SharedPreferences preferences = ctx.getSharedPreferences("LoginReturnData", MODE_PRIVATE);
        boolean HaveUserIdentity = preferences.getBoolean("HaveStoredUserIdentity", false);
        if (HaveUserIdentity)
        {
            String openID = preferences.getString("openID", "");
            String access_token = preferences.getString("access_token", "");
            String expires = preferences.getString("expires", "");
            Tencent mTencent = FindLostThingsApplication.getQQAuthService();

            mTencent.setOpenId(openID);
            mTencent.setAccessToken(access_token,expires);

            if(mTencent.isSessionValid())
            {
                return true;
            }
            else
            {
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("HaveStoredUserIdentity", false);
                editor.apply();
                return false;
            }
        }
        else {
            return false;
        }
    }

}
