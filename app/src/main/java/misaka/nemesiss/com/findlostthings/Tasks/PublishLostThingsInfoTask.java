package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import android.content.SharedPreferences;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Response.CommonResponse;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import okhttp3.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;

public class PublishLostThingsInfoTask extends CustomPostExecuteAsyncTask<LostThingsInfo, Void, CommonResponse>
{
    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    private UserService userService = FindLostThingsApplication.getUserService();
    long SnowflakeID = userService.GetUserID();


    public PublishLostThingsInfoTask(TaskPostExecuteWrapper<CommonResponse> DoInPostExecute)
    {
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
    }

    @Override
    public CommonResponse doInBackground(LostThingsInfo... lostThingsInfos)
    {
        try
        {
            LostThingsInfo lostThingsInfo = lostThingsInfos[0];
            Gson gson = new Gson();
            String result = gson.toJson(lostThingsInfo, LostThingsInfo.class);
            RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), result);
            Request request = new Request.Builder()
                    .url(APIDocs.FullLostThingsInfo)
                    .addHeader("actk", EncryptedAccessToken)
                    .addHeader("userid", String.valueOf(SnowflakeID))
                    .post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful())
            {
                String responseData = response.body().string();
                CommonResponse resp = gson.fromJson(responseData, CommonResponse.class);
                return resp;
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        okHttpClient = new OkHttpClient.Builder().connectTimeout(4500, TimeUnit.MILLISECONDS).build();
    }
}
