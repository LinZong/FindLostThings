package misaka.nemesiss.com.findlostthings.Services.User;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Utils.HMacSha256;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import static android.content.Context.MODE_PRIVATE;

public class APIDocs
{
    public static final String DeploymentAddress = "http://111.230.238.192/learn/lost";
    public static final String RequestAddress = "/user/login";
    public static final String UserInfo = "/user/info";
    public static final String LostThingsCategory = "/category";
    public static final String LostThingsCategoryPartition = "/category/detail?id=";
    public static final String GetStoreBucketKey = "/tencent/coskey";
    public static final String SchoolInfo = "/school/list/";
    public static final String SchoolBuildings = "/school/building?id=";
    public static final String LostThingsInfo = "/thing/publish";
    public static final String ThingList = "/thing/list?";
    public static final String MyPublishList = "/thing/mylist?type=1";
    public static final String MyFindList = "/thing/mylist?type=0";
    public static final String ThingsUpdate = "/thing/update";

    public static final String FullAddress = DeploymentAddress + RequestAddress;
    public static final String FullUserInfo = DeploymentAddress + UserInfo;
    public static final String FullLostThingsCategory = DeploymentAddress + LostThingsCategory;
    public static final String FullLostThingsCategoryPartition = DeploymentAddress + LostThingsCategoryPartition;
    public static final String FullGetStoreBucketKey = DeploymentAddress + GetStoreBucketKey;
    public static final String FullSchoolInfo = DeploymentAddress + SchoolInfo;
    public static final String FullSchoolBuildings = DeploymentAddress + SchoolBuildings;
    public static final String FullLostThingsInfo = DeploymentAddress + LostThingsInfo;
    public static final String FullThingList = DeploymentAddress + ThingList;
    public static final String FullMyPublishList = DeploymentAddress + MyPublishList;
    public static final String FullMyFindList = DeploymentAddress + MyFindList;
    public static final String FullThingsUpdate = DeploymentAddress + ThingsUpdate;

    public static String encryptionAccessToken() throws InvalidKeyException, NoSuchAlgorithmException
    {
        String EncryptedAccessToken = null;
        Context ctx = FindLostThingsApplication.getContext();
        SharedPreferences sp = ctx.getSharedPreferences("LoginReturnData", MODE_PRIVATE);
        String ACTK = sp.getString("access_token", null);
        if (!TextUtils.isEmpty(ACTK))
        {
            EncryptedAccessToken = HMacSha256.Encrypt(ACTK);
            return EncryptedAccessToken;
        }
        return null;
    }

}
