package misaka.nemesiss.com.findlostthings.Tasks;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.VersionInfo;
import misaka.nemesiss.com.findlostthings.Services.APIDocs;
import misaka.nemesiss.com.findlostthings.Utils.AppUtils;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class CheckNewVersionTask extends CustomPostExecuteAsyncTask<Void,Void, VersionInfo>
{

    private OkHttpClient client;

    public CheckNewVersionTask(TaskPostExecuteWrapper<VersionInfo> DoInPostExecute)
    {
        super(DoInPostExecute);
    }

    @Override
    protected VersionInfo doInBackground(Void... voids)
    {
        Request request = new Request.Builder()
                                    .url(APIDocs.FullCheckUpdate)
                                    .build();
        try
        {
            Response resp = client.newCall(request).execute();
            if(resp.isSuccessful())
            {
                if (resp.body() != null)
                {
                    String jsonBody = resp.body().string();
                    if(jsonBody.equals("null"))
                    {
                        return null;
                    }
                    return new Gson().fromJson(jsonBody,VersionInfo.class);
                }
                return null;
            }
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPreExecute()
    {
        super.onPreExecute();
        client = AppUtils.GetOkHttpClient().build();
    }

}
