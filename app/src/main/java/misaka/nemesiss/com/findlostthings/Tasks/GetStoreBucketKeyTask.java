package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Response.GetStoreBucketKeyResponse;
import misaka.nemesiss.com.findlostthings.Services.User.APIDocs;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class GetStoreBucketKeyTask extends CustomPostExecuteAsyncTask<Void,Void, GetStoreBucketKeyResponse> {

    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    Context ctx = FindLostThingsApplication.getContext();
    SharedPreferences preferences = ctx.getSharedPreferences("userIDData", MODE_PRIVATE);
    long SnowflakeID = preferences.getLong("Snowflake ID", 0);

    public GetStoreBucketKeyTask (TaskPostExecuteWrapper<GetStoreBucketKeyResponse> DoInPostExecute) {
        super(DoInPostExecute);
        APIDocs.encryptionAccessToken();
    }
    @Override
    protected GetStoreBucketKeyResponse doInBackground(Void... voids) {
        try {
            Request request = new Request.Builder()
                    .url(APIDocs.FullGetStoreBucketKey)
                    .addHeader(EncryptedAccessToken, String.valueOf(SnowflakeID))
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()){
                String responseData = response.body().string();
                Gson gson = new Gson();
               GetStoreBucketKeyResponse getStoreBucketKeyRespose = gson.fromJson(responseData, GetStoreBucketKeyResponse.class);
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
