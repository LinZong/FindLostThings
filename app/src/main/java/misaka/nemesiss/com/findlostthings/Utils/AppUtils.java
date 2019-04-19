package misaka.nemesiss.com.findlostthings.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;
import com.yalantis.ucrop.UCrop;
import misaka.nemesiss.com.findlostthings.Activity.PickupImageActivity;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.BuildConfig;
import misaka.nemesiss.com.findlostthings.R;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class AppUtils
{
    public static final String RESOURCE = "android.resource://";
    public static OkHttpClient.Builder clientInstance = null;
    public static String packageName = BuildConfig.APPLICATION_ID;
    public static final String IMAGE_TYPE = "image/jpeg";
    public static final int TYPE_CAMERA = 1234;
    public static ProgressDialog ShowProgressDialog(Context ctx, boolean Cancelable, String title, String content){
        ProgressDialog dialog = new ProgressDialog(ctx);
        dialog.setCancelable(Cancelable);
        dialog.setTitle(title);
        dialog.setMessage(content);
        return dialog;
    }

    public static AlertDialog.Builder ShowAlertDialog(Context ctx, boolean Cancelable, String title, String content)
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(ctx);
        dialog.setCancelable(Cancelable);
        dialog.setTitle(title);
        dialog.setMessage(content);
        return dialog;
    }

    public static void ShowNoNetworkError()
    {
        Toast.makeText(FindLostThingsApplication.getContext(), R.string.CannotConnectToServer,Toast.LENGTH_SHORT).show();
    }

    public static List<Pair<String,String>> BearerAuthRequestHeaders(String token) //暂时用不到
    {
        List<Pair<String,String>> header = new ArrayList<>();
        header.add(new Pair<>("Authorization","Bearer "+token));
        return header;
    }

    public static SimpleDateFormat TokenDateFormatter()
    {
        return new SimpleDateFormat("yyyy/M/d HH:mm:ss", Locale.CHINA);
    }

    public static boolean ConfirmStringsAllNotEmpty(String... strs)
    {
        for (int i = 0; i < strs.length; i++)
        {
            if(TextUtils.isEmpty(strs[i])) return false;
        }
        return true;
    }

    public static boolean ConfirmResponseSuccessful(Response resp)
    {
        return resp!=null && resp.isSuccessful();
    }

    public static OkHttpClient.Builder GetOkHttpClient()
    {
        if(clientInstance == null)
        {
            clientInstance = new OkHttpClient.Builder().connectTimeout(4500, TimeUnit.MILLISECONDS);
        }
        return clientInstance;
    }

    public static Date UnixStamp2Date(long timeStamp) //1970年到现在过了多少秒为时间戳UnixStamp
    {
        //String  formats = "yyyy-MM-dd HH:mm:ss";
        Long timestamp = timeStamp * 1000;
        return new Date(timestamp);
    }

    public static long Date2UnixStamp(Date date)
    {
        return date.getTime()/1000;
    }

    public static String UnixStampToFmtString(long unix)
    {
        return TokenDateFormatter().format(UnixStamp2Date(unix));
    }

    public static void ToolbarShowReturnButton(AppCompatActivity activity, Toolbar tb){//toolbar返回键
        activity.setSupportActionBar(tb);
        ActionBar ab = activity.getSupportActionBar();
        if(ab!=null)
        {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }


    public static boolean IfAppIsRunning(Context context)
    {
        ActivityManager activityManager =
                (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for(int i = 0; i < processInfos.size(); i++){
            if(processInfos.get(i).processName.equals(packageName)){
                return true;
            }
        }
        return false;
    }

    public static String GetSystemDCIMPath()
    {
        //  /storage/emulated/0/DCIM/Camera
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    public static String GetAppDataDCIMPath()
    {
        //  /storage/emulated/0/Android/data/misaka.nemesiss.com.findlostthings/files/DCIM
        File[] MountedSdcardPrefix = ContextCompat.getExternalFilesDirs(FindLostThingsApplication.getContext(),null);
        File Path = new File(MountedSdcardPrefix.length>1?MountedSdcardPrefix[1]:MountedSdcardPrefix[0], Environment.DIRECTORY_DCIM);
        return Path.getAbsolutePath();
    }
    public static Uri ParseResourceIdToUri(int resId)
    {
        return Uri.parse(RESOURCE+packageName+"/"+resId);
    }

    public static void OpenCamera(Uri WangStoreImageUri, Activity CallCameraActivity)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, WangStoreImageUri);
        } else {
            ContentValues contentValues = new ContentValues(1);
            contentValues.put(MediaStore.Images.Media.DATA, WangStoreImageUri.getPath());
            contentValues.put(MediaStore.Images.Media.MIME_TYPE, IMAGE_TYPE);
            Uri uri = CallCameraActivity.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        }
        CallCameraActivity.startActivityForResult(intent, TYPE_CAMERA);
    }

    public static UCrop OpenUCrop(Uri OriginalUri)
    {
        Context context = FindLostThingsApplication.getContext();
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(context.getResources().getColor(R.color.colorPrimary));
        options.setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark));
        options.setFreeStyleCropEnabled(true);
        String OriginalImageSavedPath = OriginalUri.getPath();
        String CroppedPath = AppUtils.GetCroppedPath(OriginalImageSavedPath);
        UCrop of = UCrop.of(OriginalUri,Uri.fromFile(new File(CroppedPath))).withOptions(options);
        return of;
    }
    public static String GetCroppedPath(String originalPath)
    {
        return originalPath.substring(0,originalPath.lastIndexOf("."))+"__CROPPED.jpg";
    }

    public static String GetTempImageName()
    {
        return System.currentTimeMillis() + ".jpg";
    }
}
