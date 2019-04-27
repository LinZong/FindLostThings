package misaka.nemesiss.com.findlostthings.Services.Notifications;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.service.JPushMessageReceiver;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;

public class JPushActionResultReceiver extends JPushMessageReceiver
{

    public static final int RETRY_SET_ALIAS = 999;

    Handler HandleAliasRetry = new Handler(this::AliasResetHandler);

    private boolean AliasResetHandler(Message message)
    {
        switch (message.what)
        {
            case RETRY_SET_ALIAS:{
                String textUserId = (String) message.obj;
                JPushInterface.setAlias(FindLostThingsApplication.getContext(),1, textUserId);
                break;
            }
            default:break;
        }
        return true;
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onCheckTagOperatorResult(Context context,JPushMessage jPushMessage){
        super.onCheckTagOperatorResult(context, jPushMessage);
    }
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        super.onAliasOperatorResult(context, jPushMessage);
        int error = jPushMessage.getErrorCode();
        if(error == 0)
        {
            Log.d("PushServices","成功设置Alias。");
            FindLostThingsApplication.getUserService()
                    .PersistAlias(jPushMessage.getAlias());
        }
        else if(error == 6002 || error == 6014)
        {
            Log.d("PushServices","设置Alias出现错误，将于60s后重新设置。错误代码为。"+error);
            Message message = new Message();
            message.what = RETRY_SET_ALIAS;
            message.obj = jPushMessage.getAlias();
            HandleAliasRetry.sendMessageDelayed(message,1000*60);
        }
        else Log.d("PushServices","设置Alias出现错误，代码为。"+error);
    }

    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        //TagAliasOperatorHelper.getInstance().onMobileNumberOperatorResult(context,jPushMessage);
        super.onMobileNumberOperatorResult(context, jPushMessage);
    }
}
