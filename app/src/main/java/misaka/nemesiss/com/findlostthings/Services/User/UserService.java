package misaka.nemesiss.com.findlostthings.Services.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import com.tencent.connect.UserInfo;
import com.tencent.connect.auth.QQToken;
import com.tencent.tauth.Tencent;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.MyResponse;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;
import misaka.nemesiss.com.findlostthings.Model.UserAccount;
import misaka.nemesiss.com.findlostthings.Tasks.GetUserInformationTask;

import static android.content.Context.MODE_PRIVATE;

public class UserService {

    private static Context ctx;
    private UserAccount userAccount;
    private UserInformation MyProfile = new UserInformation();
    private MyResponse Credentials;
    private static SharedPreferences sp;

    public UserService() {
        ctx = FindLostThingsApplication.getContext();
        sp = ctx.getSharedPreferences("LoginReturnData",MODE_PRIVATE);
    }

    public MyResponse getCredentials() {
        return Credentials;
    }

    public UserInformation getMyProfile() {
        return MyProfile;
    }

    public void setCredentials(MyResponse credentials) {
        Credentials = credentials;
    }

    public void setMyProfile(UserInformation myProfile) {
        MyProfile = myProfile;
    }


    public UserAccount getUserAccount() {
        return userAccount;
    }

    public void setUserAccount(UserAccount userAccount) {
        this.userAccount = userAccount;
    }

    public void SetUserID(long UserID) {
        MyProfile.setId(UserID);
    }

    public long GetUserID() {
        return MyProfile.getId();
    }

    public static UserInfo LoadUserQQProfile() {
        Tencent tencent = FindLostThingsApplication.getQQAuthService();
        QQToken qqToken = tencent.getQQToken();
        return new UserInfo(ctx, qqToken);
    }

    public static void LoadUserProfile() {
        new GetUserInformationTask((result) -> {
            if (result.getStatusCode() == 0) {
                UserService us = FindLostThingsApplication.getUserService();
                us.setMyProfile(result.getUserInfo());
                SetJPushAlias(String.valueOf(us.GetUserID()));
            }
            else Log.e("UserService", "不能获取到自身账户的全部信息 : " + result.getStatusCode());
        }).execute();
    }

    public static void SetJPushAlias(String alias) {
        boolean IsSetAlias = sp.getBoolean("HaveSetAlias",false);
        if (IsSetAlias) {
            String oldAlias = sp.getString("Alias",null);
            if(!TextUtils.isEmpty(oldAlias) && oldAlias.equals(alias)){
                return;
            }
        }
        JPushInterface.setAlias(ctx , 1,alias);
    }

    public static String GetAlias() {
        return sp.getString("Alias",null);
    }

    public static void RemoveAlias() {
        SharedPreferences.Editor EditSp = sp.edit();
        EditSp.putBoolean("HaveSetAlias",false);
        JPushInterface.deleteAlias(ctx,1);
        EditSp.apply();
    }

    public static void PersistAlias(String alias) {

        SharedPreferences.Editor EditSp = sp.edit();
        EditSp.putBoolean("HaveSetAlias",true);
        EditSp.putString("Alias",alias);
        EditSp.apply();
    }
}
