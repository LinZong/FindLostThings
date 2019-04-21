package misaka.nemesiss.com.findlostthings.Activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import com.tencent.cos.xml.CosXmlService;
import com.tencent.cos.xml.exception.CosXmlClientException;
import com.tencent.cos.xml.exception.CosXmlServiceException;
import com.tencent.cos.xml.listener.CosXmlProgressListener;
import com.tencent.cos.xml.listener.CosXmlResultListener;
import com.tencent.cos.xml.model.CosXmlRequest;
import com.tencent.cos.xml.model.CosXmlResult;
import com.tencent.cos.xml.transfer.*;
import com.tencent.qcloud.core.auth.QCloudCredentialProvider;
import com.tencent.qcloud.core.auth.ShortTimeCredentialProvider;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.R;
import misaka.nemesiss.com.findlostthings.StorageBucket.CustomCredentialProvider;

import java.io.File;

public class TryUploadFilesActivity extends FindLostThingsActivity
{

    private QCloudCredentialProvider credentialProvider;
    private CosXmlService cosXmlService;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_try_upload_files);
        credentialProvider = new CustomCredentialProvider();
        cosXmlService = new CosXmlService(TryUploadFilesActivity.this, FindLostThingsApplication.getCosXmlServiceConfig(), credentialProvider);
    }

    private void TryDoUpload()
    {
        TransferConfig transferConfig = new TransferConfig.Builder().build();
        TransferManager transferManager = new TransferManager(cosXmlService, transferConfig);

        String bucketName = "nemesiss";
        String cosPath = "lost/upload/things/relax.png";
        String localPath = "/sdcard/relax.png";
        String uploadId = null;
        File file = new File(localPath);
        if(!file.exists())
        {
            return;
        }
        COSXMLUploadTask cosxmlUploadTask = transferManager.upload(bucketName, cosPath, localPath, uploadId);
        cosxmlUploadTask.setCosXmlProgressListener(new CosXmlProgressListener()
        {
            @Override
            public void onProgress(long complete, long target)
            {
                float progress = 1.0f * complete / target * 100;
                Log.d("TEST", String.format("progress = %d%%", (int) progress));
            }
        });

        cosxmlUploadTask.setCosXmlResultListener(new CosXmlResultListener()
        {
            @Override
            public void onSuccess(CosXmlRequest request, CosXmlResult result)
            {
                COSXMLUploadTask.COSXMLUploadTaskResult cOSXMLUploadTaskResult = (COSXMLUploadTask.COSXMLUploadTaskResult) result;
                Log.d("TEST", "Success: " + cOSXMLUploadTaskResult.printResult());
            }

            @Override
            public void onFail(CosXmlRequest request, CosXmlClientException exception, CosXmlServiceException serviceException)
            {
                Log.d("TEST", "Failed: " + (exception == null ? serviceException.getMessage() : exception.toString()));
            }
        });
        //设置任务状态回调, 可以查看任务过程
        cosxmlUploadTask.setTransferStateListener(new TransferStateListener()
        {
            @Override
            public void onStateChanged(TransferState state)
            {
                Log.d("TEST", "Task state:" + state.name());
            }
        });


    }

    public void HandleClickUpload(View view)
    {
        TryDoUpload();
    }
}
