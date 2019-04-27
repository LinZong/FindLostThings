package misaka.nemesiss.com.findlostthings.Services.StorageBucket;

import com.tencent.cos.xml.transfer.COSXMLUploadTask;
import com.tencent.cos.xml.transfer.TransferConfig;
import com.tencent.cos.xml.transfer.TransferManager;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;

import java.io.File;

public class BucketFileOperation {

    public static COSXMLUploadTask UploadFile(String FileLocalPath,String CosObjectKey)
    {
        TransferConfig config = new TransferConfig.Builder().build();
        TransferManager transferManager = new TransferManager(FindLostThingsApplication.getCosXmlService(), config);
        File file = new File(FileLocalPath);
        if (!file.exists())
        {
            throw new IllegalArgumentException("待上传的文件不存在!");
        }
        COSXMLUploadTask uploadTask = transferManager.upload(BucketInfo.BucketName, CosObjectKey, FileLocalPath, null);
        return uploadTask;
    }
}
