package misaka.nemesiss.com.findlostthings.Activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.COSXMLDownloadTask;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.UserInformation;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.StorageBucket.BucketFileOperation;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ValidateGivenActivity extends FindLostThingsActivity {

    public static final int CONFIRM_GIVEN = 1005;
    public static final int REPORT_NOT_VALID = 1006;

    @BindView(R.id.ValidateIdentityImage)
    ImageView ValidateImageIdentity;
    @BindView(R.id.ValidateQQField)
    TextView QQField;
    @BindView(R.id.ValidateWxField)
    TextView WxField;
    @BindView(R.id.ValidateMobileField)
    TextView MobileField;
    @BindView(R.id.ValidateEmailField)
    TextView EmailField;
    @BindView(R.id.NotValidReport)
    Button NotValid;
    @BindView(R.id.ConfirmReturn)
    Button ConfirmReturn;
    @BindView(R.id.ValidateGivenToolbar)
    Toolbar toolbar;

    private UserInformation CurrentValidateUserInfo;
    private Uri PreviewImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_validate_given);
        ButterKnife.bind(this);
        AppUtils.ToolbarShowReturnButton(ValidateGivenActivity.this,toolbar);

        CurrentValidateUserInfo = (UserInformation) getIntent().getSerializableExtra("UserInformation");
        LoadValidateUserInfo();
    }

    private void LoadValidateUserInfo() {
        TextView[] Value = {QQField, WxField, MobileField, EmailField};
        String QQ = CurrentValidateUserInfo.getQQ();
        String Wx = CurrentValidateUserInfo.getWxID();
        String Mobile = CurrentValidateUserInfo.getPhoneNumber();
        String Email = CurrentValidateUserInfo.getEmail();
        String[] Key = {QQ, Wx, Mobile, Email};

        for (int i = 0; i < Key.length; i++) {
            if (!TextUtils.isEmpty(Key[i])) {
                Value[i].setText(Key[i]);
            }
        }
        String str = CurrentValidateUserInfo.getRealPersonIdentity();
        String[] identityImgList = new Gson().fromJson(str, new TypeToken<String[]>() {
        }.getType());
        if (identityImgList.length > 0) {
            LoadIdentityImage(identityImgList[0]);
        }

    }

    private void LoadIdentityImage(String IdentityCosObjKey) {

        String fn = AppUtils.SplitFileName(IdentityCosObjKey);
        String path = AppUtils.GetAppCachePath() + "/" + fn;
        COSXMLDownloadTask task = BucketFileOperation.DownloadFile(path, IdentityCosObjKey);
        task.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                Log.d("ValidateGivenActivity", "下载身份信息成功，准备加载。" + path);
                PreviewImageUri = Uri.fromFile(new File(path));
                runOnUiThread(() -> {
                    Glide.with(ValidateGivenActivity.this)
                            .load(PreviewImageUri)
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(ValidateImageIdentity);
                });
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Toast.makeText(ValidateGivenActivity.this, "加载认领者的身份认证信息失败" + serviceException.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @OnClick({R.id.NotValidReport})
    public void HandleReportPersonNotValid(View v) {
        // 目前服务器还没有写好举报一个用户冒领行为的逻辑，所以考虑先把这个用户的信息持久化到一个明显的地方，报告给丢失者。
        String savedPath = "/sdcard/report.json";
        try (FileWriter fw = new FileWriter(new File(savedPath))) {
            fw.write(CurrentValidateUserInfo.ToJson());
            Toast.makeText(ValidateGivenActivity.this, "已将此人的用户信息保存到" + savedPath, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent();
            intent.putExtra("Action", REPORT_NOT_VALID);
            setResult(Activity.RESULT_OK, intent);
            runOnUiThread(() -> {
                ConfirmReturn.setEnabled(false);
                ConfirmReturn.setText("已禁止确认归还");
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.ConfirmReturn})
    public void HandleConfirmReturn(View v) {
        Intent intent = new Intent();
        intent.putExtra("Action", CONFIRM_GIVEN);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @OnClick({R.id.ValidateIdentityImage})
    public void HandleIdentityImagePreview (View v) {
        if(PreviewImageUri != null ){
            Intent intent = new Intent(ValidateGivenActivity.this,PreviewSelectedImageActivity.class);
            intent.putExtra("PreviewImageUri",PreviewImageUri);
            intent.putExtra("IsDisableDelete",true);
            intent.putExtra("IsNormalPreview",true);
            startActivity(intent);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home : {
                finish();
                break;
            }
        }
        return true;
    }
}
