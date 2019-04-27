package misaka.nemesiss.com.findlostthings.Fragment.Setting;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import misaka.nemesiss.com.findlostthings.BuildConfig;
import misaka.nemesiss.com.findlostthings.R;

public class AboutMeFragment extends SettingBaseFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SetSaveSettingOnDestroy(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_about_me,container,false);
        TextView version= v.findViewById(R.id.VersionCode);
        version.setText(BuildConfig.VERSION_NAME);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        SetSaveSettingOnDestroy(false);
    }
}
