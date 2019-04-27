package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Response.MyPublishListResponse;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class MyFindListTask extends CustomPostExecuteAsyncTask<Void,Void, List<LostThingsInfo>>
{
    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    private UserService userService = FindLostThingsApplication.getUserService();
    long SnowflakeID = userService.GetUserID();

    public MyFindListTask(TaskPostExecuteWrapper<List<LostThingsInfo>> DoInPostExecute) {
        super(DoInPostExecute);
        try
        {
            EncryptedAccessToken= QQAuthCredentials.GetEncryptedAccessToken();
        } catch (InvalidKeyException e)
        {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e)
        {
            e.printStackTrace();
        }
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
