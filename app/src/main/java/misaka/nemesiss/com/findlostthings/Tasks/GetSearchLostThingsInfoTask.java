package misaka.nemesiss.com.findlostthings.Tasks;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.LostThingsInfo;
import misaka.nemesiss.com.findlostthings.Model.SearchLostThingsInfo;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Services.QQAuth.QQAuthCredentials;
import misaka.nemesiss.com.findlostthings.Services.User.UserService;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.modelmapper.TypeToken;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GetSearchLostThingsInfoTask extends CustomPostExecuteAsyncTask<SearchLostThingsInfo,Void, List<LostThingsInfo>>
{
    private OkHttpClient okHttpClient;
    private UserService userService = FindLostThingsApplication.getUserService();
    long SnowflakeID = userService.GetUserID();

    String EncryptedAccessToken = null;

    public GetSearchLostThingsInfoTask(TaskPostExecuteWrapper<List<LostThingsInfo>> DoInPostExecute) {
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
    protected List<LostThingsInfo> doInBackground(SearchLostThingsInfo...searchLostThingsInfo) {
        try {
            Request request = new Request.Builder()
                    .url(APIDocs.FullThingsSearch)
                    .addHeader("actk", EncryptedAccessToken)
                    .addHeader("userid",String.valueOf(SnowflakeID))
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if(response.isSuccessful()){
                String responseData = response.body().string();
                Gson gson = new Gson();
                List<LostThingsInfo> resp = gson.fromJson(responseData, new TypeToken<List<LostThingsInfo>>(){}.getType());
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
