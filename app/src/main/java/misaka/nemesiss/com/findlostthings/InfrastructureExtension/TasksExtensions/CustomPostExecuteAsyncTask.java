package misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions;

import android.os.AsyncTask;

public abstract class CustomPostExecuteAsyncTask<TParam,TProgress,TResult> extends AsyncTask<TParam,TProgress,TResult>
{
    private TaskPostExecuteWrapper<TResult> wrapper;
    public CustomPostExecuteAsyncTask(TaskPostExecuteWrapper<TResult> DoInPostExecute)
    {
        wrapper = DoInPostExecute;
    }
    @Override
    protected void onPostExecute(TResult o)
    {
        super.onPostExecute(o);
        wrapper.DoOnPostExecute(o);
    }
}

