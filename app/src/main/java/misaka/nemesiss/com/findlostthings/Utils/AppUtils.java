package misaka.nemesiss.com.findlostthings.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Pair;
import android.widget.Toast;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.yalantis.ucrop.UCrop;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.BuildConfig;
import misaka.nemesiss.com.findlostthings.Model.Response.CommonResponse;
import misaka.nemesiss.com.findlostthings.R;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Response;

import java.io.File;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class AppUtils
{
    public static final String RESOURCE = "android.resource://";
    public static OkHttpClient.Builder clientInstance = null;
    public static String packageName = BuildConfig.APPLICATION_ID;
    public static final String IMAGE_TYPE = "image/jpeg";
    public static final int TYPE_CAMERA = 1234;

    public static ProgressDialog ShowProgressDialog(Context ctx, boolean Cancelable, String title, String content)
    {
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
        Toast.makeText(FindLostThingsApplication.getContext(), R.string.CannotConnectToServer, Toast.LENGTH_SHORT).show();
    }


    public static List<Pair<String, String>> BearerAuthRequestHeaders(String token) //暂时用不到
    {
        List<Pair<String, String>> header = new ArrayList<>();
        header.add(new Pair<>("Authorization", "Bearer " + token));
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
            if (TextUtils.isEmpty(strs[i])) return false;
        }
        return true;
    }

    public static boolean ConfirmResponseSuccessful(Response resp)
    {
        return resp != null && resp.isSuccessful();
    }

    public static OkHttpClient.Builder GetOkHttpClient()
    {
        if (clientInstance == null)
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
        return date.getTime() / 1000;
    }

    public static String UnixStampToFmtString(long unix)
    {
        return TokenDateFormatter().format(UnixStamp2Date(unix));
    }

    public static void ToolbarShowReturnButton(AppCompatActivity activity, Toolbar tb)
    {//toolbar返回键
        activity.setSupportActionBar(tb);
        ActionBar ab = activity.getSupportActionBar();
        if (ab != null)
        {
            ab.setDisplayHomeAsUpEnabled(true);
        }
    }


    public static boolean IfAppIsRunning(Context context)
    {
        ActivityManager activityManager =
                (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> processInfos
                = activityManager.getRunningAppProcesses();
        for (int i = 0; i < processInfos.size(); i++)
        {
            if (processInfos.get(i).processName.equals(packageName))
            {
                return true;
            }
        }
        return false;
    }

    public static String GetAppCachePath() {
        File[] CacheDirList = ContextCompat.getExternalCacheDirs(FindLostThingsApplication.getContext());
        return CacheDirList[0].getAbsolutePath();
    }

    public static String GetSystemDCIMPath()
    {
        //  /storage/emulated/0/DCIM/Camera
        return Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM).getAbsolutePath();
    }

    public static String GetSystemDownloadPath(){
        return Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
    }

    public static String GetAppDataDCIMPath()
    {
        //  /storage/emulated/0/Android/data/misaka.nemesiss.com.findlostthings/files/DCIM
        File[] MountedSdcardPrefix = ContextCompat.getExternalFilesDirs(FindLostThingsApplication.getContext(), null);
        File Path = new File(MountedSdcardPrefix.length > 1 ? MountedSdcardPrefix[1] : MountedSdcardPrefix[0], Environment.DIRECTORY_DCIM);
        return Path.getAbsolutePath();
    }

    public static String GetDCIMPath() {
        int LocationFlag = FindLostThingsApplication.getAppService().GetAppSettings().getTakePhotoStoreLocation();
        switch (LocationFlag) {
            case 0:
                return GetSystemDCIMPath();
            case 1:
                return GetAppDataDCIMPath();
            default:
                return GetSystemDCIMPath();
        }
    }

    public static Uri ParseResourceIdToUri(int resId)
    {
        return Uri.parse(RESOURCE + packageName + "/" + resId);
    }

    public static void OpenCamera(Uri WangStoreImageUri, Activity CallCameraActivity)
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N)
        {
            intent.putExtra(MediaStore.EXTRA_OUTPUT, WangStoreImageUri);
        } else
        {
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
        options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
        options.setCompressionQuality(100);
        String OriginalImageSavedPath = OriginalUri.getPath();
        String CroppedPath = AppUtils.GetCroppedPath(OriginalImageSavedPath);
        return UCrop.of(OriginalUri, Uri.fromFile(new File(CroppedPath))).withOptions(options);
    }

    public static String GetCroppedPath(String originalPath)
    {
        return originalPath.substring(0, originalPath.lastIndexOf(".")) + "__CROPPED.jpg";
    }

    public static String GetTempImageName()
    {
        return System.currentTimeMillis() + ".jpg";
    }

    public static String GetPublishLostThingUploadImagePath(long UserID, String CurrentPublishUUID)
    {
        //获取当前年月
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        int mYear = cal.get(Calendar.YEAR);
        int mMonth = cal.get(Calendar.MONTH) + 1;
        String YearAndDate = String.format("%d%02d", mYear, mMonth);
        StringBuilder builder = new StringBuilder();
        builder.append("lost/upload/things/")
                .append(UserID)
                .append("/")
                .append(YearAndDate)
                .append("/")
                .append(CurrentPublishUUID)
                .append("/");
        return builder.toString();
    }

    public static String GetUploadRealPersonIdentifyImagePath(long UserID) {
        return "lost/upload/idvalidate/" + UserID + "/";
    }

    public static String[] GetPathSha1Slices(String ImagePath)
    {
        String Sha1Result = Sha1Encode(ImagePath);
        String[] result = new String[10];
        for (int i = 0; i < 10; i++)
        {
            result[i] = Sha1Result.substring(i, i + 4);
        }
        return result;
    }

    public static String Sha1Encode(String content)
    {
        byte[] hash;
        try
        {
            hash = MessageDigest.getInstance("SHA-1").digest(content.getBytes("UTF-8"));
        } catch (Exception e)
        {
            throw new RuntimeException("NoSuchAlgorithmException", e);
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash)
        {
            if ((b & 0xFF) < 0x10)
            {
                hex.append("0");
            }
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }

    public static List<String> GetAllUploadObjectKeys(List<Uri> ImageUriList, long UserID, String CurrentPublishUUID)
    {
        List<File> files = GetAllUploadObjectOriginalFilePtr(ImageUriList);
        List<String> allFileNames = new ArrayList<>();
        for (File f : files)
        {
            allFileNames.add(f.getName());
        }
        String UploadObjKey = GetPublishLostThingUploadImagePath(UserID, CurrentPublishUUID);

        String[] objKeySha1Slices = GetPathSha1Slices(UploadObjKey);
        List<String> result = new ArrayList<>();

        for (int i = 0; i < allFileNames.size(); i++)
        {
            result.add(UploadObjKey + objKeySha1Slices[i] + "-" + allFileNames.get(i));
        }
        return result;
    }

    public static List<File> GetAllUploadObjectOriginalFilePtr(List<Uri> ImageUriList)
    {
        List<File> files = new ArrayList<>();
        for (int i = 0; i < ImageUriList.size(); i++)
        {
            files.add(new File(ImageUriList.get(i).getPath()));
        }
        return files;
    }

    public static List<String> GetAllUploadObjectOriginalFilePath(List<Uri> ImageUriList)
    {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < ImageUriList.size(); i++)
        {
            result.add(ImageUriList.get(i).getPath());
        }
        return result;
    }
    public static String getAndroidId(Context context)
    {
        return Settings.System.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean IfNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static String SplitFileName(String AbsolutePath) {
        return new File(AbsolutePath).getName();
    }

    public static boolean CommonResponseOK(CommonResponse re){
        return re!=null && re.getStatusCode() == 0;
    }


    public static void InstallApk(String apkPath)
    {
        Context context = FindLostThingsApplication.getContext();
        File apkFile = new File(apkPath);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",apkFile);
            intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);
    }
}
