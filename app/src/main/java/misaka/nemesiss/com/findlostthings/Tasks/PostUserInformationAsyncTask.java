package misaka.nemesiss.com.findlostthings.Tasks;

import android.content.Context;
import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Request.LoginAccountInfo.LoginAccountInfo;
import misaka.nemesiss.com.findlostthings.Model.Response.LoginAccountResponse;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import okhttp3.*;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;

public class PostUserInformationAsyncTask extends CustomPostExecuteAsyncTask<String,Void,LoginAccountResponse>{
    private OkHttpClient okHttpClient;
    private String EncryptedAccessToken = null;
    public PostUserInformationAsyncTask(TaskPostExecuteWrapper<LoginAccountResponse> DoInPostExecute) {
        super(DoInPostExecute);
        try
        {
            EncryptedAccessToken= QQAuthCredentials.GetEncryptedAccessToken();
        } catch (InvalidKeyException e)
        {
            cancel(false);
            e.printStackTrace();

        } catch (NoSuchAlgorithmException e)
        {
            cancel(false);
            e.printStackTrace();
        }
    }

    @Override
    public LoginAccountResponse doInBackground(String... information) {
        try {
            LoginAccountInfo info = new LoginAccountInfo();
            info.setOpenID(information[0]);
            info.setAccessToken(EncryptedAccessToken);
            info.setNickName(information[1]);
            info.setCurrentDeviceAndroidID(information[2]);
            Gson gson = new Gson();
            String result = gson.toJson(info, LoginAccountInfo.class);
            RequestBody requestBody = FormBody.create(MediaType.parse("application/json"), result);
            Request request = new Request.Builder()
                    .url(APIDocs.FullAddress)
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();

            if (response.isSuccessful()) {
                String responseData = response.body().string();
                LoginAccountResponse resp = gson.fromJson(responseData, LoginAccountResponse.class);

                return resp;
            }

        }
        catch (Exception e) {
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
