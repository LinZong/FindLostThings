package misaka.nemesiss.com.findlostthings.Tasks;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Model.Response.CommonResponse;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import okhttp3.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class UpdateLostThingsInfoTask extends CustomPostExecuteAsyncTask<LostThingsInfo, Void, Integer>
{
    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    private UserService userService = FindLostThingsApplication.getUserService();
    long SnowflakeID = userService.GetUserID();


    public UpdateLostThingsInfoTask(TaskPostExecuteWrapper<Integer> DoInPostExecute)
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
    public Integer doInBackground(LostThingsInfo... lostThingsInfos)
    {
        try
        {
            LostThingsInfo lostThingsInfo = lostThingsInfos[0];
            Gson gson = new Gson();
            String result = gson.toJson(lostThingsInfo, LostThingsInfo.class);
            RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), result);
            Request request = new Request.Builder()
                    .url(APIDocs.FullThingsUpdate)
                    .addHeader("actk", EncryptedAccessToken)
                    .addHeader("userid", String.valueOf(SnowflakeID))
                    .post(requestBody).build();
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful())
            {
                String responseData = response.body().string();
                CommonResponse resp = gson.fromJson(responseData, CommonResponse.class);
                return resp.getStatusCode();
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

