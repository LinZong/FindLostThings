package misaka.nemesiss.com.findlostthings.Services.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import misaka.nemesiss.com.findlostthings.Activity.QQAuthLogin;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Utils.HMacSha256;
import okhttp3.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import static android.content.Context.MODE_PRIVATE;

public class APIDocs {
    public static final String DeploymentAddress = "http://111.230.238.192/learn/lost";
    public static final String RequestAddress = "/user/login ";
    public static final String UserInfo = "/user/info";
    public static final String LostThingsCategory = "/category";
    public static final String LostThingsCategoryPartition = "/category/detail?id=";

    public static final String FullAddress = DeploymentAddress + RequestAddress;
    public static final String FullUserInfo = DeploymentAddress + UserInfo;
    public static final String FullLostThingsCategory = DeploymentAddress + LostThingsCategory;
    public static final String FullLostThingsCategoryPartition = DeploymentAddress + LostThingsCategoryPartition;

    static  String EncryptedAccessToken = null;

    public static void encryptionAccessToken () {
        Context ctx = FindLostThingsApplication.getContext();
        SharedPreferences sp = ctx.getSharedPreferences("LoginReturnData", MODE_PRIVATE);
        String ACTK = sp.getString("access_token", null);
        if (!TextUtils.isEmpty(ACTK)) {
            try {
                EncryptedAccessToken = HMacSha256.Encrypt(ACTK);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            }
        }
    }

}
