package misaka.nemesiss.com.findlostthings.Tasks;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Response.SchoolInfoResponse;
import misaka.nemesiss.com.findlostthings.Services.User.APIDocs;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

public class GetSupportSchoolsTask extends CustomPostExecuteAsyncTask<Void,Void, SchoolInfoResponse> {
    private OkHttpClient okHttpClient;
    public GetSupportSchoolsTask(TaskPostExecuteWrapper<SchoolInfoResponse> DoInPostExecute) {
        super(DoInPostExecute);
    }
    @Override
    protected SchoolInfoResponse doInBackground(Void... voids) {
        try {
            Request request = new Request.Builder()
                    .url(APIDocs.FullSchoolInfo)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()){
                String responseData = response.body().string();
                Gson gson = new Gson();
                SchoolInfoResponse schoolInfoRespose = gson.fromJson(responseData, SchoolInfoResponse.class);
                return schoolInfoRespose;
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
