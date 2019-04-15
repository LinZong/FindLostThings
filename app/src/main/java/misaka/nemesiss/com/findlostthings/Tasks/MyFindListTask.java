package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Response.MyPublishListResponse;
import misaka.nemesiss.com.findlostthings.Services.User.APIDocs;
import misaka.nemesiss.com.findlostthings.Services.User.LostThingsInfo;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class MyFindListTask extends CustomPostExecuteAsyncTask<Void,Void, List<LostThingsInfo>>
{
    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    Context ctx = FindLostThingsApplication.getContext();
    SharedPreferences preferences = ctx.getSharedPreferences("userIDData", MODE_PRIVATE);
    long SnowflakeID = preferences.getLong("Snowflake ID", 0);

    public MyFindListTask(TaskPostExecuteWrapper<List<LostThingsInfo>> DoInPostExecute) {
        super(DoInPostExecute);
        EncryptedAccessToken= APIDocs.encryptionAccessToken();
    }
    @Override
    protected List<LostThingsInfo> doInBackground(Void... voids) {
        try {
            Request request = new Request.Builder()
                    .url(APIDocs.FullMyFindList)
                    .addHeader("actk",EncryptedAccessToken)
                    .addHeader("userid",String.valueOf(SnowflakeID))
                    .build();

            Response response1 = okHttpClient.newCall(request).execute();
            if (response1.isSuccessful()){
                String responseData1 = response1.body().string();
                Gson gson = new Gson();
                MyPublishListResponse resp = gson.fromJson(responseData1, MyPublishListResponse.class);
                return resp.getLostThingsInfos();
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
