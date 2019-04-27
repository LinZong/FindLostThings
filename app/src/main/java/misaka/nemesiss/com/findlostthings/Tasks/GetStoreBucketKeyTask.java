package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Response.GetStoreBucketKeyResponse;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class GetStoreBucketKeyTask extends CustomPostExecuteAsyncTask<Void, Void, GetStoreBucketKeyResponse> {

    private OkHttpClient okHttpClient;
    String EncryptedAccessToken;

    long SnowflakeID = FindLostThingsApplication.getUserService().GetUserID();

    public GetStoreBucketKeyTask(TaskPostExecuteWrapper<GetStoreBucketKeyResponse> DoInPostExecute) {
        super(DoInPostExecute);
        try
        {
            EncryptedAccessToken = QQAuthCredentials.GetEncryptedAccessToken();
        } catch (InvalidKeyException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
        catch (IllegalArgumentException e){
            e.printStackTrace();
        }
    }

    @Override
    protected GetStoreBucketKeyResponse doInBackground(Void... voids) {
        try {
            Request request = new Request.Builder()
                    .url(APIDocs.FullGetStoreBucketKey)
                    .addHeader("actk", EncryptedAccessToken)
                    .addHeader("userid", String.valueOf(SnowflakeID))
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseData = response.body().string();
                Gson gson = new Gson();
                Context ctx = FindLostThingsApplication.getContext();

                GetStoreBucketKeyResponse getStoreBucketKeyResponse = gson.fromJson(responseData, GetStoreBucketKeyResponse.class);
                SharedPreferences.Editor editor = ctx.getSharedPreferences("BucketKeyInfo", MODE_PRIVATE).edit();
                editor.putInt("StatusCode", getStoreBucketKeyResponse.getStatusCode());
                editor.putString("FullBucketName", getStoreBucketKeyResponse.getFullBucketName());
                editor.putString("Region", getStoreBucketKeyResponse.getRegion());
                editor.putLong("ExpiredTime", getStoreBucketKeyResponse.getResponse().getExpiredTime());
                editor.putString("Expiration", getStoreBucketKeyResponse.getResponse().getExpiration());
                editor.putString("RequestId", getStoreBucketKeyResponse.getResponse().getRequestId());
                editor.putString("Token", getStoreBucketKeyResponse.getResponse().getCredentials().getToken());
                editor.putString("TmpSecretId", getStoreBucketKeyResponse.getResponse().getCredentials().getTmpSecretId());
                editor.putString("TmpSecretKey", getStoreBucketKeyResponse.getResponse().getCredentials().getTmpSecretKey());
                editor.apply();

                return getStoreBucketKeyResponse;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        okHttpClient = new OkHttpClient.Builder().connectTimeout(4500, TimeUnit.MILLISECONDS).build();
    }

}
