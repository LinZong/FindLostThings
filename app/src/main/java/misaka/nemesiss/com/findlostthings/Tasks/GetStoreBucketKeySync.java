package misaka.nemesiss.com.findlostthings.Tasks;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Model.Response.GetStoreBucketKeyResponse;
import misaka.nemesiss.com.findlostthings.Services.User.APIDocs;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class GetStoreBucketKeySync
{
    private String actk;
    private String userid;
    private OkHttpClient client;
    public GetStoreBucketKeySync(String encryptedActk,String UserID)
    {
        SetActkAndUserID(encryptedActk,UserID);
        client = AppUtils.GetOkHttpClient().build();
    }
    public void SetActkAndUserID(String encryptedActk,String UserID)
    {
        actk = encryptedActk;
        userid = UserID;
    }
    public GetStoreBucketKeyResponse GetTempKeySync() throws IOException
    {
        Request request = new Request.Builder()
                .url(APIDocs.FullGetStoreBucketKey)
                .addHeader("actk", actk)
                .addHeader("userid", userid)
                .build();
        Response response = client.newCall(request).execute();
        if (response.isSuccessful())
        {
            String responseData = response.body().string();
            Gson gson = new Gson();
            GetStoreBucketKeyResponse getStoreBucketKeyRespose = gson.fromJson(responseData, GetStoreBucketKeyResponse.class);
            return getStoreBucketKeyRespose;
        }
        return null;
    }
}
