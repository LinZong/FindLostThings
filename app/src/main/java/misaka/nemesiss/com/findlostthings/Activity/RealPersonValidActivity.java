package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import misaka.nemesiss.com.findlostthings.Tasks.PostUserInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Tasks.UpdateUserInformationAsyncTask;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.io.File;
import java.io.IOException;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_real_person_valid);
        ButterKnife.bind(this);
        CosImagePath = new String[1];
        AppUtils.ToolbarShowReturnButton(RealPersonValidActivity.this,toolbar);
        InitComponents();
    }

    private void InitComponents() {
        userInformation = FindLostThingsApplication.getUserService().getMyProfile();
        RealPersonValidStatus = userInformation.getRealPersonValid();


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
                DownloadImageTask = BucketFileOperation.DownloadFile(LocalImagePath,CosObjectKey);
                DownloadImageTask.setCosXmlResultListener(DownloadTaskResultListener);
            }
        }
        IdentifyDescription.setText("请上传实名认证信息。");
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
            Glide.with(RealPersonValidActivity.this).load(Uri.fromFile(new File(LocalImagePath))).into(IdentifyImagePreview);
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
        switch (resultCode) {
            case Activity.RESULT_OK:{
                switch (requestCode){
                    case AppUtils.TYPE_CAMERA:{
                        // TODO 处理相机返回的照片。
                        Glide.with(RealPersonValidActivity.this).load(Uri.fromFile(new File(LocalImagePath))).into(IdentifyImagePreview);
                        UploadImageButton.setVisibility(View.VISIBLE);
                        UploadImageButton.setText("上传照片");
                        UploadImageButton.setEnabled(true);
                        break;
                    }
                    case PickupImageActivity.PREVIEW_ACTIVITY :{
                        boolean shouldDelete = data.getBooleanExtra("ShouldDeleteImage", false);
                        if(shouldDelete) {
                           IdentifyImagePreview.setImageDrawable(null);
                           UploadImageButton.setVisibility(View.GONE);
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
        String tempFileName = AppUtils.GetTempImageName();
        LocalImagePath = cachePath + "/" + tempFileName;
        File file = new File(new File(cachePath),tempFileName);

        AppUtils.OpenCamera(Uri.fromFile(file),RealPersonValidActivity.this);
    }


    @OnClick({R.id.IdentifyImagePreview})
    public void EnterPreview(View v) {
        Intent it = new Intent(RealPersonValidActivity.this, PreviewSelectedImageActivity.class);
        it.putExtra("PreviewImageUri",Uri.fromFile(new File(LocalImagePath)));
        it.putExtra("IsNormalPreview",true);
        startActivityForResult(it,PickupImageActivity.PREVIEW_ACTIVITY);
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
