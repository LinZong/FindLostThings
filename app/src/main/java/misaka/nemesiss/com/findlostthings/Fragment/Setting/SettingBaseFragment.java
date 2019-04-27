package misaka.nemesiss.com.findlostthings.Fragment.Setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import com.github.ikidou.fragmentBackHandler.FragmentBackHandler;
import misaka.nemesiss.com.findlostthings.Activity.SettingActivity;
import misaka.nemesiss.com.findlostthings.Application.FindLostThingsApplication;
import misaka.nemesiss.com.findlostthings.Model.AppSettings;
import misaka.nemesiss.com.findlostthings.Services.Common.AppService;

public abstract class SettingBaseFragment extends Fragment implements FragmentBackHandler {

    protected AppSettings appSettings;
    protected AppService appService;
    protected String SettingTitle;
    private boolean SaveSettingOnDestroy = true;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        appService = FindLostThingsApplication.getAppService();
        appSettings = appService.GetAppSettings();
    }

    @Override
    public final boolean onBackPressed() {
        ((SettingActivity)getActivity()).HandleFragmentsBack();
        return true;
    }

    public void setSettingTitle(String settingTitle) {
        if(TextUtils.isEmpty(settingTitle)){
            SettingTitle = this.getClass().getSimpleName();
        }
        else {
            SettingTitle = settingTitle;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if(SaveSettingOnDestroy) appService.SaveAppSettings();
    }

    public String getSettingTitle() {
        return SettingTitle;
    }

    protected boolean GetSaveSettingOnDestroy() {
        return SaveSettingOnDestroy;
    }
    protected void SetSaveSettingOnDestroy(boolean val) {
        SaveSettingOnDestroy = val;
    }
}
