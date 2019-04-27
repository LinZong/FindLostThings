package misaka.nemesiss.com.findlostthings.Fragment.Setting;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import misaka.nemesiss.com.findlostthings.Activity.SettingActivity;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.R;

/**
 *  什么时候需要可以跳转页面(Fragment)的设置项?（二级设置项）
 *      -- 当某个设置项较为复杂，具有若干选项可以调整时，应该引导用户进入二级页面调整给出的配置，
 *      许多常见的应用都具有二级设置项。比如QQ。
 *
 *  如何创建一个二级设置项?
 * 1. 前往AppSettings 类中新建一个所需记录的设置项的data member和对应的get，set, 并赋予此data member默认值.
 *    请确保此设置项能够以JSON的格式完成序列化/反序列化.
 *
 * 2. 编辑/新建Layout文件，用于展示二级页面的设置项.
 *
 * 3. 新建一个Fragment类，放置在此包下，继承自SettingBaseFragment。因为二级设置项视为Fragment加载，
 *    所以请同时重写展示Fragment所必须的函数，并实现监听获取用户在当前设置页的操作。
 *
 * 4. 通过appSetting对象访问先前在AppSetting类中新增的设置项以及get,set函数，
 *    通过appService实现保存设置项.
 *
 *   注意事项:
 *
 *   在通常情况下，不需要手动调用appService.SaveAppSettings(), 该方法会在离开当前Fragment时自动调用。
 *   如果有特殊需求，需要禁用此行为，请调用SetSaveSettingOnDestroy(boolean val);方法设置。
 */

public class SettingFragment extends SettingBaseFragment {


    @BindView(R.id.Setting_NotificationSwitch)
    SwitchCompat NotificationSwitch;
    private Context context = FindLostThingsApplication.getContext();
    private Unbinder unbinder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment,container,false);
        unbinder = ButterKnife.bind(this,view);
        LoadSettingToView();
        NotificationSwitch.setOnCheckedChangeListener(this::HandleNotificationSwitchChanged);
        return view;
    }

    private void HandleNotificationSwitchChanged(CompoundButton compoundButton, boolean b) {

        if(b){
            appSettings.setEnableNotification(1);
            if (JPushInterface.isPushStopped(context)) {
                JPushInterface.resumePush(context);
            }
        }
        else {
            appSettings.setEnableNotification(0);
            if (!JPushInterface.isPushStopped(context)) {
                JPushInterface.stopPush(context);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    private void LoadSettingToView() {
        int noti = appSettings.getEnableNotification();
        if(noti == 1) NotificationSwitch.setChecked(true);
        else  NotificationSwitch.setChecked(false);
    }

    @OnClick({R.id.EnterSetPhotoStoreLocation})
    public void EnterSetPhotoStoreLocation(View v) {
        SettingActivity.ToNextFragment(new PhotoSaveLocationFragment(),"照片存储位置",null);
    }
}
