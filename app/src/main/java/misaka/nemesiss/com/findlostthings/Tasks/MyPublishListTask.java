package misaka.nemesiss.com.findlostthings.Tasks;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import okhttp3.*;
import org.modelmapper.TypeToken;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;
import static android.content.Context.MODE_PRIVATE;
public class MyPublishListTask extends CustomPostExecuteAsyncTask<Void,Void, List<LostThingsInfo>>
{
    private OkHttpClient okHttpClient;
    String EncryptedAccessToken = null;
    private UserService userService = FindLostThingsApplication.getUserService();
    long SnowflakeID = userService.GetUserID();

    public MyPublishListTask(TaskPostExecuteWrapper<List<LostThingsInfo>> DoInPostExecute) {
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
                    .url(APIDocs.FullMyPublishList)
                    .addHeader("actk",EncryptedAccessToken)
                    .addHeader("userid",String.valueOf(SnowflakeID))
                    .build();

            Response response1 = okHttpClient.newCall(request).execute();
            if (response1.isSuccessful()){
                String responseData1 = response1.body().string();
                Gson gson = new Gson();
                List<LostThingsInfo> resp = gson.fromJson(responseData1, new TypeToken<List<LostThingsInfo>>(){}.getType());
                return resp;
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
