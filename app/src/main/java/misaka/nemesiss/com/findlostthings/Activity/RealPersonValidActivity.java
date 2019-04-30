package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLDownloadTask;
import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.StorageBucket.BucketFileOperation;
import misaka.nemesiss.com.findlostthings.Tasks.UpdateUserInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.io.*;
import java.util.List;

public class RealPersonValidActivity extends AppCompatActivity {

    @BindView(R.id.RealPersonValidToolbar)
    Toolbar toolbar;
    @BindView(R.id.IdentifyDescription)
    TextView IdentifyDescription;
    @BindView(R.id.IdentifyImagePreview)
    ImageView IdentifyImagePreview;
    @BindView(R.id.TakeIdentifyPhotoButton)
    ConstraintLayout TakeIdentifyPhotoButton;
    @BindView(R.id.TakeIdentifyPhotoButtonTips)
    TextView TakeIdentifyPhotoButtonTips;
    @BindView(R.id.IdentifyStatus)
    TextView IdentifyStatus;
    @BindView(R.id.UploadImageButton)
    Button UploadImageButton;

    private UserInformation userInformation;
    private int RealPersonValidStatus;
    private String LocalImagePath;
    private String[] CosImagePath;
    private COSXMLDownloadTask DownloadImageTask;
    private boolean IsUploadingInfo = false;
    private boolean IsReturnFromCamera = false;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_person_valid);
        ButterKnife.bind(this);
        IdentifyImagePreview.setDrawingCacheEnabled(false);
        IdentifyImagePreview.setWillNotCacheDrawing(true);
        sp = FindLostThingsApplication.getContext().getSharedPreferences("PersistActivityState",MODE_PRIVATE);

        CosImagePath = new String[1];
        AppUtils.ToolbarShowReturnButton(RealPersonValidActivity.this,toolbar);

        RestoreActivityState();
        InitComponents();
    }


    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onResume() {
        super.onResume();
        RestoreActivityState();
    }


    private void PersistActivityState() {
        new Thread(() -> {
            String str = new Gson().toJson(new ActivityState(LocalImagePath,true),ActivityState.class);
            String cache = AppUtils.GetAppCachePath();
            File file = new File(new File(cache),"RealPersonValidState.json");
            try {
                FileWriter fw = new FileWriter(file);
                fw.write(str);
                fw.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }).start();
    }

    private void RestoreActivityState() {
        String cache = AppUtils.GetAppCachePath();
        File file = new File(new File(cache),"RealPersonValidState.json");
        if(file.exists()) {
            try {
                FileReader fr = new FileReader(file);
                BufferedReader br = new BufferedReader(fr);
                StringBuilder sb = new StringBuilder();
                String temp;
                while (!TextUtils.isEmpty((temp = br.readLine()))) {
                    sb.append(temp);
                }
                br.close();
                fr.close();
                String str = sb.toString();
                ActivityState as = new Gson().fromJson(str,ActivityState.class);
                LocalImagePath = as.SavedLocalImagePath;
                IsReturnFromCamera = as.IsReturnFromCamera;
                Log.d("RealPersonValidActivity","恢复Activity状态成功!");
                if(IsReturnFromCamera) {
                    UploadImageButton.setVisibility(View.VISIBLE);
                    UploadImageButton.setText("上传照片");
                    UploadImageButton.setEnabled(true);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else Log.d("RealPersonValidActivity","不存在Activity状态!!");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("RealPersonValidActivity","活动被Destroy!");
    }

    //针对个别低配置机型，切到照相机拍照后Activity被回收，且不调用onSaveInstanceState的情况，采用写配置文件的方式强行持久化Activity相关信息。

    class ActivityState {
        private String SavedLocalImagePath;
        private boolean IsReturnFromCamera;
        public ActivityState(String savedLocalImagePath, boolean isReturnFromCamera) {
            SavedLocalImagePath = savedLocalImagePath;
            IsReturnFromCamera = isReturnFromCamera;
        }
    }

    private void ClearRealPersonValidActivityState() {
            String cache = AppUtils.GetAppCachePath();
            File file = new File(new File(cache), "RealPersonValidState.json");
            if (file.exists()) {
                file.delete();
            }
    }

    private void InitComponents() {

        userInformation = FindLostThingsApplication.getUserService().getMyProfile();
        RealPersonValidStatus = userInformation.getRealPersonValid();
        IdentifyDescription.setText("请上传实名认证信息。");

        if(!IsReturnFromCamera) {
            String ValidImageUrlJson = userInformation.getRealPersonIdentity();
            if(!TextUtils.isEmpty(ValidImageUrlJson)) {
                Gson gson = new Gson();
                List<String> ValidImageUrlList = gson.fromJson(ValidImageUrlJson,new TypeToken<List<String>>(){}.getType());
                if(!ValidImageUrlList.isEmpty()) {

                    IdentifyDescription.setText("实名认证信息已上传，请等待认证通过。");

                    //TODO 调用存储桶下载该文件，缓存到本地打开.
                    String CosObjectKey = ValidImageUrlList.get(0);

                    //  /storage/emulated/0/Android/data/com.example.myapplication/cache
                    String[] namez = CosObjectKey.split("/");

                    LocalImagePath = AppUtils.GetAppCachePath() + "/" + namez[namez.length - 1];
                    File file = new File(LocalImagePath);
                    if(file.exists()) {
                        // 不需要启动下载，直接加载
                        Glide.with(RealPersonValidActivity.this).load(Uri.fromFile(file)).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(IdentifyImagePreview);
                    }
                    else {
                        DownloadImageTask = BucketFileOperation.DownloadFile(LocalImagePath,CosObjectKey);
                        DownloadImageTask.setCosXmlResultListener(DownloadTaskResultListener);
                    }
                }
            }
        }
        else {
            Glide.with(RealPersonValidActivity.this).load(LocalImagePath).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(IdentifyImagePreview);
            IsReturnFromCamera = true;
        }

        switch (RealPersonValidStatus) {
            case 0:
                IdentifyStatus.setText("未认证");
                break;
            case 1:
                // 一般情况下1是不会出现这个界面的。如果有人通过手段弄出了这个界面，那也需要正常的Handle这个case。
                IdentifyStatus.setText("已认证");
                TakeIdentifyPhotoButton.setVisibility(View.GONE);
                break;
            case 2:
                IdentifyStatus.setText("认证不通过");
                IdentifyDescription.setText("实名认证信息未通过审核，请重新上传。");
                break;
        }
    }

    private CosXmlResultListener DownloadTaskResultListener = new CosXmlResultListener() {
        @Override
        public void onSuccess(CosXmlRequest request, CosXmlResult result) {
            Log.d("RealPersonValidActivity","用户已经上传的身份认证图片下载完成。准备加载到ImageView。");
            runOnUiThread(() -> Glide.with(RealPersonValidActivity.this).load(Uri.fromFile(new File(LocalImagePath))).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(IdentifyImagePreview));
        }

        @Override
        public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
            Log.d("RealPersonValidActivity","用户已经上传的身份认证图片下载失败。" + serviceException.getMessage());
            Toast.makeText(RealPersonValidActivity.this,"身份认证图片下载失败!", Toast.LENGTH_SHORT).show();
        }
    };


    private CosXmlResultListener UploadTaskResultListener = new CosXmlResultListener() {
        @Override
        public void onSuccess(CosXmlRequest request, CosXmlResult result) {
            Log.d("RealPersonValidActivity","用户身份认证图片上传完成!");
            runOnUiThread(() -> {
                UploadImageButton.setText("上传完成");
                UploadImageButton.setEnabled(false);
            });
            UpdateUserInfo();
        }

        @Override
        public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
            runOnUiThread(() -> {
                UploadImageButton.setText("上传失败");
            });
            IsUploadingInfo = false;
            Log.d("RealPersonValidActivity","用户身份认证图片上传失败!" + exception.errorMessage);
            Toast.makeText(RealPersonValidActivity.this,"用户身份认证图片上传失败!" + exception.errorMessage, Toast.LENGTH_SHORT).show();
        }
    };

    private CosXmlProgressListener UploadTaskProgressListener = new CosXmlProgressListener() {
        @Override
        public void onProgress(long complete, long target) {
            runOnUiThread(() -> {
                int Percent = (int)(complete*100/target);
                UploadImageButton.setText("正在上传 ("+Percent+")%");
            });
        }
    };



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(TextUtils.isEmpty(LocalImagePath)) {
            RestoreActivityState();
        }
        switch (resultCode) {
            case Activity.RESULT_OK:{
                switch (requestCode){
                    case AppUtils.TYPE_CAMERA:{
                        // TODO 处理相机返回的照片。
                        runOnUiThread(() -> {
                            Log.d("RealPersonValidActivity","即将加载图片 : "+ LocalImagePath);
                            Glide.with(RealPersonValidActivity.this).load(Uri.fromFile(new File(LocalImagePath))).diskCacheStrategy(DiskCacheStrategy.NONE).skipMemoryCache(true).into(IdentifyImagePreview);
                            UploadImageButton.setVisibility(View.VISIBLE);
                            UploadImageButton.setText("上传照片");
                            UploadImageButton.setEnabled(true);
                        });
                        break;
                    }
                    case PickupImageActivity.PREVIEW_ACTIVITY :{
                        ClearRealPersonValidActivityState();
                        boolean shouldDelete = data.getBooleanExtra("ShouldDeleteImage", false);
                        if(shouldDelete) {
                            runOnUiThread(() -> {
                                IdentifyImagePreview.setImageDrawable(null);
                                LocalImagePath = null;
                                UploadImageButton.setVisibility(View.GONE);
                            });
                        }
                        break;
                    }
                }
                break;
            }
        }
    }
    @OnClick({R.id.UploadImageButton})
    public void PrepareUploadIdentifyImage(View v) {
        try {
            UploadIdentifyImage(LocalImagePath);
        } catch (IOException e) {
            Log.d("RealPersonValidActivity",e.getMessage());
            e.printStackTrace();
        }
    }


    private void UploadIdentifyImage(String FileLocation) throws IOException {
        IsUploadingInfo = true;
        long UserID = FindLostThingsApplication.getUserService().GetUserID();
        String path = AppUtils.GetUploadRealPersonIdentifyImagePath(UserID);
        File file = new File(FileLocation);
        if(!file.exists()) {
            throw new IOException("待上传文件不存在!");
        }
        String ObjKey = path + file.getName();
        CosImagePath[0] = ObjKey;

        COSXMLUploadTask task = BucketFileOperation.UploadFile(FileLocation,ObjKey);
        task.setCosXmlResultListener(UploadTaskResultListener);
        task.setCosXmlProgressListener(UploadTaskProgressListener);
    }

    @OnClick({R.id.TakeIdentifyPhotoButton})
    public void CallCameraToTakePhoto(View v) {

        String cachePath = AppUtils.GetAppCachePath();
        LocalImagePath = cachePath + "/identity.jpg";
        //解决一些低内存设备来不及保存Activity状态就被杀掉，回来没有拍照路径的问题。
        IsReturnFromCamera = true;
        File file = new File(LocalImagePath);
        PersistActivityState();
        AppUtils.OpenCamera(Uri.fromFile(file),RealPersonValidActivity.this);
    }


    @OnClick({R.id.IdentifyImagePreview})
    public void EnterPreview(View v) {
        if(!TextUtils.isEmpty(LocalImagePath)) {
            Intent it = new Intent(RealPersonValidActivity.this, PreviewSelectedImageActivity.class);
            it.putExtra("PreviewImageUri",Uri.fromFile(new File(LocalImagePath)));
            it.putExtra("IsNormalPreview",true);
            startActivityForResult(it,PickupImageActivity.PREVIEW_ACTIVITY);
        }
    }

    @Override
    public void onBackPressed() {
        HandleBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:{
                HandleBackPressed();
                break;
            }
        }
        return true;
    }

    private void HandleBackPressed() {
        if(IsUploadingInfo) {
            Toast.makeText(RealPersonValidActivity.this,"正在上传实名认证信息，请等待程序上传完成。", Toast.LENGTH_SHORT).show();
        }
        else finish();
    }

    private void UpdateUserInfo() {
        // TODO 告知后端服务器，用户的身份认证信息有更新。
        userInformation.setRealPersonValid(0);

        Gson gson = new Gson();
        String IdentityUrl = gson.toJson(CosImagePath,new TypeToken<String[]>(){}.getType());
        userInformation.setRealPersonIdentity(IdentityUrl);

        new UpdateUserInformationAsyncTask((result) -> {
            IsUploadingInfo = false;
            if(result!=null) {
                int status = result.getStatusCode();
                if(status != 0) {
                    IdentifyStatus.setText("未认证");
                    IdentifyDescription.setText("实名认证信息已上传，请等待认证通过。");
                    Toast.makeText(RealPersonValidActivity.this,"更新用户实名认证状态失败", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(RealPersonValidActivity.this,"更新用户实名认证状态成功", Toast.LENGTH_SHORT).show();
                    RealPersonValidActivity.this.finish();
                }
            }
            else Toast.makeText(RealPersonValidActivity.this,"更新用户实名认证状态失败, 无网络。", Toast.LENGTH_SHORT).show();
        }).execute(userInformation);
    }



}
