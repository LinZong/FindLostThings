package misaka.nemesiss.com.findlostthings.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PermissionsHelper
{
    private static ArrayList<String> NeedAllPermissions = new ArrayList<>();
    public static final int GRANT_ALL_PERMISSION_CODE = 9999;
    private static boolean DontShowAgain = false;

    static {
        InitRequestedPermissionsList();
    }

    public static boolean GetDontShowAgain()
    {
        return DontShowAgain;
    }

    public static void SetDontShowAgain(boolean value)
    {
        DontShowAgain = value;
    }

    private static void InitRequestedPermissionsList()
    {
        Context context = FindLostThingsApplication.getContext();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                Collections.addAll(NeedAllPermissions, info.requestedPermissions);
            }
            //清除掉极光推送不应该申请的权限.
            NeedAllPermissions.remove("android.permission.RECEIVE_USER_PRESENT");
            NeedAllPermissions.remove("android.permission.MOUNT_UNMOUNT_FILESYSTEMS");
            NeedAllPermissions.remove("android.permission.WRITE_SETTINGS");
            NeedAllPermissions.remove("android.permission.SYSTEM_ALERT_WINDOW");
            NeedAllPermissions.remove("android.permission.CHANGE_NETWORK_STATE");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ArrayList<String> GetPermissionsList()
    {
        return NeedAllPermissions;
    }

    public static boolean RequestPermissions(Context ctx, Activity activity, ArrayList<String> RequestPermissionList)
    {
        List<String> NoGrantedPermissions = new ArrayList<>();
        for (String needPermission : RequestPermissionList)
        {
            if (ContextCompat.checkSelfPermission(ctx, needPermission) != PackageManager.PERMISSION_GRANTED)
            {
                NoGrantedPermissions.add(needPermission);
            }
        }
        if (!NoGrantedPermissions.isEmpty())
        {
            //shouldShowRequestPermissionRationale(Manifest.permission.WRITE_CONTACTS)
            ActivityCompat.requestPermissions(activity, NoGrantedPermissions.toArray(new String[0]), GRANT_ALL_PERMISSION_CODE);
            return false;
        }
        return true;
    }
    public static boolean RequestAllPermissions(Context ctx, Activity activity)
    {
        return RequestPermissions(ctx, activity, NeedAllPermissions);
    }
}
