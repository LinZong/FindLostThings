package misaka.nemesiss.com.findlostthings.InfrastructureExtension.TasksExtensions;

public interface TaskPostExecuteWrapper<TTaskReturn>
{
    void DoOnPostExecute(TTaskReturn TaskRet);
}
