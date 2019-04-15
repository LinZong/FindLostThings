package misaka.nemesiss.com.findlostthings.Tasks;

import com.google.gson.Gson;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.CustomPostExecuteAsyncTask;
import misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions.TaskPostExecuteWrapper;
import misaka.nemesiss.com.findlostthings.Model.Response.SchoolBuildingsResponse;
import misaka.nemesiss.com.findlostthings.Services.User.APIDocs;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.util.concurrent.TimeUnit;

public class GetSchoolBuildingsTask extends CustomPostExecuteAsyncTask<Integer,Void, SchoolBuildingsResponse> {
    private OkHttpClient okHttpClient;
    public GetSchoolBuildingsTask(TaskPostExecuteWrapper<SchoolBuildingsResponse> DoInPostExecute) {
        super(DoInPostExecute);
    }
    @Override
    protected SchoolBuildingsResponse doInBackground(Integer... IDs) {
        String ValidFullSchoolBuildings=(APIDocs.FullSchoolBuildings+IDs[0]);
        try {
            Request request = new Request.Builder()
                    .url(ValidFullSchoolBuildings)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            if (response.isSuccessful()){
                String responseData = response.body().string();
                Gson gson = new Gson();
                SchoolBuildingsResponse schoolBuildingsResponse = gson.fromJson(responseData, SchoolBuildingsResponse.class);
                return schoolBuildingsResponse;
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
