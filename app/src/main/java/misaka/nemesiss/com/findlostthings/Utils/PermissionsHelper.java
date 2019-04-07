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

    static {
        InitRequestedPermissionsList();
    }

    private static void InitRequestedPermissionsList()
    {
        Context context = FindLostThingsApplication.getContext();
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            if (info.requestedPermissions != null) {
                Collections.addAll(NeedAllPermissions, info.requestedPermissions);
            }
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
