package misaka.nemesiss.com.findlostthings.Activity;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.CosXmlServiceConfig;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.model.object.GetObjectACLRequest;
import com.tencent.cos.xml.model.object.GetObjectACLResult;
import com.tencent.cos.xml.transfer.*;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.Services.StorageBucket.BucketInfo;
import misaka.nemesiss.com.findlostthings.Services.StorageBucket.CustomCredentialProvider;

import java.io.File;

public class TryDoDownload extends AppCompatActivity {


    private QCloudCredentialProvider credentialProvider;
    private CosXmlService cosXmlService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_do_download);
        ButterKnife.bind(this);
        credentialProvider = new CustomCredentialProvider("actkRelax","36767411659079680");

        CosXmlServiceConfig cosXmlServiceConfig = new CosXmlServiceConfig.Builder()
                .setAppidAndRegion(BucketInfo.AppID,BucketInfo.Region)
                .setDebuggable(true)
                .isHttps(true)
                .builder();

        cosXmlService = new CosXmlService(TryDoDownload.this, cosXmlServiceConfig, credentialProvider);

    }

    private void TryDownload()
    {

        TransferConfig transferConfig = new TransferConfig.Builder().build();
        TransferManager transferManager = new TransferManager(cosXmlService,transferConfig);

        String bucketName = "nemesiss";
        String cosPath = "/lost/upload/things/36767411659079680/201904/C6E131A9-E8C9-4223-9D0B-E92AD01580D0/068a-1555695849005.jpg";

        String savedDirPath = "/sdcard/";
        COSXMLDownloadTask cosxmlDownloadTask = transferManager.download(getApplicationContext(), bucketName, cosPath, savedDirPath, null);
//设置下载进度回调
        cosxmlDownloadTask.setCosXmlProgressListener(new CosXmlProgressListener() {
            @Override
            public void onProgress(long complete, long target) {
                float progress = 1.0f * complete / target * 100;
                Log.d("TEST",  String.format("progress = %d%%", (int)progress));
            }
        });
//设置返回结果回调
        cosxmlDownloadTask.setCosXmlResultListener(new CosXmlResultListener() {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result) {
                COSXMLDownloadTask.COSXMLDownloadTaskResult cOSXMLDownloadTaskResult = (COSXMLDownloadTask.COSXMLDownloadTaskResult)result;
                Log.d("TEST",  "Success: " + cOSXMLDownloadTaskResult.printResult());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException) {
                Log.d("TEST",  "Failed: " + (exception == null ? serviceException.getMessage() : exception.toString()));
            }
        });
//设置任务状态回调, 可以查看任务过程
        cosxmlDownloadTask.setTransferStateListener(new TransferStateListener() {
            @Override
            public void onStateChanged(TransferState state) {
                Log.d("TEST", "Task state:" + state.name());
            }
        });
    }

    @OnClick({R.id.TestDownload})
    public void HandleDownload(View v) {
        TryDownload();
    }
}
